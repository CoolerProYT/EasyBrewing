package com.coolerpromc.easybrewing.screen;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBrewingStationMenu extends ScreenHandler {
    private final ItemBrewingStationBE blockEntity;
    private final World level;
    private final PropertyDelegate data;

    public ItemBrewingStationMenu(int containerId, PlayerInventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos), new ArrayPropertyDelegate(8));
    }

    public ItemBrewingStationMenu(int containerId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate data){
        super(EasyBrewing.ITEM_BREWING_STATION_MENU, containerId);
        this.blockEntity = (ItemBrewingStationBE) blockEntity;
        this.level = playerInventory.player.getWorld();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        SimpleInventory fuelHandler = this.blockEntity.fuelHandler;
        this.addSlot(new Slot(fuelHandler, 0, 17, 17){
            @Override
            public boolean canInsert(ItemStack stack) {
                return fuelHandler.isValid(0, stack);
            }
        });

        SimpleInventory potionHandler = this.blockEntity.potionHandler;
        this.addSlot(new Slot(potionHandler, 0, 79, 17){
            @Override
            public boolean canInsert(ItemStack stack) {
                return potionHandler.isValid(0, stack);
            }
        });

        SimpleInventory inputHandler = this.blockEntity.inputHandler;
        this.addSlot(new Slot(inputHandler, 0, 59, 37));

        SimpleInventory outputHandler = this.blockEntity.outputHandler;
        this.addSlot(new Slot(outputHandler, 0, 79, 57){
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        SimpleInventory upgradeHandler = this.blockEntity.upgradeHandler;
        this.addSlot(new Slot(upgradeHandler, 0, 154, 6){
            @Override
            public boolean canInsert(ItemStack stack) {
                return upgradeHandler.isValid(0, stack);
            }
        });
        this.addSlot(new Slot(upgradeHandler, 1, 154, 24){
            @Override
            public boolean canInsert(ItemStack stack) {
                return upgradeHandler.isValid(1, stack);
            }
        });

        addProperties(data);
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
    public ItemStack quickMove(PlayerEntity player, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasStack()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!insertItem(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!insertItem(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.setStackNoCallbacks(ItemStack.EMPTY);
        } else {
            sourceSlot.markDirty();
        }
        sourceSlot.onTakeItem(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, EasyBrewing.ITEM_BREWING_STATION);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
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
