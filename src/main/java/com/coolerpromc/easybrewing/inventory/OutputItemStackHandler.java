package com.coolerpromc.easybrewing.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class OutputItemStackHandler extends SimpleInventory {
    public OutputItemStackHandler(){
        super(1);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
