package aiden.clip.save;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import aiden.clip.core.ColorTier;
import aiden.clip.core.GameManager;
import aiden.clip.core.Handler;
import aiden.clip.core.ID;
import aiden.clip.entities.Upgrade;

import java.io.File;
import java.io.IOException;

public class SaveManager {

    private final String path;
    private final ObjectMapper mapper;

    public SaveManager() {
        String userHome = System.getProperty("user.home");
        String saveDir;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows: Documents/My Games/ClipGame
            File documents = new File(userHome, "Documents");
            saveDir = new File(documents, "My Games/ClipGame").getAbsolutePath();
        } else {
            // macOS/Linux: ~/ClipGame
            saveDir = new File(userHome, "ClipGame").getAbsolutePath();
        }

        // Make sure the directory exists
        File dir = new File(saveDir);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Failed to create save folder: " + dir.getAbsolutePath());
        }

        this.path = saveDir + File.separator + "save.json";

        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // --- SAVE ---
    public void save(GameManager gameManager) {
        GameSaveData data = new GameSaveData(
                gameManager.getClips(),
                gameManager.getColoredUpgrade().name(),
                gameManager.getValueUpgradeCount(),
                gameManager.getMoreUpgradeCount());

        try {
            File file = new File(path);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            System.out.println("Game saved as JSON at: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save game at: " + path);
            e.printStackTrace();
        }
    }

    // --- LOAD ---
    public boolean load(GameManager gameManager) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Save file not found. Starting new game.");
            return false;
        }

        try {
            GameSaveData data = mapper.readValue(file, GameSaveData.class);

            gameManager.setClips(data.clips);

            // Calculate max clips based on base config + upgrades
            int baseMax = gameManager.getConfig().maxClipCount;
            gameManager.setMaxClipCount(baseMax + data.moreUpgradeCount);

            try {
                if (data.coloredUpgrade != null && !data.coloredUpgrade.isBlank()) {
                    gameManager.setColoredUpgrade(ColorTier.valueOf(data.coloredUpgrade));
                } else {
                    gameManager.setColoredUpgrade(ColorTier.BASIC);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid coloredUpgrade in save file: " + data.coloredUpgrade);
                gameManager.setColoredUpgrade(ColorTier.BASIC);
            }

            gameManager.setValueUpgradeCount(data.valueUpgradeCount);
            gameManager.setMoreUpgradeCount(data.moreUpgradeCount);

            rebuildUpgrades(gameManager);
            System.out.println("Game loaded from JSON at: " + file.getAbsolutePath());
            return true;

        } catch (IOException e) {
            System.err.println("Failed to load save file. Starting new game.");
            e.printStackTrace();
            gameManager.setColoredUpgrade(ColorTier.BASIC);
            return false;
        }
    }

    private void rebuildUpgrades(GameManager gameManager) {
        Handler handler = gameManager.getHandler();
        ColorTier tier = gameManager.getColoredUpgrade();

        if (tier != null) {
            handler.addObject(new Upgrade(175, 50, tier.getUpgradeID(), gameManager.getConfig()));
        }

        handler.addObject(new Upgrade(175, 145, ID.VALUE_UPGRADE, gameManager.getConfig()));
        handler.addObject(new Upgrade(65, 145, ID.MORE_UPGRADE, gameManager.getConfig()));
    }
}
