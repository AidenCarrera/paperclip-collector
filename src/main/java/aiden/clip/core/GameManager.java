package aiden.clip.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aiden.clip.save.SaveManager;
import aiden.clip.ui.Menu;
import aiden.clip.ui.GameUI;

public class GameManager {

    private final Handler handler;
    private final SaveManager saveManager;
    private final ConfigManager config;
    private final Random random;

    // Sub-managers
    private final SpawnManager spawnManager;
    private final UpgradeManager upgradeManager;

    // Window scaling factors
    private final float windowScaleX;
    private final float windowScaleY;

    // Game state
    private int clips;
    private GameState state;
    private List<Menu> menuButtons;

    // Particle System
    private final ParticleSystem particleSystem;

    // Game UI
    private GameUI gameUI;

    public GameManager(Handler handler, ConfigManager config, float windowScaleX, float windowScaleY) {
        this.handler = handler;
        this.config = config;
        this.windowScaleX = windowScaleX;
        this.windowScaleY = windowScaleY;
        this.saveManager = new SaveManager();
        this.random = new Random();

        this.spawnManager = new SpawnManager(handler, config, random);
        this.upgradeManager = new UpgradeManager(config);
        this.particleSystem = new ParticleSystem(config);

        this.state = GameState.MENU;
        this.menuButtons = new ArrayList<>();

        showMenuButtons();
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public void setGameUI(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    // --- Menu management ---
    public void showMenuButtons() {
        if (!menuButtons.isEmpty())
            return;

        int width = config.displayWidth;
        int height = config.displayHeight;

        Menu newGameBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.NEW_GAME, config);
        Menu continueBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.CONTINUE, config);
        Menu exitBtn = new Menu(0.07f, 0.15f, 0.07f, 0.01f, ID.EXIT, config);

        newGameBtn.updatePosition(width, height, 2);
        continueBtn.updatePosition(width, height, 1);
        exitBtn.updatePosition(width, height, 0);

        menuButtons.add(newGameBtn);
        menuButtons.add(continueBtn);
        menuButtons.add(exitBtn);

        for (Menu btn : menuButtons)
            handler.addObject(btn);
    }

    public void hideMenuButtons() {
        for (Menu btn : menuButtons)
            handler.removeObject(btn);
        menuButtons.clear();
    }

    // --- Game control ---
    public void startNewGame() {
        System.out.println("Game Restarted");
        state = GameState.GAME;
        hideMenuButtons();

        clips = config.startClips;
        spawnManager.reset(config.startClips, config.maxClipCount);
        upgradeManager.reset();
    }

    public void continueGame() {
        System.out.println("Loading previous game...");
        state = GameState.GAME;
        hideMenuButtons();

        if (!saveManager.load(this)) {
            System.out.println("No save found — starting new game.");
            startNewGame();
        }
    }

    public void saveGame() {
        if (upgradeManager.getColoredUpgrade() == null) { // Check if initialized
            return;
        }
        saveManager.save(this);
    }

    // --- Gameplay actions ---
    public void collectClip(GameObject clip) {
        int baseValue = 0;
        java.awt.Color particleColor = java.awt.Color.GRAY;

        switch (clip.getID()) {
            case PAPERCLIP -> {
                baseValue = config.paperclipBaseValue;
                particleColor = java.awt.Color.GRAY;
            }
            case RED_PAPERCLIP -> {
                baseValue = config.paperclipBaseValue * 5;
                particleColor = java.awt.Color.RED;
            }
            case GREEN_PAPERCLIP -> {
                baseValue = config.paperclipBaseValue * 25;
                particleColor = java.awt.Color.GREEN;
            }
            case BLUE_PAPERCLIP -> {
                baseValue = config.paperclipBaseValue * 100;
                particleColor = java.awt.Color.BLUE;
            }
            case PURPLE_PAPERCLIP -> {
                baseValue = config.paperclipBaseValue * 1000;
                particleColor = new java.awt.Color(128, 0, 128);
            }
            case YELLOW_PAPERCLIP -> {
                baseValue = config.paperclipBaseValue * 10000;
                particleColor = java.awt.Color.YELLOW;
            }
            default -> {
            }
        }

        if (baseValue > 0) {
            clips += baseValue * (upgradeManager.getValueUpgradeCount() + 1);
            spawnManager.decreaseClipCount();
            handler.removeObject(clip);

            // Spawn particles
            particleSystem.spawnExplosion(clip.getX() + 16, clip.getY() + 16, particleColor, 10);
        }
    }

    public void checkCollisions(int x1, int y1, int x2, int y2) {
        // Check for paperclip collisions (hover/drag) using line segment for CCD
        // AND a rectangle for the cursor body to make collection easier
        java.awt.Rectangle cursorBounds = new java.awt.Rectangle(x2, y2, 32, 32); // Approx cursor size

        for (int i = 0; i < handler.getObjects().size(); i++) {
            GameObject obj = handler.getObjects().get(i);
            if (obj.getID() == null)
                continue;

            // Check if line segment intersects object bounds OR if cursor rectangle
            // intersects
            if (obj.getBounds().intersectsLine(x1, y1, x2, y2) || obj.getBounds().intersects(cursorBounds)) {
                switch (obj.getID()) {
                    case PAPERCLIP, RED_PAPERCLIP, GREEN_PAPERCLIP, BLUE_PAPERCLIP, PURPLE_PAPERCLIP,
                            YELLOW_PAPERCLIP -> {
                        collectClip(obj);
                        return; // Collected a clip, stop checking (one per tick/event)
                    }
                    default -> {
                    }
                }
            }
        }
    }

    public void handleUiClick(int x, int y) {
        // Check UI clicks
        if (gameUI != null) {
            java.awt.event.MouseEvent e = new java.awt.event.MouseEvent(
                    new java.awt.Component() {
                    }, 0, 0, 0, x, y, 1, false);
            gameUI.getUIManager().onMouseMove(e); // Update hover state
            gameUI.getUIManager().onMouseRelease(e); // Trigger click
        }
    }

    public void handleUiHover(int x, int y) {
        if (gameUI != null) {
            java.awt.event.MouseEvent e = new java.awt.event.MouseEvent(
                    new java.awt.Component() {
                    }, 0, 0, 0, x, y, 0, false);
            gameUI.getUIManager().onMouseMove(e);
        }
    }

    public void handleInput(int x, int y) {
        handleUiClick(x, y);
    }

    // --- Upgrade prices (Delegates) ---
    public int getColoredUpgradePrice(ColorTier tier) {
        return upgradeManager.getColoredUpgradePrice(tier);
    }

    public int getValueUpgradePrice() {
        return upgradeManager.getValueUpgradePrice();
    }

    public int getMoreUpgradePrice() {
        return upgradeManager.getMoreUpgradePrice();
    }

    // --- Upgrade wrappers for HUD ---
    public void buyColoredUpgrade() {
        int cost = upgradeManager.tryBuyColoredUpgrade(clips);
        if (cost > 0) {
            clips -= cost;
            System.out.println("Bought colored upgrade.");
        }
    }

    public void buyValueUpgrade() {
        int cost = upgradeManager.tryBuyValueUpgrade(clips);
        if (cost > 0) {
            clips -= cost;
            System.out.println("Bought value upgrade.");
        }
    }

    public void buyMoreUpgrade() {
        int cost = upgradeManager.tryBuyMoreUpgrade(clips);
        if (cost > 0) {
            clips -= cost;
            spawnManager.increaseMaxClipCount();
            System.out.println("Bought more upgrade.");
        }
    }

    // --- Game ticking ---
    public void tick() {
        if (state != GameState.GAME)
            return;

        // Pass necessary data to SpawnManager
        // HUD dimensions for spawn margins
        int hudWidth = (int) (400 * windowScaleX);
        int hudHeight = (int) (400 * windowScaleY);

        spawnManager.tick(windowScaleX, windowScaleY, upgradeManager.getColoredUpgrade(), hudWidth, hudHeight);
        particleSystem.tick();

        if (gameUI != null) {
            gameUI.tick();
        }
    }

    public void render(java.awt.Graphics g) {
        if (state != GameState.GAME)
            return;

        particleSystem.render(g);
    }

    // --- Getters / Setters for SaveManager and HUD ---
    public int getClips() {
        return clips;
    }

    public void setClips(int clips) {
        this.clips = clips;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    // Delegate getters for compatibility with existing HUD/SaveManager
    public int getCurrentClipCount() {
        return spawnManager.getCurrentClipCount();
    }

    public void setCurrentClipCount(int count) {
        spawnManager.setCurrentClipCount(count);
    }

    public int getMaxClipCount() {
        return spawnManager.getMaxClipCount();
    }

    public void setMaxClipCount(int count) {
        spawnManager.setMaxClipCount(count);
    }

    public ColorTier getColoredUpgrade() {
        return upgradeManager.getColoredUpgrade();
    }

    public void setColoredUpgrade(ColorTier tier) {
        upgradeManager.setColoredUpgrade(tier);
    }

    public int getValueUpgradeCount() {
        return upgradeManager.getValueUpgradeCount();
    }

    public void setValueUpgradeCount(int count) {
        upgradeManager.setValueUpgradeCount(count);
    }

    public int getMoreUpgradeCount() {
        return upgradeManager.getMoreUpgradeCount();
    }

    public void setMoreUpgradeCount(int count) {
        upgradeManager.setMoreUpgradeCount(count);
    }

    public Handler getHandler() {
        return handler;
    }

    public ConfigManager getConfig() {
        return config;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        if (state == GameState.MENU)
            showMenuButtons();
        else
            hideMenuButtons();
    }

    public float getWindowScaleX() {
        return windowScaleX;
    }

    public float getWindowScaleY() {
        return windowScaleY;
    }
}
