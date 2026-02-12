package com.coolerpromc.easybrewing.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class OutputItemStackHandler extends SimpleContainer {
    public OutputItemStackHandler(){
        super(1);
    }

    @Override
    public boolean canAddItem(ItemStack stack) {
        return false;
    }
}
