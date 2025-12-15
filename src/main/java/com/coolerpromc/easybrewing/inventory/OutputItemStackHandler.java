package com.coolerpromc.easybrewing.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class OutputItemStackHandler extends SimpleContainer {
    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return false;
    }
}
