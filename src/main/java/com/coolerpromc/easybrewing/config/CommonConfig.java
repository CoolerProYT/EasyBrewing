package com.coolerpromc.easybrewing.config;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CommonConfig {
    public static final CommonConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    public final ForgeConfigSpec.ConfigValue<Integer> potionStackSize;
    public final ForgeConfigSpec.ConfigValue<Integer> maxSpeedUpgrade;
    public final ForgeConfigSpec.ConfigValue<Boolean> allowAmountUpgrade;
    public final ForgeConfigSpec.ConfigValue<Integer> maxAmountUpgrade;

    private final ForgeConfigSpec.ConfigValue<Integer> potionCount;
    public final ForgeConfigSpec.IntValue processingTime;

    private CommonConfig(ForgeConfigSpec.Builder builder){
        builder.push("General");
        potionStackSize = builder.comment("Max stack size of potion item.").worldRestart().defineInRange("potionStackSize", 16, 1, 64);
        maxSpeedUpgrade = builder.comment("Amount of speed upgrade item can be used in one brewing station.").worldRestart().defineInRange("maxSpeedUpgrade", 5, 1, 64);
        allowAmountUpgrade = builder.comment("Allow brewing station to use amount upgrade item.").worldRestart().define("allowAmountUpgrade", true);
        maxAmountUpgrade = builder.comment("Amount of amount upgrade item can be used in one brewing station. Amount upgrade is for processing more item with one input item. For example, base recipe can craft 3 potion with 1 ingredient, consume 3 potion input produce 3 potion output. If amount upgrade is installed, if the upgrade add 1, it will become consume 4 and output 4.").worldRestart().defineInRange("maxAmountUpgrade", 1, 1, 64);
        builder.pop();

        builder.push("ItemBrewingStation");
        potionCount = builder.comment("The amount of potion it can craft with one ingredient. Cannot be more than the potion stack size.").defineInRange("potionCount", 3, 1, 64);
        processingTime = builder.comment("The base processing time for a recipe in ticks").defineInRange("processingTime", 400, 20, 4000);
        builder.pop();
    }

    static {
        Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public int potionCount(){
        if (potionCount.get() > potionStackSize.get()) {
            EasyBrewing.LOGGER.warn("potionCount ({}) is greater than potionStackSize ({}). Clamping.", potionCount.get(), potionStackSize.get());
        }
        return Math.min(potionCount.get(), potionStackSize.get());
    }
}
