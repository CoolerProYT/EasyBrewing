package com.coolerpromc.easybrewing.block.entity;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.inventory.ModItemStackHandler;
import com.coolerpromc.easybrewing.inventory.OutputItemStackHandler;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class ItemBrewingStationBE extends BlockEntity implements MenuProvider {
    public final ContainerData data;
    public final ModItemStackHandler fuelHandler = new ModItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(Items.BLAZE_POWDER);
        }
    };
    public final ModItemStackHandler potionHandler = new ModItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isValidPotion(stack);
        }
    };
    public final ModItemStackHandler inputHandler = new ModItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public final OutputItemStackHandler outputHandler = new OutputItemStackHandler(){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public final ModItemStackHandler upgradeHandler = new ModItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()){
                if (slot == 0){
                    handleUpgrade();
                }
                else{
                    getAdditionalAmount();
                }
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 1){
                return stack.getItem() instanceof AmountUpgradeItem;
            }
            return stack.getItem() instanceof SpeedUpgradeItem;
        }
    };

    private LazyOptional<IItemHandler> fuelCap = LazyOptional.of(() -> fuelHandler);
    private LazyOptional<IItemHandler> potionCap = LazyOptional.of(() -> potionHandler);
    private LazyOptional<IItemHandler> inputCap = LazyOptional.of(() -> inputHandler);
    private LazyOptional<IItemHandler> outputCap = LazyOptional.of(() -> outputHandler);

    private int progress = 0;
    private int maxProgress = CommonConfig.CONFIG.processingTime.get();
    public int fuel = 0;
    private float multiplier = 1f;
    public int additionalAmount = 0;
    private Map<Direction, Slot> capabilityBySide = new HashMap<>();

    public ItemBrewingStationBE(BlockPos pos, BlockState blockState) {
        super(EasyBrewing.ITEM_BREWING_STATION_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> ItemBrewingStationBE.this.progress;
                    case 1 -> ItemBrewingStationBE.this.maxProgress;
                    case 2 -> ItemBrewingStationBE.this.fuel;
                    case 3 -> (int) (ItemBrewingStationBE.this.multiplier * 100);
                    case 4 -> CommonConfig.CONFIG.maxSpeedUpgrade.get();
                    case 5 -> additionalAmount;
                    case 6 -> CommonConfig.CONFIG.maxAmountUpgrade.get();
                    case 7 -> CommonConfig.CONFIG.allowAmountUpgrade.get() ? 1 : 0;
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
                    case 4 -> CommonConfig.CONFIG.maxSpeedUpgrade.set(i1);
                    case 5 -> additionalAmount = i1;
                    case 6 -> CommonConfig.CONFIG.maxAmountUpgrade.set(i1);
                    case 7 -> CommonConfig.CONFIG.allowAmountUpgrade.set(i1 == 1);
                }
            }

            @Override
            public int getCount() {
                return 8;
            }
        };

        initCapabilityBySide();
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
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.easybrewing.item_brewing_station");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ItemBrewingStationMenu(i, inventory, this, data);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        fuelHandler.serialize(output.child("fuelHandler"));
        potionHandler.serialize(output.child("potionHandler"));
        inputHandler.serialize(output.child("inputHandler"));
        outputHandler.serialize(output.child("outputHandler"));
        upgradeHandler.serialize(output.child("upgradeHandler"));

        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putInt("fuel", fuel);
        output.putFloat("multiplier", multiplier);
        output.putInt("additionalAmount", additionalAmount);
        output.store("capabilityBySide", ExtraCodecs.strictUnboundedMap(Direction.CODEC, Slot.CODEC), capabilityBySide);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fuelHandler.deserialize(input.childOrEmpty("fuelHandler"));
        potionHandler.deserialize(input.childOrEmpty("potionHandler"));
        inputHandler.deserialize(input.childOrEmpty("inputHandler"));
        outputHandler.deserialize(input.childOrEmpty("outputHandler"));
        upgradeHandler.deserialize(input.childOrEmpty("upgradeHandler"));

        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", 400);
        fuel = input.getIntOr("fuel", 0);
        multiplier = input.getFloatOr("multiplier", 1f);
        additionalAmount = input.getIntOr("additionalAmount", 0);
        capabilityBySide = new HashMap<>(input.read("capabilityBySide", ExtraCodecs.strictUnboundedMap(Direction.CODEC, Slot.CODEC)).orElse(new HashMap<>()));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithFullMetadata(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput tag, HolderLookup.Provider holders) {
        super.handleUpdateTag(tag, holders);
        loadAdditional(tag);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ValueInput data, HolderLookup.Provider lookup) {
        super.onDataPacket(connection, data, lookup);
        loadAdditional(data);
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide()) return;

        handleFuel();
        checkUpgrade();

        if (isBrewable(level.potionBrewing())){
            progress++;
            setChanged(level, pos, blockState);

            if (progress >= maxProgress){
                doBrew(level.potionBrewing());
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                progress = 0;
            }
        }
        else{
            progress = 0;
        }
    }

    private void checkUpgrade(){
        if (!CommonConfig.CONFIG.allowAmountUpgrade.get() && additionalAmount > 0){
            additionalAmount = 0;
        }
        else{
            if (CommonConfig.CONFIG.allowAmountUpgrade.get() && additionalAmount == 0 && upgradeHandler.getStackInSlot(1).getCount() > 0){
                getAdditionalAmount();
            }
        }
    }

    private void handleUpgrade(){
        maxProgress = CommonConfig.CONFIG.processingTime.get();
        ItemStack upgradeStack = upgradeHandler.getStackInSlot(0);

        if (!upgradeStack.isEmpty() && upgradeStack.getItem() instanceof SpeedUpgradeItem upgradeItem){
            float multiplier = upgradeItem.getSpeedMultiplier();
            float baseMultiplier = 1f;
            int upgradeCount = Math.min(CommonConfig.CONFIG.maxSpeedUpgrade.get(), upgradeStack.getCount());

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
            ItemStack fuelStack = fuelHandler.getStackInSlot(0);
            if (fuelStack.is(Items.BLAZE_POWDER)){
                fuel = 20;
                fuelHandler.extractItem(0, 1, false);
            }
        }
    }

    private boolean isBrewable(PotionBrewing potionBrewing) {
        ItemStack ingredient = inputHandler.getStackInSlot(0);
        if (ingredient.isEmpty()) {
            return false;
        } else if (!potionBrewing.isIngredient(ingredient)) {
            return false;
        } else {
            ItemStack potion = potionHandler.getStackInSlot(0).copyWithCount(1);
            return !potion.isEmpty() && potionBrewing.hasMix(potion, ingredient) && hasEnoughInput() && canInsertIntoOutputSlot(potionBrewing) && hasFuel();
        }
    }

    public void getAdditionalAmount(){
        if (CommonConfig.CONFIG.allowAmountUpgrade.get() && upgradeHandler.getStackInSlot(1).getItem() instanceof AmountUpgradeItem upgradeItem){
            int effectiveUpgrade = Math.min(upgradeHandler.getStackInSlot(1).getCount(), CommonConfig.CONFIG.maxAmountUpgrade.get());
            additionalAmount = upgradeItem.getAmount() * effectiveUpgrade;
        }
        else{
            additionalAmount = 0;
        }
    }

    private boolean hasEnoughInput(){
        return potionHandler.getStackInSlot(0).getCount() >= CommonConfig.CONFIG.potionCount() + additionalAmount;
    }

    public boolean hasFuel(){
        return fuel > 0;
    }

    private boolean canInsertIntoOutputSlot(PotionBrewing potionbrewing){
        ItemStack output = potionbrewing.mix(inputHandler.getStackInSlot(0), potionHandler.getStackInSlot(0));
        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);
        return outputHandler.innerInsertItem(0, output, true).isEmpty();
    }

    private void doBrew(PotionBrewing potionbrewing) {
        ItemStack ingredient = inputHandler.getStackInSlot(0);
        ItemStack potion = potionHandler.getStackInSlot(0).copyWithCount(1);
        ItemStack output = potionbrewing.mix(ingredient, potion);

        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);

        if (!ingredient.getCraftingRemainder().isEmpty()) {
            ItemStack leftover = ingredient.getCraftingRemainder();
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                ingredient = leftover;
            } else {
                Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), leftover);
            }
        } else {
            ingredient.shrink(1);
        }

        inputHandler.setStackInSlot(0, ingredient);
        potionHandler.extractItem(0, CommonConfig.CONFIG.potionCount() + additionalAmount, false);
        outputHandler.innerInsertItem(0, output, false);
        fuel--;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && side != null) {
            return switch (capabilityBySide.get(side)) {
                case FUEL -> fuelCap.cast();
                case POTION -> potionCap.cast();
                case INPUT -> inputCap.cast();
                case OUTPUT -> outputCap.cast();
            };
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fuelCap.invalidate();
        potionCap.invalidate();
        inputCap.invalidate();
        outputCap.invalidate();
    }

    private void recreateCapabilities() {
        fuelCap = LazyOptional.of(() -> fuelHandler);
        potionCap = LazyOptional.of(() -> potionHandler);
        inputCap = LazyOptional.of(() -> inputHandler);
        outputCap = LazyOptional.of(() -> outputHandler);
    }

    public void setCapabilityBySide(Direction direction, Slot slot){
        capabilityBySide.put(direction, slot);
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        invalidateCaps();
        recreateCapabilities();
    }

    public Slot getCapabilityBySide(Direction direction){
        return capabilityBySide.get(direction);
    }

    private boolean isValidPotion(ItemStack stack){
        return stack.getItem() instanceof PotionItem || stack.is(Items.GLASS_BOTTLE);
    }

    public void drops(){
        SimpleContainer container = new SimpleContainer(
                fuelHandler.getStackInSlot(0),
                potionHandler.getStackInSlot(0),
                inputHandler.getStackInSlot(0),
                outputHandler.getStackInSlot(0),
                upgradeHandler.getStackInSlot(0),
                upgradeHandler.getStackInSlot(1)
        );

        Containers.dropContents(level, getBlockPos(), container);
    }

    @Override
    public void preRemoveSideEffects(BlockPos p_397404_, BlockState p_395805_) {
        drops();
    }

    public enum Slot{
        FUEL(0xFFdbbd0f, 36),
        POTION(0xFF2c65f5, 37),
        INPUT(0xFF1fff26, 38),
        OUTPUT(0xFFde5d07, 39);

        public static final Codec<Slot> CODEC = Codec.STRING.xmap(Slot::valueOf, Slot::name);
        public static final StreamCodec<RegistryFriendlyByteBuf, Slot> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

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