package com.coolerpromc.easybrewing.platform;

import com.coolerpromc.easybrewing.platform.services.IPotionHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NeoForgePotionHelper implements IPotionHelper {
    @Override
    public boolean isInput(Level level, ItemStack stack) {
        return level.potionBrewing().isInput(stack);
    }

    @Override
    public boolean isIngredient(Level level, ItemStack stack) {
        return level.potionBrewing().isIngredient(stack);
    }
}
