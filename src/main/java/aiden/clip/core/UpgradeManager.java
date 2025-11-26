package aiden.clip.core;

public class UpgradeManager {

    private final ConfigManager config;

    private ColorTier coloredUpgrade;
    private int valueUpgradeCount;
    private int moreUpgradeCount;

    public UpgradeManager(ConfigManager config) {
        this.config = config;
        reset();
    }

    public void reset() {
        coloredUpgrade = ColorTier.BASIC;
        valueUpgradeCount = 0;
        moreUpgradeCount = 0;
    }

    // --- Upgrade prices ---
    public int getColoredUpgradePrice(ColorTier tier) {
        return switch (tier) {
            case RED -> config.redUpgradeCost;
            case GREEN -> config.greenUpgradeCost;
            case BLUE -> config.blueUpgradeCost;
            case PURPLE -> config.purpleUpgradeCost;
            case YELLOW -> config.yellowUpgradeCost;
            default -> 0;
        };
    }

    public int getValueUpgradePrice() {
        return (int) (config.valueUpgradeBaseCost * Math.pow(config.upgradeCostMultiplier, valueUpgradeCount));
    }

    public int getMoreUpgradePrice() {
        return (int) (config.moreUpgradeBaseCost * Math.pow(config.upgradeCostMultiplier, moreUpgradeCount));
    }

    // --- Upgrade actions ---
    // Returns cost if successful, 0 if not (or throws exception/returns boolean)
    // For now, we'll return the cost so GameManager can deduct clips.
    public int tryBuyColoredUpgrade(int currentClips) {
        ColorTier nextTier = coloredUpgrade.next();
        if (nextTier != null) {
            int price = getColoredUpgradePrice(nextTier);
            if (currentClips >= price) {
                coloredUpgrade = nextTier;
                return price;
            }
        }
        return 0;
    }

    public int tryBuyValueUpgrade(int currentClips) {
        int price = getValueUpgradePrice();
        if (currentClips >= price) {
            valueUpgradeCount++;
            return price;
        }
        return 0;
    }

    public int tryBuyMoreUpgrade(int currentClips) {
        int price = getMoreUpgradePrice();
        if (currentClips >= price) {
            moreUpgradeCount++;
            return price;
        }
        return 0;
    }

    // --- Getters / Setters ---
    public ColorTier getColoredUpgrade() {
        return coloredUpgrade;
    }

    public void setColoredUpgrade(ColorTier coloredUpgrade) {
        this.coloredUpgrade = coloredUpgrade;
    }

    public int getValueUpgradeCount() {
        return valueUpgradeCount;
    }

    public void setValueUpgradeCount(int valueUpgradeCount) {
        this.valueUpgradeCount = valueUpgradeCount;
    }

    public int getMoreUpgradeCount() {
        return moreUpgradeCount;
    }

    public void setMoreUpgradeCount(int moreUpgradeCount) {
        this.moreUpgradeCount = moreUpgradeCount;
    }
}
