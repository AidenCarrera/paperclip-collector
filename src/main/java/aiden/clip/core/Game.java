package aiden.clip.core;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.Objects;

import javax.swing.ImageIcon;

import aiden.clip.audio.SoundHandler;
import aiden.clip.input.InputHandler;
import aiden.clip.ui.GameUI;
import aiden.clip.ui.Menu;
import aiden.clip.ui.Window;
import aiden.clip.util.BufferedImageLoader;

public class Game extends Canvas implements Runnable {

    @Serial
    private static final long serialVersionUID = 1550691097823471818L;
    private static final double UPDATE_CAP = 1.0 / 165.0;

    private Thread thread;
    private boolean running = false;

    private final BufferedImageLoader loader;
    private final Handler handler;
    private GameUI gameUI;
    private final GameManager gameManager;
    private InputHandler inputHandler;
    private final double autoSaveInterval;
    private BufferedImage levelImage = null;
    private final BufferedImage dog;

    // Mouse tracking for CCD
    private int lastMouseX, lastMouseY;

    private GameState gameState = GameState.MENU;

    // Scaling factors
    private final float windowScaleX;
    private final float windowScaleY;

    // Config field now accessible anywhere
    private final ConfigManager config;

    public static void main(String[] args) {
        System.out.println("Game starting...");

        try {
            ConfigManager config = new ConfigManager();
            config.load();
            new Game(config);
            System.out.println("Game initialized successfully.");
        } catch (Throwable e) {
            e.printStackTrace();
            javax.swing.SwingUtilities.invokeLater(() -> javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Error starting game: " + e,
                    "Game Crash",
                    javax.swing.JOptionPane.ERROR_MESSAGE));
        }
    }

    public Game(ConfigManager config) {
        this.config = config;

        loader = new BufferedImageLoader();
        setLevelImage("/images/menu.png");

        // --- Detect actual screen size ---
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int actualWidth = screenSize.width;
        int actualHeight = screenSize.height;

        // --- Compute scaling factors ---
        windowScaleX = (float) actualWidth / config.displayWidth;
        windowScaleY = (float) actualHeight / config.displayHeight;

        // --- Handler ---
        handler = new Handler();

        // --- GameManager ---
        gameManager = new GameManager(handler, config, windowScaleX, windowScaleY);

        // --- GameUI ---
        gameUI = new GameUI(gameManager, config, windowScaleX, windowScaleY);
        gameManager.setGameUI(gameUI);

        // --- Input ---
        // Replace legacy Mouse with InputHandler
        this.inputHandler = new InputHandler(this, handler, config);
        this.addMouseListener(inputHandler);
        this.addMouseMotionListener(inputHandler);

        // --- Window ---
        new Window(this, config);

        // --- Audio ---
        SoundHandler.runMusic(config.musicVolume, config.soundEffectsVolume);

        // --- Load dog image ---
        dog = loader.loadImage("/images/dog.png");

        // --- Auto-save interval ---
        autoSaveInterval = config.autoSaveIntervalSeconds;

        // --- Set custom cursor ---
        // --- Set custom cursor ---
        Image cursorImg = new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/images/cursor.png")))
                .getImage();

        // Resize cursor to prevent stretching
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension bestSize = toolkit.getBestCursorSize(32, 32);

        if (bestSize.width > 0 && bestSize.height > 0) {
            BufferedImage resizedCursor = new BufferedImage(bestSize.width, bestSize.height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = resizedCursor.getGraphics();

            // Calculate dimensions to preserve aspect ratio
            int imgWidth = cursorImg.getWidth(null);
            int imgHeight = cursorImg.getHeight(null);

            if (imgWidth > 0 && imgHeight > 0) {
                double scale = Math.min((double) bestSize.width / imgWidth, (double) bestSize.height / imgHeight);
                int newWidth = (int) (imgWidth * scale);
                int newHeight = (int) (imgHeight * scale);

                g.drawImage(cursorImg, 0, 0, newWidth, newHeight, null);
            } else {
                // Fallback if dimensions unknown (shouldn't happen with ImageIcon)
                g.drawImage(cursorImg, 0, 0, bestSize.width, bestSize.height, null);
            }
            g.dispose();
            cursorImg = resizedCursor;
        }

        setCursor(toolkit.createCustomCursor(cursorImg, new Point(0, 0), "Cursor"));
    }

    // --- Game state getters/setters ---
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setLevelImage(String path) {
        levelImage = loader.loadImage(path);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public float getWindowScaleX() {
        return windowScaleX;
    }

    public float getWindowScaleY() {
        return windowScaleY;
    }

    // --- Game loop ---
    private double autoSaveTimer = 0;

    public synchronized void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            if (thread != null)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.requestFocus();
        boolean render;
        double lastTime = System.nanoTime() / 1e9;
        double unprocessedTime = 0;
        double frameTime = 0;
        int frames = 0;
        int fps;

        while (running) {
            render = false;
            double currentTime = System.nanoTime() / 1e9;
            double passedTime = currentTime - lastTime;
            lastTime = currentTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while (unprocessedTime >= UPDATE_CAP) {
                unprocessedTime -= UPDATE_CAP;
                render = true;
                tick();

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                    System.out.println(fps + " FPS");

                    if (gameState == GameState.GAME) {
                        autoSaveTimer += 1.0;
                        if (autoSaveTimer >= autoSaveInterval) {
                            gameManager.saveGame();
                            autoSaveTimer = 0;
                            System.out.println("Auto-saved game.");
                        }
                    }
                }
            }

            if (render) {
                render();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (gameState == GameState.GAME) {
            gameManager.saveGame();
            System.out.println("Game saved on exit.");
        }
        SoundHandler.close();
        stop();
    }

    private void tick() {
        inputHandler.tick();
        handler.tick();

        if (gameState == GameState.MENU) {
            int buttonIndex = 2;
            for (GameObject obj : handler.getObjects()) {
                if (obj instanceof Menu menuButton) {
                    menuButton.updatePosition((int) (config.displayWidth * windowScaleX),
                            (int) (config.displayHeight * windowScaleY),
                            buttonIndex--);
                }
            }
        }

        if (gameState == GameState.GAME) {
            gameManager.tick();

            // Handle continuous input (hover/drag) on the game thread with CCD
            int currentMouseX = inputHandler.getMouseX();
            int currentMouseY = inputHandler.getMouseY();

            // Initialize last positions if this is the first tick or they are 0 (start)
            if (lastMouseX == 0 && lastMouseY == 0) {
                lastMouseX = currentMouseX;
                lastMouseY = currentMouseY;
            }

            gameManager.checkCollisions(lastMouseX, lastMouseY, currentMouseX, currentMouseY);
            gameManager.handleUiHover(currentMouseX, currentMouseY);

            lastMouseX = currentMouseX;
            lastMouseY = currentMouseY;
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(levelImage, 0, 0, getWidth(), getHeight(), null);

        if (gameState == GameState.MENU) {
            g.drawImage(dog, (int) (960 * windowScaleX), (int) (20 * windowScaleY), null);
        } else {
            gameManager.render(g);
            if (gameUI != null) {
                gameUI.render(g);
            }
        }

        handler.render(g);
        g.dispose();
        bs.show();
    }

    public static int clamp(int var, int min, int max) {
        return Math.max(min, Math.min(max, var));
    }
}
