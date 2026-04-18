package com.coolerpromc.easybrewing.config;

import com.coolerpromc.easybrewing.Constants;

public class CommonConfig {
    public static int potionStackSize = 16;
    public static int maxSpeedUpgrade = 5;
    public static boolean allowAmountUpgrade = true;
    public static int maxAmountUpgrade = 1;

    public static int potionCount = 3;
    public static int processingTime = 400;

    public static int cobblemonPotionCount = 3;

    public static int potionCount() {
        if (potionCount > potionStackSize) {
            Constants.LOGGER.warn("potionCount ({}) is greater than potionStackSize ({}). Clamping.", potionCount, potionStackSize);
        }
        return Math.min(potionCount, potionStackSize);
    }
}
