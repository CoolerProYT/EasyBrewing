package com.coolerpromc.easybrewing.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OutputItemStackHandler extends ItemStacksResourceHandler {
    private final List<StackJournal> snapshotJournals;
    
    public OutputItemStackHandler(int size) {
        super(size);
        this.snapshotJournals = new ArrayList<>(this.stacks.size());

        for(int i = 0; i < this.stacks.size(); ++i) {
            this.snapshotJournals.add(new StackJournal(i));
        }
    }
    
    @Override
    public boolean isValid(int index, ItemResource resource) {
        return false;
    }

    public int innerInsert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, this.size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        ItemStack currentStack = this.stacks.get(index);
        int currentAmount = this.getAmountFrom(currentStack);
        if ((currentAmount == 0 || this.matches(currentStack, resource))) {
            int inserted = Math.min(amount, this.getCapacity(index, resource) - currentAmount);
            if (inserted > 0) {
                (this.snapshotJournals.get(index)).updateSnapshots(transaction);
                this.stacks.set(index, this.getStackFrom(resource, currentAmount + inserted));
                return inserted;
            }
        }

        return 0;
    }

    private class StackJournal extends SnapshotJournal<ItemStack> {
        private final int index;

        private StackJournal(int index) {
            this.index = index;
        }

        protected ItemStack createSnapshot() {
            return OutputItemStackHandler.this.copyOf(OutputItemStackHandler.this.stacks.get(this.index));
        }

        protected void revertToSnapshot(ItemStack snapshot) {
            OutputItemStackHandler.this.stacks.set(this.index, snapshot);
        }

        protected void onRootCommit(ItemStack originalState) {
            OutputItemStackHandler.this.onContentsChanged(this.index, originalState);
        }
    }
}
