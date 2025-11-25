package aiden.clip.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class UIElement {

    protected int x, y;
    protected int width, height;
    protected boolean visible = true;
    protected boolean hovered = false;

    public UIElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void tick();

    public abstract void render(Graphics g);

    public abstract void onClick();

    public void onMouseMove(MouseEvent e) {
        hovered = getBounds().contains(e.getX(), e.getY());
    }

    public void onMouseRelease(MouseEvent e) {
        if (hovered && visible) {
            onClick();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
