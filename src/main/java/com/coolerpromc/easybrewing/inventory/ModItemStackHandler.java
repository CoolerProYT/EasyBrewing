package com.coolerpromc.easybrewing.inventory;

import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraftforge.items.ItemStackHandler;

public class ModItemStackHandler extends ItemStackHandler {
    public ModItemStackHandler(int size) {
        super(size);
    }

    public void serialize(ValueOutput output) {
        ValueOutput.TypedOutputList<ItemStackWithSlot> itemList = output.list("Items", ItemStackWithSlot.CODEC);

        for(int i = 0; i < this.stacks.size(); ++i) {
            ItemStack stack = (ItemStack)this.stacks.get(i);
            if (!stack.isEmpty()) {
                itemList.add(new ItemStackWithSlot(i, stack));
            }
        }

        output.putInt("Size", this.stacks.size());
    }

    public void deserialize(ValueInput input) {
        this.setSize(input.getIntOr("Size", this.stacks.size()));
        input.listOrEmpty("Items", ItemStackWithSlot.CODEC).forEach((slot) -> {
            if (slot.isValidInContainer(this.stacks.size())) {
                this.stacks.set(slot.slot(), slot.stack());
            }
        });
    }
}
