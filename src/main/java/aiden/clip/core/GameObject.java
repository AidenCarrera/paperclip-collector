package aiden.clip.core;

import java.awt.*;

public abstract class GameObject {
    // Position and ID
    protected int x, y;
    protected ID id;

    // Optional: track mouse position (used by Mouse object)
    protected int mouseX, mouseY;

    // Shared config reference
    protected final ConfigManager config;

    /**
     * Base constructor for all game objects.
     * 
     * @param x      initial x position
     * @param y      initial y position
     * @param id     object ID
     * @param config reference to global ConfigManager
     */
    public GameObject(int x, int y, ID id, ConfigManager config) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.config = config;
    }

    // Abstract methods for each subclass to implement
    public abstract void tick();

    public abstract void render(Graphics g);

    public abstract Rectangle getBounds();

    // Common utility methods
    public ID getID() {
        return id;
    }

    public void setMousePosition(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public Point getMousePosition() {
        return new Point(mouseX, mouseY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
