package aiden.clip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

public class UIButton extends UIElement {

    private final String text;
    private final Image icon;
    private final Runnable clickAction;
    private final Font font;
    private final Color textColor;
    private Color hoverColor;

    public UIButton(int x, int y, int width, int height, String text, Image icon, Runnable clickAction) {
        super(x, y, width, height);
        this.text = text;
        this.icon = icon;
        this.clickAction = clickAction;
        this.font = new Font("Arial", Font.BOLD, 14);
        this.textColor = Color.WHITE;
        this.hoverColor = new Color(255, 255, 255, 50);
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    @Override
    public void tick() {
    }

    @Override
    public void render(Graphics g) {
        if (!visible)
            return;

        if (hovered && hoverColor != null) {
            g.setColor(hoverColor);
            g.fillRect(x, y, width, height);
        }

        if (icon != null) {
            g.drawImage(icon, x + 5, y + 5, height - 10, height - 10, null);
        }

        if (text != null) {
            g.setColor(textColor);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (icon != null ? height : 5);
            int textY = y + (height + fm.getAscent()) / 2 - 4;
            g.drawString(text, textX, textY);
        }

        // Debug border
        // g.setColor(Color.RED);
        // g.drawRect(x, y, width, height);
    }

    @Override
    public void onClick() {
        if (clickAction != null) {
            clickAction.run();
        }
    }
}
