package com.coolerpromc.easybrewing.platform.services;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IPotionHelper {
    boolean isInput(Level level, ItemStack stack);
    boolean isIngredient(Level level, ItemStack stack);
}
