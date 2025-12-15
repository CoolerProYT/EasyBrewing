package com.coolerpromc.easybrewing.config;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CommonConfig {
    public static final CommonConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.ConfigValue<Integer> potionStackSize;
    public final ModConfigSpec.ConfigValue<Integer> maxSpeedUpgrade;
    public final ModConfigSpec.ConfigValue<Boolean> allowAmountUpgrade;
    public final ModConfigSpec.ConfigValue<Integer> maxAmountUpgrade;

    private final ModConfigSpec.ConfigValue<Integer> potionCount;
    public final ModConfigSpec.IntValue processingTime;

    public final ModConfigSpec.ConfigValue<Integer> cobblemonPotionCount;

    private CommonConfig(ModConfigSpec.Builder builder){
        builder.push("General");
        potionStackSize = builder.comment("Max stack size of potion item.").gameRestart().defineInRange("potionStackSize", 16, 1, 64);
        maxSpeedUpgrade = builder.comment("Amount of speed upgrade item can be used in one brewing station.").gameRestart().defineInRange("maxSpeedUpgrade", 5, 1, 64);
        allowAmountUpgrade = builder.comment("Allow brewing station to use amount upgrade item.").gameRestart().define("allowAmountUpgrade", true);
        maxAmountUpgrade = builder.comment("Amount of amount upgrade item can be used in one brewing station. Amount upgrade is for processing more item with one input item. For example, base recipe can craft 3 potion with 1 ingredient, consume 3 potion input produce 3 potion output. If amount upgrade is installed, if the upgrade add 1, it will become consume 4 and output 4.").gameRestart().defineInRange("maxAmountUpgrade", 1, 1, 64);
        builder.pop();

        builder.push("ItemBrewingStation");
        potionCount = builder.comment("The amount of potion it can craft with one ingredient. Cannot be more than the potion stack size.").defineInRange("potionCount", 3, 1, 64);
        processingTime = builder.comment("The base processing time for a recipe in ticks").defineInRange("processingTime", 400, 20, 4000);
        builder.pop();

        builder.push("Cobblemon");
        cobblemonPotionCount = builder.comment("The amount of cobblemon brewing recipe item can craft with one ingredient.").defineInRange("cobblemonPotionCount", 3, 1, 64);
        builder.pop();
    }

    static {
        Pair<CommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonConfig::new);

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
