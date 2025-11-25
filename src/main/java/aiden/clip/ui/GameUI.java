package aiden.clip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import aiden.clip.core.ColorTier;
import aiden.clip.core.ConfigManager;
import aiden.clip.core.GameManager;
import aiden.clip.core.ID;
import aiden.clip.util.ResourceManager;

public class GameUI {

    private final GameManager gameManager;
    private final UIManager uiManager;
    private final float scaleX, scaleY;
    private final float hudScale;

    private final Image bamboo;
    private final Image clipIcon;
    private final Font mainFont;

    // Buttons
    private UIButton valueUpgradeBtn;
    private UIButton moreUpgradeBtn;
    private UIButton coloredUpgradeBtn;

    public GameUI(GameManager gameManager, ConfigManager config, float scaleX, float scaleY) {
        this.gameManager = gameManager;

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.hudScale = (float) config.hudScale;
        this.uiManager = new UIManager();

        this.bamboo = ResourceManager.getImage("/images/bamboo.png");
        this.clipIcon = ResourceManager.getImage("/images/clipIcon.png");
        this.mainFont = new Font("TimesRoman", Font.BOLD, (int) (20 * hudScale * Math.min(scaleX, scaleY)));

        initUI();
    }

    private void initUI() {
        // Positions from old HUD
        int coloredX = 170, coloredY = 70;
        int valueX = 170, valueY = 165;
        int moreX = 60, moreY = 165;
        int size = 70;

        // Create buttons
        // Value Upgrade
        valueUpgradeBtn = new UIButton(
                scaledX(valueX), scaledY(valueY), scaledSize(size), scaledSize(size),
                "", ResourceManager.getImage("/images/valueUpgrade.png"),
                gameManager::buyValueUpgrade);
        uiManager.addElement(valueUpgradeBtn);

        // More Upgrade
        moreUpgradeBtn = new UIButton(
                scaledX(moreX), scaledY(moreY), scaledSize(size), scaledSize(size),
                "", ResourceManager.getImage("/images/moreUpgrade.png"),
                gameManager::buyMoreUpgrade);
        uiManager.addElement(moreUpgradeBtn);

        // Colored Upgrade (Dynamic image, so we might need a custom button or update
        // it)
        coloredUpgradeBtn = new UIButton(
                scaledX(coloredX), scaledY(coloredY), scaledSize(size), scaledSize(size),
                "", ResourceManager.getImage("/images/redUpgrade.png"), // Initial
                gameManager::buyColoredUpgrade) {
            @Override
            public void render(Graphics g) {
                // Custom render to update image based on tier
                ColorTier nextTier = gameManager.getColoredUpgrade() != null ? gameManager.getColoredUpgrade().next()
                        : ColorTier.RED;
                if (nextTier != null && nextTier.getUpgradeID() != null) {
                    // Update image logic here or just draw it
                    // For now, let's just draw the correct image
                    Image img = getUpgradeImage(nextTier.getUpgradeID());
                    if (img != null) {
                        g.drawImage(img, x, y, width, height, null);
                    }
                    // Draw cost
                    drawIconWithValue(g, clipIcon, x, y, gameManager.getColoredUpgradePrice(nextTier));
                }
            }
        };
        uiManager.addElement(coloredUpgradeBtn);
    }

    private Image getUpgradeImage(ID id) {
        return switch (id) {
            case RED_UPGRADE -> ResourceManager.getImage("/images/redUpgrade.png");
            case GREEN_UPGRADE -> ResourceManager.getImage("/images/greenUpgrade.png");
            case BLUE_UPGRADE -> ResourceManager.getImage("/images/blueUpgrade.png");
            case PURPLE_UPGRADE -> ResourceManager.getImage("/images/purpleUpgrade.png");
            case YELLOW_UPGRADE -> ResourceManager.getImage("/images/yellowUpgrade.png");
            default -> null;
        };
    }

    public void tick() {
        uiManager.tick();
    }

    public void render(Graphics g) {
        // Background
        if (bamboo != null) {
            g.drawImage(bamboo, 0, 0, (int) (bamboo.getWidth(null) * scaleX * hudScale),
                    (int) (bamboo.getHeight(null) * scaleY * hudScale), null);
        }

        g.setFont(mainFont);
        g.setColor(Color.WHITE);

        // Clip counter
        drawIconWithValue(g, clipIcon, scaledX(62), scaledY(80), gameManager.getClips());

        // Render UI Manager (Buttons)
        uiManager.render(g);

        // Draw costs for generic upgrades (since button doesn't handle it yet)
        // Value Upgrade Cost
        drawIconWithValue(g, clipIcon, scaledX(170), scaledY(165),
                gameManager.getValueUpgradePrice(), gameManager.getValueUpgradeCount(),
                scaledX(170 - 34), scaledY(165 + 22));

        // More Upgrade Cost
        drawIconWithValue(g, clipIcon, scaledX(60), scaledY(165),
                gameManager.getMoreUpgradePrice(), gameManager.getMoreUpgradeCount(),
                scaledX(60 - 34), scaledY(165 + 22));
    }

    private void drawIconWithValue(Graphics g, Image icon, int x, int y, int value) {
        g.drawString(String.valueOf(value), x + scaledSize(20), y + scaledSize(5));
        int iconHeight = scaledSize(22);
        int iconWidth = (int) (icon.getWidth(null) * ((double) iconHeight / icon.getHeight(null)));
        g.drawImage(icon, x, y - scaledSize(18), iconWidth, iconHeight, null);
    }

    private void drawIconWithValue(Graphics g, Image icon, int iconX, int iconY,
            int price, int count, int countX, int countY) {
        String text = "(" + count + ") " + price;
        g.drawString(text, countX + scaledSize(45), countY - scaledSize(25));
        int iconHeight = scaledSize(22);
        int iconWidth = (int) (icon.getWidth(null) * ((double) iconHeight / icon.getHeight(null)));
        g.drawImage(icon, iconX - scaledSize(5), iconY - scaledSize(20), iconWidth, iconHeight, null);
    }

    private int scaledX(int x) {
        return (int) (x * hudScale * scaleX);
    }

    private int scaledY(int y) {
        return (int) (y * hudScale * scaleY);
    }

    private int scaledSize(int size) {
        return (int) (size * hudScale * Math.min(scaleX, scaleY));
    }

    public UIManager getUIManager() {
        return uiManager;
    }
}
