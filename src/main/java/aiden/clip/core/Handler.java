package aiden.clip.core;

import java.awt.*;
import java.util.LinkedList;

public class Handler {
    private final LinkedList<GameObject> objects = new LinkedList<>();
    private final LinkedList<GameObject> toAdd = new LinkedList<>();
    private final LinkedList<GameObject> toRemove = new LinkedList<>();

    // Runs this method every frame
    public void tick() {
        for (GameObject obj : objects) {
            obj.tick();
        }

        // Safely add queued objects
        if (!toAdd.isEmpty()) {
            objects.addAll(toAdd);
            toAdd.clear();
        }

        // Safely remove queued objects
        if (!toRemove.isEmpty()) {
            objects.removeAll(toRemove);
            toRemove.clear();
        }
    }

    // Renders game objects
    public void render(Graphics g) {
        for (GameObject obj : objects) {
            obj.render(g);
        }
    }

    // Queues an object to be added after current tick
    public void addObject(GameObject object) {
        toAdd.add(object);
    }

    // Queues an object to be removed after current tick
    public void removeObject(GameObject object) {
        toRemove.add(object);
    }

    // Return the list directly as we are now single-threaded
    public LinkedList<GameObject> getObjects() {
        return objects;
    }

    public void setMousePositionForMouse(int mouseX, int mouseY) {
        for (GameObject obj : objects) {
            if (obj.getID() == ID.MOUSE) {
                obj.setMousePosition(mouseX, mouseY);
            }
        }
    }

    public void resetMousePosition() {
        for (GameObject obj : objects) {
            if (obj.getID() == ID.MOUSE) {
                obj.setMousePosition(0, 0);
            }
        }
    }
}
