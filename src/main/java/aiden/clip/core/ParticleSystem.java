package aiden.clip.core;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.Color;
import java.util.Random;

import aiden.clip.entities.Particle;

public class ParticleSystem {

    private final List<Particle> particles;
    private final Random random;
    private final ConfigManager config;

    public ParticleSystem(ConfigManager config) {
        this.particles = new ArrayList<>();
        this.random = new Random();
        this.config = config;
    }

    public void tick() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.tick();
            if (p.isDead()) {
                it.remove();
            }
        }
    }

    public void render(Graphics g) {
        for (Particle p : particles) {
            p.render(g);
        }
    }

    public void spawnExplosion(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            float velX = (random.nextFloat() - 0.5f) * 10;
            float velY = (random.nextFloat() - 0.5f) * 10;
            float life = 0.5f + random.nextFloat() * 0.5f;
            int size = 3 + random.nextInt(5);
            particles.add(new Particle(x, y, size, size, color, life, velX, velY, config));
        }
    }
}
