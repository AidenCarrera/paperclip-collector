package aiden.clip.core;

public enum ColorTier {
    BASIC(null), // Basic gray paperclip
    RED(ID.RED_UPGRADE),
    GREEN(ID.GREEN_UPGRADE),
    BLUE(ID.BLUE_UPGRADE),
    PURPLE(ID.PURPLE_UPGRADE),
    YELLOW(ID.YELLOW_UPGRADE);

    private final ID upgradeID; // The corresponding upgrade object in the HUD

    ColorTier(ID upgradeID) {
        this.upgradeID = upgradeID;
    }

    public ID getUpgradeID() {
        return upgradeID;
    }

    // Get the next tier in progression
    public ColorTier next() {
        int ordinal = this.ordinal();
        ColorTier[] values = ColorTier.values();
        if (ordinal < values.length - 1)
            return values[ordinal + 1];
        return null;
    }

    // Optional: get previous tier if needed
    public ColorTier previous() {
        int ordinal = this.ordinal();
        if (ordinal > 0)
            return ColorTier.values()[ordinal - 1];
        return null;
    }
}
