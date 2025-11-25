package aiden.clip.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import aiden.clip.core.ConfigManager;
import aiden.clip.core.Game;
import aiden.clip.core.GameState;
import aiden.clip.core.GameObject;
import aiden.clip.core.Handler;
import aiden.clip.ui.Menu;

public class InputHandler extends MouseAdapter {

    private final Game game;
    private final Handler handler;
    private final ConfigManager config;
    private int pressX, pressY;

    public InputHandler(Game game, Handler handler, ConfigManager config) {
        this.game = game;
        this.handler = handler;
        this.config = config;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressX = e.getX();
        pressY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int releaseX = e.getX();
        int releaseY = e.getY();

        if (Math.abs(releaseX - pressX) <= config.clickTolerance
                && Math.abs(releaseY - pressY) <= config.clickTolerance) {
            handleClick(releaseX, releaseY);
        }
    }

    private volatile int mouseX, mouseY;

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    private void handleClick(int x, int y) {
        if (game.getGameState() == GameState.MENU) {
            handleMenuClick(x, y);
        } else if (game.getGameState() == GameState.GAME) {
            handleGameClick(x, y);
        }
    }

    private void handleMenuClick(int x, int y) {
        for (GameObject obj : handler.getObjects()) {
            if (obj instanceof Menu menu && menu.getBounds().contains(x, y)) {
                switch (menu.getID()) {
                    case NEW_GAME -> {
                        game.setLevelImage("/images/office.png");
                        game.setGameState(GameState.GAME);
                        game.getGameManager().startNewGame();
                    }
                    case CONTINUE -> {
                        game.setLevelImage("/images/office.png");
                        game.setGameState(GameState.GAME);
                        game.getGameManager().continueGame();
                    }
                    case EXIT -> System.exit(0);
                    default -> {
                    }
                }
                return;
            }
        }
    }

    private void handleGameClick(int x, int y) {
        // Delegate to GameManager to handle UI clicks
        game.getGameManager().handleUiClick(x, y);
    }
}
