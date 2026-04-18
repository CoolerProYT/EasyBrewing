package com.coolerpromc.easybrewing.config;

import com.coolerpromc.easybrewing.Constants;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricCommonConfig {

    public static final FabricCommonConfig CONFIG = new FabricCommonConfig();

    private CommentedFileConfig config;

    public int potionStackSize;
    public int maxSpeedUpgrade;
    public boolean allowAmountUpgrade;
    public int maxAmountUpgrade;

    private int potionCount;
    public int processingTime;

    public int cobblemonPotionCount;

    private FabricCommonConfig() {}

    public void load() {
        Path path = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("easybrewing-common.toml");

        config = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .preserveInsertionOrder()
                .build();

        config.load();

        potionStackSize = getInt(
                "General.potionStackSize",
                16,
                "Max stack size of potion item.",
                1, 64
        );

        maxSpeedUpgrade = getInt(
                "General.maxSpeedUpgrade",
                5,
                "Amount of speed upgrade item can be used in one brewing station.",
                1, 64
        );

        allowAmountUpgrade = getBool(
                "General.allowAmountUpgrade",
                true,
                "Allow brewing station to use amount upgrade item."
        );

        maxAmountUpgrade = getInt(
                "General.maxAmountUpgrade",
                1,
                "Amount of amount upgrade item can be used in one brewing station.",
                1, 64
        );

        potionCount = getInt(
                "ItemBrewingStation.potionCount",
                3,
                "The amount of potion it can craft with one ingredient.",
                1, 64
        );

        processingTime = getInt(
                "ItemBrewingStation.processingTime",
                400,
                "The base processing time for a recipe in ticks",
                20, 4000
        );

        cobblemonPotionCount = getInt(
                "Cobblemon.cobblemonPotionCount",
                3,
                "The amount of cobblemon brewing recipe item can craft with one ingredient.",
                1, 64
        );

        syncToCommon();
    }

    private void syncToCommon() {
        CommonConfig.potionStackSize = potionStackSize;
        CommonConfig.maxSpeedUpgrade = maxSpeedUpgrade;
        CommonConfig.allowAmountUpgrade = allowAmountUpgrade;
        CommonConfig.maxAmountUpgrade = maxAmountUpgrade;
        CommonConfig.potionCount = potionCount();
        CommonConfig.processingTime = processingTime;
        CommonConfig.cobblemonPotionCount = cobblemonPotionCount;
    }

    private int getInt(String path, int def, String comment, int min, int max) {
        config.add(path, def);
        config.setComment(path, comment);

        int value = config.getInt(path);
        if (value < min || value > max) {
            Constants.LOGGER.warn("{} out of range ({}–{}). Clamping.", path, min, max);
            value = Math.max(min, Math.min(max, value));
            config.set(path, value);
        }
        return value;
    }

    private boolean getBool(String path, boolean def, String comment) {
        config.add(path, def);
        config.setComment(path, comment);
        return config.getOrElse(path, def);
    }

    public int potionCount() {
        if (potionCount > potionStackSize) {
            Constants.LOGGER.warn(
                    "potionCount ({}) is greater than potionStackSize ({}). Clamping.",
                    potionCount, potionStackSize
            );
        }
        return Math.min(potionCount, potionStackSize);
    }
}