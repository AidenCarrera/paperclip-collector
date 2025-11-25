package aiden.clip.core;

import java.awt.Dimension;
import java.util.Random;

import aiden.clip.entities.Paperclip;
import aiden.clip.entities.Spawner;

public class SpawnManager {

    private final ConfigManager config;
    private final Random random;
    private final Spawner spawner; // Keeping Spawner for now as a low-level factory if needed, or we can merge
                                   // logic here.

    private int currentClipCount;
    private int maxClipCount;

    public SpawnManager(Handler handler, ConfigManager config, Random random) {

        this.config = config;
        this.random = random;
        this.spawner = new Spawner(handler, random, config);
        this.maxClipCount = config.maxClipCount;
    }

    public void tick(float windowScaleX, float windowScaleY, ColorTier coloredUpgrade, int hudWidth, int hudHeight) {
        // --- Determine max spawn bounds based on display and window scale ---
        int maxX = (int) (config.displayWidth * windowScaleX);
        int maxY = (int) (config.displayHeight * windowScaleY);

        // --- Compute paperclip size dynamically using Paperclip helper ---
        Dimension clipSize = Paperclip.getScaledSize(config, windowScaleX, windowScaleY);
        int paperclipWidth = clipSize.width;
        int paperclipHeight = clipSize.height;

        // --- Spawn bounds with margins ---
        int spawnMinX = hudWidth;
        int spawnMinY = 0;
        int spawnMaxX = Math.max(spawnMinX + 1, maxX - paperclipWidth);
        int spawnMaxY = Math.max(spawnMinY + 1, maxY - paperclipHeight);

        // --- Spawn paperclips while under max count ---
        while (currentClipCount < maxClipCount) {
            spawnSingleClip(coloredUpgrade, spawnMinX, spawnMinY, spawnMaxX, spawnMaxY, windowScaleX, windowScaleY);
            currentClipCount++;
        }
    }

    private void spawnSingleClip(ColorTier coloredUpgrade, int minX, int minY, int maxX, int maxY, float scaleX,
            float scaleY) {
        // --- Determine spawn type using weighted random ---
        double P = 69;
        double redP = config.redSpawnWeight;
        double greenP = config.greenSpawnWeight;
        double blueP = config.blueSpawnWeight;
        double purpleP = config.purpleSpawnWeight;
        double yellowP = config.yellowSpawnWeight;

        double totalWeight = P + redP + greenP + blueP + purpleP + yellowP;
        double roll = random.nextDouble() * totalWeight;

        ID spawnType;
        if (roll < yellowP && coloredUpgrade.ordinal() >= ColorTier.YELLOW.ordinal())
            spawnType = ID.YELLOW_PAPERCLIP;
        else if (roll < yellowP + purpleP && coloredUpgrade.ordinal() >= ColorTier.PURPLE.ordinal())
            spawnType = ID.PURPLE_PAPERCLIP;
        else if (roll < yellowP + purpleP + blueP && coloredUpgrade.ordinal() >= ColorTier.BLUE.ordinal())
            spawnType = ID.BLUE_PAPERCLIP;
        else if (roll < yellowP + purpleP + blueP + greenP && coloredUpgrade.ordinal() >= ColorTier.GREEN.ordinal())
            spawnType = ID.GREEN_PAPERCLIP;
        else if (roll < yellowP + purpleP + blueP + greenP + redP
                && coloredUpgrade.ordinal() >= ColorTier.RED.ordinal())
            spawnType = ID.RED_PAPERCLIP;
        else
            spawnType = ID.PAPERCLIP;

        // --- Random position within safe bounds ---
        int spawnX = minX + random.nextInt(Math.max(1, maxX - minX));
        int spawnY = minY + random.nextInt(Math.max(1, maxY - minY));

        // --- Spawn the paperclip ---
        spawner.spawnClip(spawnType, spawnX, spawnY, scaleX, scaleY);
    }

    public void decreaseClipCount() {
        currentClipCount--;
    }

    public void reset(int startClips, int maxClips) {
        this.currentClipCount = 0;
        this.maxClipCount = maxClips;
    }

    public int getCurrentClipCount() {
        return currentClipCount;
    }

    public void setCurrentClipCount(int currentClipCount) {
        this.currentClipCount = currentClipCount;
    }

    public int getMaxClipCount() {
        return maxClipCount;
    }

    public void setMaxClipCount(int maxClipCount) {
        this.maxClipCount = maxClipCount;
    }

    public void increaseMaxClipCount() {
        this.maxClipCount++;
    }
}
