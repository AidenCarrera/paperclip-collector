package aiden.clip.entities;

import javax.swing.*;
import aiden.clip.core.ColorTier;
import aiden.clip.core.ConfigManager;
import aiden.clip.core.GameObject;
import aiden.clip.core.ID;
import java.awt.*;
import java.util.Objects;

public class Paperclip extends GameObject {

    private final int width;
    private final int height;
    private final int value;
    private final Color particleColor;

    private static final Image PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/paperclip.png"))).getImage();
    private static final Image RED_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/redPaperclip.png"))).getImage();
    private static final Image GREEN_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/greenPaperclip.png"))).getImage();
    private static final Image BLUE_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/bluePaperclip.png"))).getImage();
    private static final Image PURPLE_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/purplePaperclip.png"))).getImage();
    private static final Image YELLOW_PAPERCLIP = new ImageIcon(
            Objects.requireNonNull(Paperclip.class.getResource("/images/yellowPaperclip.png"))).getImage();

    /**
     * @param x      X position
     * @param y      Y position
     * @param id     Paperclip type
     * @param config Config manager
     * @param scaleX Horizontal scale factor
     * @param scaleY Vertical scale factor
     */
    private final ColorTier tier;

    /**
     * @param x      X position
     * @param y      Y position
     * @param tier   Paperclip tier (color)
     * @param config Config manager
     * @param scaleX Horizontal scale factor
     * @param scaleY Vertical scale factor
     */
    public Paperclip(int x, int y, ColorTier tier, ConfigManager config, double scaleX, double scaleY) {
        super(x, y, ID.PAPERCLIP, config);
        this.tier = tier;

        Image img = getImageForTier(tier);

        // Apply resolution scaling + optional clip size multiplier
        double finalScaleX = scaleX * config.clipSize;
        double finalScaleY = scaleY * config.clipSize;

        width = (int) (img.getWidth(null) * finalScaleX);
        height = (int) (img.getHeight(null) * finalScaleY);

        // Initialize properties based on Tier
        switch (tier) {
            case BASIC -> {
                this.value = config.paperclipBaseValue;
                this.particleColor = Color.GRAY;
            }
            case RED -> {
                this.value = config.paperclipBaseValue * 5;
                this.particleColor = Color.RED;
            }
            case GREEN -> {
                this.value = config.paperclipBaseValue * 25;
                this.particleColor = Color.GREEN;
            }
            case BLUE -> {
                this.value = config.paperclipBaseValue * 100;
                this.particleColor = Color.BLUE;
            }
            case PURPLE -> {
                this.value = config.paperclipBaseValue * 1000;
                this.particleColor = new Color(128, 0, 128);
            }
            case YELLOW -> {
                this.value = config.paperclipBaseValue * 10000;
                this.particleColor = Color.YELLOW;
            }
            default -> {
                this.value = 0;
                this.particleColor = Color.GRAY;
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void tick() {
    }

    public void render(Graphics g) {
        g.drawImage(getImageForTier(tier), x, y, width, height, null);
    }

    private Image getImageForTier(ColorTier tier) {
        return switch (tier) {
            case BASIC -> PAPERCLIP;
            case RED -> RED_PAPERCLIP;
            case GREEN -> GREEN_PAPERCLIP;
            case BLUE -> BLUE_PAPERCLIP;
            case PURPLE -> PURPLE_PAPERCLIP;
            case YELLOW -> YELLOW_PAPERCLIP;
        };
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getValue() {
        return value;
    }

    public Color getParticleColor() {
        return particleColor;
    }

    public static Dimension getScaledSize(ConfigManager config, double scaleX, double scaleY) {
        Image img = PAPERCLIP; // use base paperclip image
        int width = (int) (img.getWidth(null) * scaleX * config.clipSize);
        int height = (int) (img.getHeight(null) * scaleY * config.clipSize);
        return new Dimension(width, height);
    }
}
