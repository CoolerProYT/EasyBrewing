package com.coolerpromc.easybrewing.block.entity;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.cobblemon.CobblemonBottleIngredientCheck;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;import java.util.HashMap;
import java.util.Map;import static com.coolerpromc.easybrewing.block.ItemBrewingStationBlock.FACING;

@SuppressWarnings("ConstantConditions")
public class ItemBrewingStationBE extends BlockEntity implements MenuProvider {
    public final ContainerData data;
    public final ItemStacksResourceHandler fuelHandler = new ItemStacksResourceHandler(1){
        @Override
        protected void onContentsChanged(int slot, ItemStack prevContent) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return resource.is(Items.BLAZE_POWDER);
        }
    };
    public final ItemStacksResourceHandler potionHandler = new ItemStacksResourceHandler(1){
        @Override
        protected void onContentsChanged(int slot, ItemStack prevContent) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return isValidPotion(resource);
        }
    };
    public final ItemStacksResourceHandler inputHandler = new ItemStacksResourceHandler(1){
        @Override
        protected void onContentsChanged(int slot, ItemStack prevContent) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public final OutputItemStackHandler outputHandler = new OutputItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot, ItemStack prevContent) {
            setChanged();
            if (!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public final ItemStacksResourceHandler upgradeHandler = new ItemStacksResourceHandler(2){
        @Override
        protected void onContentsChanged(int slot, ItemStack prevContent) {
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
        public boolean isValid(int index, ItemResource resource) {
            if (index == 1){
                return resource.getItem() instanceof AmountUpgradeItem;
            }
            return resource.getItem() instanceof SpeedUpgradeItem;
        }
    };

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
        output.store("capabilityBySide", ExtraCodecs.strictUnboundedMap(RelativeSide.CODEC, Slot.CODEC), capabilityBySide);
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
        capabilityBySide = new HashMap<>(input.read("capabilityBySide", ExtraCodecs.strictUnboundedMap(RelativeSide.CODEC, Slot.CODEC)).orElse(new HashMap<>()));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
        loadAdditional(input);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide()) return;

        handleFuel();
        checkUpgrade();

        if (isBrewable(level.potionBrewing()) || (ModList.get().isLoaded("cobblemon")/* && CobblemonRecipeCheck.hasRecipe(this)*/)){
            progress++;
            setChanged(level, pos, blockState);

            if (progress >= maxProgress){
                if (isBrewable(level.potionBrewing())){
                    doBrew(level.potionBrewing());
                }
                /*else if (ModList.get().isLoaded("cobblemon")){
                    CobblemonRecipeCheck.craft(this);
                }*/
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
            if (CommonConfig.CONFIG.allowAmountUpgrade.get() && additionalAmount == 0 && upgradeHandler.getAmountAsInt(1) > 0){
                getAdditionalAmount();
            }
        }
    }

    private void handleUpgrade(){
        maxProgress = CommonConfig.CONFIG.processingTime.getAsInt();
        ItemStack upgradeStack = upgradeHandler.getResource(0).toStack(upgradeHandler.getAmountAsInt(0));

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
            ItemResource fuelStack = fuelHandler.getResource(0);
            if (fuelStack.is(Items.BLAZE_POWDER)){
                try(Transaction tx = Transaction.openRoot()){
                    if (fuelHandler.extract(0, fuelStack, 1, tx) == 1){
                        fuel = 20;
                        tx.commit();
                    }
                }
            }
        }
    }

    private boolean isBrewable(PotionBrewing potionBrewing) {
        ItemStack ingredient = inputHandler.getResource(0).toStack(inputHandler.getAmountAsInt(0));
        if (ingredient.isEmpty()) {
            return false;
        } else if (!potionBrewing.isIngredient(ingredient)) {
            return false;
        } else {
            ItemStack potion = potionHandler.getResource(0).toStack(inputHandler.getAmountAsInt(0));
            return !potion.isEmpty() && potionBrewing.hasMix(potion, ingredient) && hasEnoughInput() && canInsertIntoOutputSlot(potionBrewing) && hasFuel();
        }
    }

    public void getAdditionalAmount(){
        if (CommonConfig.CONFIG.allowAmountUpgrade.get() && upgradeHandler.getResource(1).getItem() instanceof AmountUpgradeItem upgradeItem){
            int effectiveUpgrade = Math.min(upgradeHandler.getAmountAsInt(1), CommonConfig.CONFIG.maxAmountUpgrade.get());
            additionalAmount = upgradeItem.getAmount() * effectiveUpgrade;
        }
        else{
            additionalAmount = 0;
        }
    }

    private boolean hasEnoughInput(){
        return potionHandler.getAmountAsInt(0) >= CommonConfig.CONFIG.potionCount() + additionalAmount;
    }

    public boolean hasFuel(){
        return fuel > 0;
    }

    private boolean canInsertIntoOutputSlot(PotionBrewing potionbrewing){
        ItemStack output = potionbrewing.mix(inputHandler.getResource(0).toStack(inputHandler.getAmountAsInt(0)), potionHandler.getResource(0).toStack(potionHandler.getAmountAsInt(0)));
        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);
        try(Transaction tx = Transaction.openRoot()){
            return outputHandler.innerInsert(0, ItemResource.of(output), output.getCount(), tx) == output.getCount();
        }
    }

    private void doBrew(PotionBrewing potionbrewing) {
        ItemStack ingredient = inputHandler.getResource(0).toStack(inputHandler.getAmountAsInt(0));
        ItemStack potion = potionHandler.getResource(0).toStack(potionHandler.getAmountAsInt(0));
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

        inputHandler.set(0, ItemResource.of(ingredient), ingredient.getCount());
        try(Transaction tx = Transaction.openRoot()){
            if (potionHandler.extract(0, ItemResource.of(potion), CommonConfig.CONFIG.potionCount() + additionalAmount, tx) == CommonConfig.CONFIG.potionCount() + additionalAmount){
                tx.commit();
            }
        }
        try(Transaction tx = Transaction.openRoot()){
            if (outputHandler.innerInsert(0, ItemResource.of(output), output.getCount(), tx) == output.getCount()){
                tx.commit();
            }
        }
        fuel--;
    }

    public @Nullable ResourceHandler<ItemResource> getCapability(@Nullable Direction direction) {
        if (direction == null) return null;

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

    private boolean isValidPotion(ItemResource resource){
        return resource.getItem() instanceof PotionItem || resource.is(Items.GLASS_BOTTLE) || (ModList.get().isLoaded("cobblemon") && CobblemonBottleIngredientCheck.isCobblemonBottle(resource, level));
    }

    public void drops(){
        SimpleContainer container = new SimpleContainer(
                fuelHandler.getResource(0).toStack(fuelHandler.getAmountAsInt(0)),
                potionHandler.getResource(0).toStack(potionHandler.getAmountAsInt(0)),
                inputHandler.getResource(0).toStack(inputHandler.getAmountAsInt(0)),
                outputHandler.getResource(0).toStack(outputHandler.getAmountAsInt(0)),
                upgradeHandler.getResource(0).toStack(upgradeHandler.getAmountAsInt(0)),
                upgradeHandler.getResource(1).toStack(upgradeHandler.getAmountAsInt(1))
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

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        drops();
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
