package com.coolerpromc.easybrewing.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SimpleItemHandler implements Container {
    protected final NonNullList<ItemStack> items;

    public SimpleItemHandler(int size) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = items.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack result;
        if (stack.getCount() <= amount) {
            result = stack.copy();
            items.set(slot, ItemStack.EMPTY);
        } else {
            result = stack.split(amount);
        }
        onContentsChanged(slot);
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        onContentsChanged(slot);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isValid(slot, stack);
    }

    public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    protected void onContentsChanged(int slot) {
        setChanged();
    }

    /**
     * Serialize using MC 26.1's ValueOutput API.
     */
    public void serialize(ValueOutput output) {
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                output.store("slot_" + i, ItemStack.CODEC, items.get(i));
            }
        }
    }

    /**
     * Deserialize using MC 26.1's ValueInput API.
     */
    public void deserialize(ValueInput input) {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, input.read("slot_" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        }
    }

    /**
     * Try to insert a stack into a slot, returning how many were actually inserted.
     */
    public int insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isValid(slot, stack)) return 0;

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

    /**
     * Extract items from a slot, returning the extracted stack.
     */
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;
        ItemStack existing = items.get(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getCount());
        if (!simulate) {
            ItemStack result = existing.split(toExtract);
            if (existing.isEmpty()) {
                items.set(slot, ItemStack.EMPTY);
            }
            onContentsChanged(slot);
            return result;
        } else {
            return existing.copyWithCount(toExtract);
        }
    }
}
