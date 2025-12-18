package com.coolerpromc.easybrewing.block.entity;

import com.cobblemon.mod.fabric.brewing.CobblemonFabricBreweryRegistry;
import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.inventory.OutputItemStackHandler;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class ItemBrewingStationBE extends BlockEntity implements ExtendedScreenHandlerFactory {
    public final PropertyDelegate data;
    public final SimpleInventory fuelHandler = new SimpleInventory(1){
        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return stack.isOf(Items.BLAZE_POWDER);
        }
    };
    public final SimpleInventory potionHandler = new SimpleInventory(1){
        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return isValidPotion(stack);
        }
    };
    public final SimpleInventory inputHandler = new SimpleInventory(1);
    public final OutputItemStackHandler outputHandler = new OutputItemStackHandler();
    public final SimpleInventory upgradeHandler = new SimpleInventory(2){
        @Override
        public boolean isValid(int slot, ItemStack stack) {
            if (slot == 1){
                return stack.getItem() instanceof AmountUpgradeItem;
            }
            return stack.getItem() instanceof SpeedUpgradeItem;
        }
    };

    public InventoryStorage fuelStorage = InventoryStorage.of(fuelHandler, null);
    public InventoryStorage potionStorage = InventoryStorage.of(potionHandler, null);
    public InventoryStorage inputStorage = InventoryStorage.of(inputHandler, null);
    public InventoryStorage outputStorage = InventoryStorage.of(outputHandler, null);
    public InventoryStorage upgradeStorage = InventoryStorage.of(upgradeHandler, null);

    private int progress = 0;
    private int maxProgress = CommonConfig.CONFIG.processingTime;
    public int fuel = 0;
    private float multiplier = 1f;
    public int additionalAmount = 0;
    private Map<Direction, Slot> capabilityBySide = new HashMap<>();

    public ItemBrewingStationBE(BlockPos pos, BlockState blockState) {
        super(EasyBrewing.ITEM_BREWING_STATION_BE, pos, blockState);
        this.data = new PropertyDelegate() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> ItemBrewingStationBE.this.progress;
                    case 1 -> ItemBrewingStationBE.this.maxProgress;
                    case 2 -> ItemBrewingStationBE.this.fuel;
                    case 3 -> (int) (ItemBrewingStationBE.this.multiplier * 100);
                    case 4 -> CommonConfig.CONFIG.maxSpeedUpgrade;
                    case 5 -> additionalAmount;
                    case 6 -> CommonConfig.CONFIG.maxAmountUpgrade;
                    case 7 -> CommonConfig.CONFIG.allowAmountUpgrade ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i){
                    case 0 -> ItemBrewingStationBE.this.progress = i1;
                    case 1 -> ItemBrewingStationBE.this.maxProgress = i1;
                    case 2 -> ItemBrewingStationBE.this.fuel = i1;
                    case 3 -> ItemBrewingStationBE.this.multiplier = i1;
                }
            }

            @Override
            public int size() {
                return 8;
            }
        };

        initCapabilityBySide();

        upgradeHandler.addListener(container -> {
            handleUpgrade();
            getAdditionalAmount();
            markDirty();
        });
    }

    public void initCapabilityBySide(){
        capabilityBySide.putIfAbsent(Direction.DOWN, Slot.OUTPUT);
        capabilityBySide.putIfAbsent(Direction.UP, Slot.FUEL);
        capabilityBySide.putIfAbsent(Direction.NORTH, Slot.POTION);
        capabilityBySide.putIfAbsent(Direction.SOUTH, Slot.POTION);
        capabilityBySide.putIfAbsent(Direction.WEST, Slot.INPUT);
        capabilityBySide.putIfAbsent(Direction.EAST, Slot.INPUT);
    }

    @Override
    public @NotNull Text getDisplayName() {
        return Text.translatable("block.easybrewing.item_brewing_station");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int i, @NotNull PlayerInventory inventory, @NotNull PlayerEntity player) {
        return new ItemBrewingStationMenu(i, inventory, this, data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(getPos());
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound tag) {
        super.writeNbt(tag);

        tag.put("fuelHandler", Inventories.writeNbt(new NbtCompound(), fuelHandler.stacks));
        tag.put("potionHandler", Inventories.writeNbt(new NbtCompound(), potionHandler.stacks));
        tag.put("inputHandler", Inventories.writeNbt(new NbtCompound(), inputHandler.stacks));
        tag.put("outputHandler", Inventories.writeNbt(new NbtCompound(), outputHandler.stacks));
        tag.put("upgradeHandler", Inventories.writeNbt(new NbtCompound(), upgradeHandler.stacks));

        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putInt("fuel", fuel);
        tag.putFloat("multiplier", multiplier);
        tag.putInt("additionalAmount", additionalAmount);
        tag.put("capabilityBySide", Codec.unboundedMap(Direction.CODEC, Slot.CODEC).encodeStart(NbtOps.INSTANCE, capabilityBySide).getOrThrow(false, EasyBrewing.LOGGER::error));
    }

    @Override
    public void readNbt(@NotNull NbtCompound tag) {
        super.readNbt(tag);

        Inventories.readNbt(tag.getCompound("fuelHandler"), fuelHandler.stacks);
        Inventories.readNbt(tag.getCompound("potionHandler"), potionHandler.stacks);
        Inventories.readNbt(tag.getCompound("inputHandler"), inputHandler.stacks);
        Inventories.readNbt(tag.getCompound("outputHandler"), outputHandler.stacks);
        Inventories.readNbt(tag.getCompound("upgradeHandler"), upgradeHandler.stacks);

        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        fuel = tag.getInt("fuel");
        multiplier = tag.getFloat("multiplier");
        additionalAmount = tag.getInt("additionalAmount");
        capabilityBySide = new HashMap<>(Codec.unboundedMap(Direction.CODEC, Slot.CODEC).parse(NbtOps.INSTANCE, tag.getCompound("capabilityBySide")).getOrThrow(false, EasyBrewing.LOGGER::error));
    }

    @Override
    public @NotNull NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = new NbtCompound();
        writeNbt(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void tick(World level, BlockPos pos, BlockState blockState) {
        if (level.isClient()) return;

        handleFuel();
        checkUpgrade();

        if (isBrewable()){
            progress++;
            markDirty(level, pos, blockState);

            if (progress >= maxProgress){
                doBrew();
                level.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
                progress = 0;
            }
        }
        else{
            progress = 0;
        }
    }

    private void checkUpgrade(){
        if (!CommonConfig.CONFIG.allowAmountUpgrade && additionalAmount > 0){
            additionalAmount = 0;
        }
        else{
            if (CommonConfig.CONFIG.allowAmountUpgrade && additionalAmount == 0 && upgradeStorage.getSlot(1).getAmount() > 0){
                getAdditionalAmount();
            }
        }
    }

    private void handleUpgrade(){
        maxProgress = CommonConfig.CONFIG.processingTime;
        ItemStack upgradeStack = upgradeHandler.getStack(0);

        if (!upgradeStack.isEmpty() && upgradeStack.getItem() instanceof SpeedUpgradeItem upgradeItem){
            float multiplier = upgradeItem.getSpeedMultiplier();
            float baseMultiplier = 1f;
            int upgradeCount = Math.min(CommonConfig.CONFIG.maxSpeedUpgrade, upgradeStack.getCount());

            for (int i = 0; i < upgradeCount; i++){
                baseMultiplier *= multiplier;
            }

            this.multiplier = baseMultiplier;
            maxProgress = (int) (maxProgress / this.multiplier);
        }
        else {
            this.multiplier = 1f;
        }
    }

    private void handleFuel(){
        if (fuel <= 0){
            ItemStack fuelStack = fuelHandler.getStack(0);
            if (fuelStack.isOf(Items.BLAZE_POWDER)){
                fuel = 20;
                try(Transaction tx = Transaction.openOuter()){
                    long extracted = fuelStorage.extract(ItemVariant.of(fuelHandler.getStack(0)), 1, tx);
                    if (extracted == 1){
                        tx.commit();
                    }
                }
            }
        }
    }

    private boolean isBrewable() {
        ItemStack ingredient = inputHandler.getStack(0);
        if (ingredient.isEmpty()) {
            return false;
        } else if (!BrewingRecipeRegistry.isValidIngredient(ingredient)) {
            return false;
        } else {
            ItemStack potion = potionHandler.getStack(0);
            return !potion.isEmpty() && BrewingRecipeRegistry.hasRecipe(potion, ingredient) && hasEnoughInput() && canInsertIntoOutputSlot() && hasFuel();
        }
    }

    public void getAdditionalAmount(){
        if (CommonConfig.CONFIG.allowAmountUpgrade && upgradeHandler.getStack(1).getItem() instanceof AmountUpgradeItem upgradeItem){
            int effectiveUpgrade = Math.min(upgradeHandler.getStack(1).getCount(), CommonConfig.CONFIG.maxAmountUpgrade);
            additionalAmount = upgradeItem.getAmount() * effectiveUpgrade;
        }
        else{
            additionalAmount = 0;
        }
    }

    private boolean hasEnoughInput(){
        return potionHandler.getStack(0).getCount() >= CommonConfig.CONFIG.potionCount() + additionalAmount;
    }

    public boolean hasFuel(){
        return fuel > 0;
    }

    private boolean canInsertIntoOutputSlot(){
        ItemStack output = BrewingRecipeRegistry.craft(inputHandler.getStack(0), potionHandler.getStack(0));
        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);
        try(Transaction tx = Transaction.openOuter()){
            long inserted = outputStorage.insert(ItemVariant.of(output), output.getCount(), tx);
            return inserted == output.getCount();
        }
    }

    private void doBrew() {
        ItemStack ingredient = inputHandler.getStack(0);
        ItemStack potion = potionHandler.getStack(0);
        ItemStack output = BrewingRecipeRegistry.craft(ingredient, potion);

        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);

        if (!ingredient.getRecipeRemainder().isEmpty()) {
            ItemStack leftover = ingredient.getRecipeRemainder();
            ingredient.decrement(1);
            if (ingredient.isEmpty()) {
                ingredient = leftover;
            } else {
                ItemScatterer.spawn(world, getPos().getX(), getPos().getY(), getPos().getZ(), leftover);
            }
        } else {
            ingredient.decrement(1);
        }

        inputHandler.setStack(0, ingredient);
        try(Transaction tx = Transaction.openOuter()){
            potionStorage.extract(potionStorage.getSlot(0).getResource(), CommonConfig.CONFIG.potionCount() + additionalAmount, tx);
            tx.commit();
        }
        try(Transaction tx = Transaction.openOuter()){
            outputStorage.insert(ItemVariant.of(output), output.getCount(), tx);
            tx.commit();
        }
        fuel--;
    }

    public @Nullable Storage<ItemVariant> getCapability(@Nullable Direction direction) {
        if (direction == null) return null;
        Slot slot = capabilityBySide.get(direction);

        return switch (slot){
            case FUEL -> fuelStorage;
            case POTION -> potionStorage;
            case INPUT -> inputStorage;
            case OUTPUT -> outputStorage;
        };
    }

    public void setCapabilityBySide(Direction direction, Slot slot){
        capabilityBySide.put(direction, slot);
        markDirty();
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
    }

    public Slot getCapabilityBySide(Direction direction){
        return capabilityBySide.get(direction);
    }

    private boolean isValidPotion(ItemStack stack){
        return stack.getItem() instanceof PotionItem || stack.isOf(Items.GLASS_BOTTLE) || CobblemonFabricBreweryRegistry.INSTANCE.isValidPotionSlot(stack);
    }

    public void drops(){
        SimpleInventory container = new SimpleInventory(
                fuelHandler.getStack(0),
                potionHandler.getStack(0),
                inputHandler.getStack(0),
                outputHandler.getStack(0),
                upgradeHandler.getStack(0),
                upgradeHandler.getStack(1)
        );

        ItemScatterer.spawn(world, getPos(), container);
    }

    public enum Slot{
        FUEL(0xFFdbbd0f, 36),
        POTION(0xFF2c65f5, 37),
        INPUT(0xFF1fff26, 38),
        OUTPUT(0xFFde5d07, 39);

        public static final Codec<Slot> CODEC = Codec.STRING.xmap(Slot::valueOf, Slot::name);

        public final int color;
        public final int slotIndex;

        Slot(int color, int slotIndex){
            this.color = color;
            this.slotIndex = slotIndex;
        }

        public Slot next(){
            return values()[(ordinal() + 1) % values().length];
        }
    }
}
