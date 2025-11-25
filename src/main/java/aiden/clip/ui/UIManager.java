package aiden.clip.ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UIManager {

    private final List<UIElement> elements;

    public UIManager() {
        this.elements = new ArrayList<>();
    }

    public void addElement(UIElement e) {
        elements.add(e);
    }

    public void removeElement(UIElement e) {
        elements.remove(e);
    }

    public void clear() {
        elements.clear();
    }

    public void tick() {
        for (UIElement e : elements) {
            e.tick();
        }
    }

    public void render(Graphics g) {
        for (UIElement e : elements) {
            e.render(g);
        }
    }

    public void onMouseMove(MouseEvent e) {
        for (UIElement element : elements) {
            element.onMouseMove(e);
        }
    }

    public void onMouseRelease(MouseEvent e) {
        for (UIElement element : elements) {
            element.onMouseRelease(e);
        }
    }
}
