package com.coolerpromc.easybrewing.block.entity;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.cobblemon.CobblemonBottleIngredientCheck;
import com.coolerpromc.easybrewing.compat.cobblemon.CobblemonRecipeCheck;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.inventory.OutputItemStackHandler;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;import java.util.HashMap;
import java.util.Map;import static com.coolerpromc.easybrewing.block.ItemBrewingStationBlock.FACING;

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

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isIngredientValid(stack);
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

    private final IItemHandler combinedHandler = new CombinedInvWrapper(inputHandler, fuelHandler, potionHandler, outputHandler);

    private int progress = 0;
    private int maxProgress = CommonConfig.CONFIG.processingTime.getAsInt();
    public int fuel = 0;
    private float multiplier = 1f;
    public int additionalAmount = 0;
    private Map<RelativeSide, Slot> capabilityBySide = new EnumMap<>(RelativeSide.class);

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
        capabilityBySide.putIfAbsent(RelativeSide.BOTTOM, Slot.OUTPUT);
        capabilityBySide.putIfAbsent(RelativeSide.TOP, Slot.FUEL);
        capabilityBySide.putIfAbsent(RelativeSide.LEFT, Slot.POTION);
        capabilityBySide.putIfAbsent(RelativeSide.RIGHT, Slot.POTION);
        capabilityBySide.putIfAbsent(RelativeSide.FRONT, Slot.INPUT);
        capabilityBySide.putIfAbsent(RelativeSide.BACK, Slot.INPUT);
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
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("fuelHandler", fuelHandler.serializeNBT(registries));
        tag.put("potionHandler", potionHandler.serializeNBT(registries));
        tag.put("inputHandler", inputHandler.serializeNBT(registries));
        tag.put("outputHandler", outputHandler.serializeNBT(registries));
        tag.put("upgradeHandler", upgradeHandler.serializeNBT(registries));

        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putInt("fuel", fuel);
        tag.putFloat("multiplier", multiplier);
        tag.putInt("additionalAmount", additionalAmount);
        tag.put("capabilityBySide", ExtraCodecs.strictUnboundedMap(RelativeSide.CODEC, Slot.CODEC).encodeStart(NbtOps.INSTANCE, capabilityBySide).getOrThrow());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        fuelHandler.deserializeNBT(registries, tag.getCompound("fuelHandler"));
        potionHandler.deserializeNBT(registries, tag.getCompound("potionHandler"));
        inputHandler.deserializeNBT(registries, tag.getCompound("inputHandler"));
        outputHandler.deserializeNBT(registries, tag.getCompound("outputHandler"));
        upgradeHandler.deserializeNBT(registries, tag.getCompound("upgradeHandler"));

        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        fuel = tag.getInt("fuel");
        multiplier = tag.getFloat("multiplier");
        additionalAmount = tag.getInt("additionalAmount");
        capabilityBySide = new HashMap<>(ExtraCodecs.strictUnboundedMap(RelativeSide.CODEC, Slot.CODEC).parse(NbtOps.INSTANCE, tag.getCompound("capabilityBySide")).getOrThrow());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide()) return;

        handleFuel();
        checkUpgrade();

        if (isBrewable(level.potionBrewing()) || (ModList.get().isLoaded("cobblemon") && CobblemonRecipeCheck.hasRecipe(this))){
            progress++;
            setChanged(level, pos, blockState);

            if (progress >= maxProgress){
                if (isBrewable(level.potionBrewing())){
                    doBrew(level.potionBrewing());
                }
                else if (ModList.get().isLoaded("cobblemon")){
                    CobblemonRecipeCheck.craft(this);
                }
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
        maxProgress = CommonConfig.CONFIG.processingTime.getAsInt();
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
            ItemStack potion = potionHandler.getStackInSlot(0);
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
        ItemStack potion = potionHandler.getStackInSlot(0);
        ItemStack output = potionbrewing.mix(ingredient, potion);

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

    public @Nullable IItemHandler getCapability(@Nullable Direction direction) {
        if (direction == null) return combinedHandler;

        Direction facing = getBlockState().getValue(FACING);
        RelativeSide side = getRelativeSide(direction, facing);
        if (side == null) return null;

        Slot slot = capabilityBySide.get(side);
        if (slot == null) return null;

        return switch (slot){
            case FUEL -> fuelHandler;
            case POTION -> potionHandler;
            case INPUT -> inputHandler;
            case OUTPUT -> outputHandler;
        };
    }

    private RelativeSide getRelativeSide(Direction worldSide, Direction facing) {
        if (worldSide == Direction.UP) return RelativeSide.TOP;
        if (worldSide == Direction.DOWN) return RelativeSide.BOTTOM;

        if (worldSide == facing) {
            return RelativeSide.FRONT;
        }
        if (worldSide == facing.getOpposite()) {
            return RelativeSide.BACK;
        }
        if (worldSide == facing.getCounterClockWise()) {
            return RelativeSide.RIGHT;
        }
        if (worldSide == facing.getClockWise()) {
            return RelativeSide.LEFT;
        }

        return null;
    }

    public void setCapabilityBySide(RelativeSide direction, Slot slot){
        capabilityBySide.put(direction, slot);
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        level.invalidateCapabilities(getBlockPos());
    }

    public Slot getCapabilityBySide(RelativeSide direction){
        return capabilityBySide.get(direction);
    }

    private boolean isValidPotion(ItemStack stack){
        return level.potionBrewing().isInput(stack) || (ModList.get().isLoaded("cobblemon") && CobblemonBottleIngredientCheck.isCobblemonBottle(stack, level));
    }

    private boolean isIngredientValid(ItemStack stack){
        return level.potionBrewing().isIngredient(stack) || (ModList.get().isLoaded("cobblemon") && CobblemonBottleIngredientCheck.isCobblemonIngredient(stack, level));
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

    public enum RelativeSide {
        FRONT,
        BACK,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

        public static final Codec<RelativeSide> CODEC = Codec.STRING.xmap(RelativeSide::valueOf, RelativeSide::name);
        public static final StreamCodec<RegistryFriendlyByteBuf, RelativeSide> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    }
}
