package com.coolerpromc.easybrewing.screen;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ItemBrewingStationMenu extends AbstractContainerMenu {
    private final ItemBrewingStationBE blockEntity;
    private final Level level;
    private final ContainerData data;

    public ItemBrewingStationMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(8));
    }

    public ItemBrewingStationMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity, ContainerData data){
        super(EasyBrewing.ITEM_BREWING_STATION_MENU.get(), containerId);
        this.blockEntity = (ItemBrewingStationBE) blockEntity;
        this.level = playerInventory.player.level();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        IItemHandler fuelHandler = this.blockEntity.fuelHandler;
        this.addSlot(new SlotItemHandler(fuelHandler, 0, 17, 17));

        IItemHandler potionHandler = this.blockEntity.potionHandler;
        this.addSlot(new SlotItemHandler(potionHandler, 0, 79, 17));

        IItemHandler inputHandler = this.blockEntity.inputHandler;
        this.addSlot(new SlotItemHandler(inputHandler, 0, 59, 37));

        IItemHandler outputHandler = this.blockEntity.outputHandler;
        this.addSlot(new SlotItemHandler(outputHandler, 0, 79, 57){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        IItemHandler upgradeHandler = this.blockEntity.upgradeHandler;
        this.addSlot(new SlotItemHandler(upgradeHandler, 0, 154, 6));
        this.addSlot(new SlotItemHandler(upgradeHandler, 1, 154, 24));

        addDataSlots(data);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 6;

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, EasyBrewing.ITEM_BREWING_STATION.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public ItemBrewingStationBE getBlockEntity() {
        return blockEntity;
    }

    public int getProgress(){
        return this.data.get(0);
    }

    public int getMaxProgress(){
        return this.data.get(1);
    }

    public int getFuel(){
        return this.data.get(2);
    }

    public float getSpeedMultiplier(){
        return this.data.get(3) / 100f;
    }

    public int getMaxUpgrade(){
        return this.data.get(4);
    }

    public int getAdditionalUpgrade(){
        return this.data.get(5);
    }

    public int getMaxAmountUpgrade(){
        return allowAmountUpgrade() ? this.data.get(6) : 0;
    }

    public boolean allowAmountUpgrade(){
        return this.data.get(7) == 1;
    }
}
