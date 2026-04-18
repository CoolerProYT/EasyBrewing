package com.coolerpromc.easybrewing.inventory;

import net.minecraft.world.item.ItemStack;

public class OutputItemStackHandler extends SimpleItemHandler {

    public OutputItemStackHandler(int size) {
        super(size);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return false; // no external insertion
    }

    /**
     * Internal insert that bypasses isValid check.
     * Returns the number of items actually inserted.
     */
    public int innerInsertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return 0;

        ItemStack existing = items.get(slot);
        int limit = Math.min(getMaxStackSize(), stack.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(existing, stack)) return 0;
            limit -= existing.getCount();
        }

        if (limit <= 0) return 0;

        int toInsert = Math.min(stack.getCount(), limit);
        if (!simulate) {
            if (existing.isEmpty()) {
                items.set(slot, stack.copyWithCount(toInsert));
            } else {
                existing.grow(toInsert);
            }
            onContentsChanged(slot);
        }
        return toInsert;
    }
}
