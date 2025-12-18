package com.coolerpromc.easybrewing.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class OutputItemStackHandler extends ItemStackHandler {
    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false;
    }

    public ItemStack innerInsertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            int limit = this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemStack.isSameItemSameTags(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }

                    this.onContentsChanged(slot);
                }

                return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }
}
