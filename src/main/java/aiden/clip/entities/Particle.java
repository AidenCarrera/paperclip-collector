package aiden.clip.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import aiden.clip.core.GameObject;
import aiden.clip.core.ID;

public class Particle extends GameObject {

    private final Color color;
    private final int width, height;
    private final float life; // Life duration in seconds
    private float age;
    private float velX, velY;

    public Particle(int x, int y, int width, int height, Color color, float life, float velX, float velY,
            aiden.clip.core.ConfigManager config) {
        super(x, y, ID.PARTICLE, config);
        this.width = width;
        this.height = height;
        this.color = color;
        this.life = life;
        this.velX = velX;
        this.velY = velY;
        this.age = 0;

    }

    @Override
    public void tick() {
        x += velX;
        y += velY;
        age += 1.0f / 60.0f; // Assuming 60 ticks per second roughly, or use delta time if passed

        // Simple gravity or friction could be added here
        // velY += 0.1f;
        // velX *= 0.99f;
    }

    @Override
    public void render(Graphics g) {
        if (isDead())
            return;

        // Fade out
        int alpha = (int) (255 * (1 - (age / life)));
        if (alpha < 0)
            alpha = 0;
        if (alpha > 255)
            alpha = 255;

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.fillRect(x, y, width, height);
    }

    public boolean isDead() {
        return age >= life;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
