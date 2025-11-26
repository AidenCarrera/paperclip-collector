package aiden.clip.save;

public class GameSaveData {
    public int clips;
    public String coloredUpgrade; // store enum name
    public int valueUpgradeCount;
    public int moreUpgradeCount;

    public GameSaveData() {
    } // Needed for JSON deserialization

    public GameSaveData(int clips, String coloredUpgrade, int valueUpgradeCount, int moreUpgradeCount) {
        this.clips = clips;
        this.coloredUpgrade = coloredUpgrade;
        this.valueUpgradeCount = valueUpgradeCount;
        this.moreUpgradeCount = moreUpgradeCount;
    }
}
