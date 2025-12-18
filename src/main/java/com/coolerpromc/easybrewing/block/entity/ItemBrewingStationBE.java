package com.coolerpromc.easybrewing.block.entity;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.inventory.OutputItemStackHandler;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class ItemBrewingStationBE extends BlockEntity implements MenuProvider {
    public final ContainerData data;
    public final ItemStackHandler fuelHandler = new ItemStackHandler(1){
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
    public final ItemStackHandler potionHandler = new ItemStackHandler(1){
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
    public final ItemStackHandler inputHandler = new ItemStackHandler(1){
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
    public final ItemStackHandler upgradeHandler = new ItemStackHandler(2){
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
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("fuelHandler", fuelHandler.serializeNBT());
        tag.put("potionHandler", potionHandler.serializeNBT());
        tag.put("inputHandler", inputHandler.serializeNBT());
        tag.put("outputHandler", outputHandler.serializeNBT());
        tag.put("upgradeHandler", upgradeHandler.serializeNBT());

        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putInt("fuel", fuel);
        tag.putFloat("multiplier", multiplier);
        tag.putInt("additionalAmount", additionalAmount);
        tag.put("capabilityBySide", Codec.unboundedMap(Direction.CODEC, Slot.CODEC).encodeStart(NbtOps.INSTANCE, capabilityBySide).getOrThrow(false, EasyBrewing.LOGGER::error));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        fuelHandler.deserializeNBT(tag.getCompound("fuelHandler"));
        potionHandler.deserializeNBT(tag.getCompound("potionHandler"));
        inputHandler.deserializeNBT(tag.getCompound("inputHandler"));
        outputHandler.deserializeNBT(tag.getCompound("outputHandler"));
        upgradeHandler.deserializeNBT(tag.getCompound("upgradeHandler"));

        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        fuel = tag.getInt("fuel");
        multiplier = tag.getFloat("multiplier");
        additionalAmount = tag.getInt("additionalAmount");
        capabilityBySide = new HashMap<>(Codec.unboundedMap(Direction.CODEC, Slot.CODEC).parse(NbtOps.INSTANCE, tag.getCompound("capabilityBySide")).getOrThrow(false, EasyBrewing.LOGGER::error));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        load(tag);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide()) return;

        handleFuel();
        checkUpgrade();

        if (isBrewable() || isBrewableForge()){
            progress++;
            setChanged(level, pos, blockState);

            if (progress >= maxProgress){
                if (isBrewable()) doBrew();
                else doBrewForge();
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

    private boolean isBrewable() {
        ItemStack ingredient = inputHandler.getStackInSlot(0);
        if (ingredient.isEmpty()) {
            return false;
        } else if (!PotionBrewing.isIngredient(ingredient)) {
            return false;
        } else {
            ItemStack potion = potionHandler.getStackInSlot(0);
            return !potion.isEmpty() && (PotionBrewing.hasMix(potion, ingredient)) && hasEnoughInput() && canInsertIntoOutputSlot() && hasFuel();
        }
    }

    private boolean isBrewableForge() {
        ItemStack ingredient = inputHandler.getStackInSlot(0);
        if (ingredient.isEmpty()) {
            return false;
        } else if (!BrewingRecipeRegistry.isValidIngredient(ingredient)) {
            return false;
        } else {
            ItemStack potion = potionHandler.getStackInSlot(0);
            return !potion.isEmpty() && !BrewingRecipeRegistry.getOutput(potion.copyWithCount(1), ingredient.copyWithCount(1)).isEmpty() && hasEnoughInput() && canInsertIntoOutputSlotForge() && hasFuel();
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

    private boolean canInsertIntoOutputSlot(){
        ItemStack output = PotionBrewing.mix(inputHandler.getStackInSlot(0), potionHandler.getStackInSlot(0));
        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);
        return outputHandler.innerInsertItem(0, output, true).isEmpty();
    }

    private boolean canInsertIntoOutputSlotForge(){
        ItemStack output = BrewingRecipeRegistry.getOutput(potionHandler.getStackInSlot(0).copyWithCount(1), inputHandler.getStackInSlot(0));
        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);
        return outputHandler.innerInsertItem(0, output, true).isEmpty();
    }

    private void doBrew() {
        ItemStack ingredient = inputHandler.getStackInSlot(0);
        ItemStack potion = potionHandler.getStackInSlot(0);
        ItemStack output = PotionBrewing.mix(ingredient, potion);

        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);

        if (ingredient.hasCraftingRemainingItem()) {
            ItemStack leftover = ingredient.getCraftingRemainingItem();
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

    private void doBrewForge() {
        ItemStack ingredient = inputHandler.getStackInSlot(0);
        ItemStack potion = potionHandler.getStackInSlot(0);
        ItemStack output = BrewingRecipeRegistry.getOutput(potion.copyWithCount(1), ingredient);

        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);

        if (ingredient.hasCraftingRemainingItem()) {
            ItemStack leftover = ingredient.getCraftingRemainingItem();
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
        return stack.getItem() instanceof PotionItem || stack.is(Items.GLASS_BOTTLE) || BrewingRecipeRegistry.isValidInput(stack);
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

    public enum Slot{
        FUEL(0xFFdbbd0f, 36),
        POTION(0xFF2c65f5, 37),
        INPUT(0xFF1fff26, 38),
        OUTPUT(0xFFde5d07, 39);

        public static final Codec<Slot> CODEC = Codec.STRING.xmap(Slot::valueOf, Slot::name);

        public void encode(FriendlyByteBuf buf){
            buf.writeEnum(this);
        }

        public static Slot decode(FriendlyByteBuf buf){
            return buf.readEnum(Slot.class);
        }

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
