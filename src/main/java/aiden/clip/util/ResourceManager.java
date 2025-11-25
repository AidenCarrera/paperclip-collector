package aiden.clip.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceManager {

    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final Map<String, Image> iconCache = new HashMap<>();

    public static BufferedImage getImage(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(ResourceManager.class.getResource(path)));
            imageCache.put(path, image);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Image getIcon(String path) {
        if (iconCache.containsKey(path)) {
            return iconCache.get(path);
        }

        Image image = new ImageIcon(Objects.requireNonNull(ResourceManager.class.getResource(path))).getImage();
        iconCache.put(path, image);
        return image;
    }

    public static void clearCache() {
        imageCache.clear();
        iconCache.clear();
    }
}
