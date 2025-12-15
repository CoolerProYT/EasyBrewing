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
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class ItemBrewingStationBE extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {
    public final ContainerData data;
    public final SimpleContainer fuelHandler = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return stack.is(Items.BLAZE_POWDER);
        }
    };
    public final SimpleContainer potionHandler = new SimpleContainer(1){
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return isValidPotion(stack);
        }
    };
    public final SimpleContainer inputHandler = new SimpleContainer(1);
    public final OutputItemStackHandler outputHandler = new OutputItemStackHandler();
    public final SimpleContainer upgradeHandler = new SimpleContainer(2){
        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
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
        this.data = new ContainerData() {
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
            public int getCount() {
                return 8;
            }
        };

        initCapabilityBySide();

        upgradeHandler.addListener(container -> {
            handleUpgrade();
            getAdditionalAmount();
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
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.easybrewing.item_brewing_station");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ItemBrewingStationMenu(i, inventory, this, data);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.getBlockPos();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("fuelHandler", ContainerHelper.saveAllItems(new CompoundTag(), fuelHandler.items, registries));
        tag.put("potionHandler", ContainerHelper.saveAllItems(new CompoundTag(), potionHandler.items, registries));
        tag.put("inputHandler", ContainerHelper.saveAllItems(new CompoundTag(), inputHandler.items, registries));
        tag.put("outputHandler", ContainerHelper.saveAllItems(new CompoundTag(), outputHandler.items, registries));
        tag.put("upgradeHandler", ContainerHelper.saveAllItems(new CompoundTag(), upgradeHandler.items, registries));

        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putInt("fuel", fuel);
        tag.putFloat("multiplier", multiplier);
        tag.putInt("additionalAmount", additionalAmount);
        tag.put("capabilityBySide", ExtraCodecs.strictUnboundedMap(Direction.CODEC, Slot.CODEC).encodeStart(NbtOps.INSTANCE, capabilityBySide).getOrThrow());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        ContainerHelper.loadAllItems(tag.getCompound("fuelHandler"), fuelHandler.items, registries);
        ContainerHelper.loadAllItems(tag.getCompound("potionHandler"), potionHandler.items, registries);
        ContainerHelper.loadAllItems(tag.getCompound("inputHandler"), inputHandler.items, registries);
        ContainerHelper.loadAllItems(tag.getCompound("outputHandler"), outputHandler.items, registries);
        ContainerHelper.loadAllItems(tag.getCompound("upgradeHandler"), upgradeHandler.items, registries);

        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        fuel = tag.getInt("fuel");
        multiplier = tag.getFloat("multiplier");
        additionalAmount = tag.getInt("additionalAmount");
        capabilityBySide = new HashMap<>(ExtraCodecs.strictUnboundedMap(Direction.CODEC, Slot.CODEC).parse(NbtOps.INSTANCE, tag.getCompound("capabilityBySide")).getOrThrow());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide()) return;

        handleFuel();
        checkUpgrade();

        if (isBrewable(level.potionBrewing()) || (FabricLoader.getInstance().isModLoaded("cobblemon") && CobblemonRecipeCheck.hasRecipe(this))){
            progress++;
            setChanged(level, pos, blockState);

            if (progress >= maxProgress){
                if (isBrewable(level.potionBrewing())){
                    doBrew(level.potionBrewing());
                }
                else if (FabricLoader.getInstance().isModLoaded("cobblemon")){
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
        ItemStack upgradeStack = upgradeHandler.getItem(0);

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
            ItemStack fuelStack = fuelHandler.getItem(0);
            if (fuelStack.is(Items.BLAZE_POWDER)){
                fuel = 20;
                try(Transaction tx = Transaction.openOuter()){
                    long extracted = fuelStorage.extract(ItemVariant.of(fuelHandler.getItem(0)), 1, tx);
                    if (extracted == 1){
                        tx.commit();
                    }
                }
            }
        }
    }

    private boolean isBrewable(PotionBrewing potionBrewing) {
        ItemStack ingredient = inputHandler.getItem(0);
        if (ingredient.isEmpty()) {
            return false;
        } else if (!potionBrewing.isIngredient(ingredient)) {
            return false;
        } else {
            ItemStack potion = potionHandler.getItem(0);
            return !potion.isEmpty() && potionBrewing.hasMix(potion, ingredient) && hasEnoughInput() && canInsertIntoOutputSlot(potionBrewing) && hasFuel();
        }
    }

    public void getAdditionalAmount(){
        if (CommonConfig.CONFIG.allowAmountUpgrade && upgradeHandler.getItem(1).getItem() instanceof AmountUpgradeItem upgradeItem){
            int effectiveUpgrade = Math.min(upgradeHandler.getItem(1).getCount(), CommonConfig.CONFIG.maxAmountUpgrade);
            additionalAmount = upgradeItem.getAmount() * effectiveUpgrade;
        }
        else{
            additionalAmount = 0;
        }
    }

    private boolean hasEnoughInput(){
        return potionHandler.getItem(0).getCount() >= CommonConfig.CONFIG.potionCount() + additionalAmount;
    }

    public boolean hasFuel(){
        return fuel > 0;
    }

    private boolean canInsertIntoOutputSlot(PotionBrewing potionbrewing){
        ItemStack output = potionbrewing.mix(inputHandler.getItem(0), potionHandler.getItem(0));
        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);
        try(Transaction tx = Transaction.openOuter()){
            long inserted = outputStorage.insert(ItemVariant.of(output), output.getCount(), tx);
            return inserted == output.getCount();
        }
    }

    private void doBrew(PotionBrewing potionbrewing) {
        ItemStack ingredient = inputHandler.getItem(0);
        ItemStack potion = potionHandler.getItem(0);
        ItemStack output = potionbrewing.mix(ingredient, potion);

        output.setCount(CommonConfig.CONFIG.potionCount() + additionalAmount);

        if (!ingredient.getRecipeRemainder().isEmpty()) {
            ItemStack leftover = ingredient.getRecipeRemainder();
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                ingredient = leftover;
            } else {
                Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), leftover);
            }
        } else {
            ingredient.shrink(1);
        }

        inputHandler.setItem(0, ingredient);
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
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public Slot getCapabilityBySide(Direction direction){
        return capabilityBySide.get(direction);
    }

    private boolean isValidPotion(ItemStack stack){
        return stack.getItem() instanceof PotionItem || stack.is(Items.GLASS_BOTTLE) || (FabricLoader.getInstance().isModLoaded("cobblemon") && CobblemonBottleIngredientCheck.isCobblemonBottle(stack, level));
    }

    public void drops(){
        SimpleContainer container = new SimpleContainer(
                fuelHandler.getItem(0),
                potionHandler.getItem(0),
                inputHandler.getItem(0),
                outputHandler.getItem(0),
                upgradeHandler.getItem(0)
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
}
