package com.coolerpromc.easybrewing.screen.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MenuSlot extends Slot {
    private final int slotIndex;

    public MenuSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.slotIndex = slot;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return container.canPlaceItem(slotIndex, itemStack);
    }
}
