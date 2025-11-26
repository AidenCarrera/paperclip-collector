package aiden.clip.entities;

import java.util.Random;
import aiden.clip.core.ColorTier;
import aiden.clip.core.ConfigManager;
import aiden.clip.core.Handler;

public class Spawner {
    private final Handler handler;
    private final Random random;
    private final ConfigManager config;

    public Spawner(Handler handler, Random random, ConfigManager config) {
        this.handler = handler;
        this.random = random;
        this.config = config;
    }

    /**
     * Spawn a single paperclip at a specific location.
     */
    public void spawnClip(ColorTier tier, int x, int y, float windowScaleX, float windowScaleY) {
        handler.addObject(new Paperclip(x, y, tier, config, windowScaleX, windowScaleY));
    }

    /**
     * Spawn multiple paperclips at random locations.
     * This method can optionally be removed if all spawning is now handled by
     * GameManager.
     */
    public void spawnClips(ColorTier tier, int count, int minX, int minY, int maxX, int maxY, float windowScaleX,
            float windowScaleY) {
        for (int i = 0; i < count; i++) {
            int x = minX + random.nextInt(Math.max(1, maxX - minX));
            int y = minY + random.nextInt(Math.max(1, maxY - minY));
            spawnClip(tier, x, y, windowScaleX, windowScaleY);
        }
    }
}
