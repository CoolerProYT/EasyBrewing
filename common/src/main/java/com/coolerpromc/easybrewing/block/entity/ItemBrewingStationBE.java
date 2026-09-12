package com.coolerpromc.easybrewing.block.entity;

import com.coolerpromc.easybrewing.CommonClass;
import com.coolerpromc.easybrewing.compat.cobblemon.CobblemonBottleIngredientCheck;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.inventory.OutputItemStackHandler;
import com.coolerpromc.easybrewing.inventory.SimpleItemHandler;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.platform.Services;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.coolerpromc.easybrewing.block.ItemBrewingStationBlock.FACING;

@SuppressWarnings("ConstantConditions")
public class ItemBrewingStationBE extends BlockEntity implements MenuProvider {
    public final ContainerData data;
    public final SimpleItemHandler fuelHandler = new SimpleItemHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return stack.is(Items.BLAZE_POWDER);
        }
    };
    public final SimpleItemHandler potionHandler = new SimpleItemHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return isValidPotion(stack);
        }
    };
    public final SimpleItemHandler inputHandler = new SimpleItemHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return isIngredientValid(stack);
        }
    };
    public final OutputItemStackHandler outputHandler = new OutputItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public final SimpleItemHandler upgradeHandler = new SimpleItemHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                if (slot == 0) {
                    handleUpgrade();
                } else {
                    getAdditionalAmount();
                }
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            if (slot == 1) {
                return stack.getItem() instanceof AmountUpgradeItem;
            }
            return stack.getItem() instanceof SpeedUpgradeItem;
        }
    };

    private int progress = 0;
    private int maxProgress = CommonConfig.processingTime;
    public int fuel = 0;
    private float multiplier = 1f;
    public int additionalAmount = 0;
    private Map<RelativeSide, Slot> capabilityBySide = new EnumMap<>(RelativeSide.class);
    private final RecipeManager.CachedCheck<BrewingInput, BrewingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.BREWING);

    public ItemBrewingStationBE(BlockPos pos, BlockState blockState) {
        super(CommonClass.ITEM_BREWING_STATION_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> ItemBrewingStationBE.this.progress;
                    case 1 -> ItemBrewingStationBE.this.maxProgress;
                    case 2 -> ItemBrewingStationBE.this.fuel;
                    case 3 -> (int) (ItemBrewingStationBE.this.multiplier * 100);
                    case 4 -> CommonConfig.maxSpeedUpgrade;
                    case 5 -> additionalAmount;
                    case 6 -> CommonConfig.maxAmountUpgrade;
                    case 7 -> CommonConfig.allowAmountUpgrade ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i) {
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

    public void initCapabilityBySide() {
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
        initCapabilityBySide();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }


    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;

        handleFuel();
        checkUpgrade();

        if (isBrewable(serverLevel) || (Services.PLATFORM.isModLoaded("cobblemon")/* && CobblemonRecipeCheck.hasRecipe(this)*/)) {
            progress++;
            setChanged(level, pos, blockState);

            if (progress >= maxProgress) {
                if (isBrewable(serverLevel)) {
                    doBrew(serverLevel);
                }
                /*else if (Services.PLATFORM.isModLoaded("cobblemon")){
                    CobblemonRecipeCheck.craft(this);
                }*/
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                progress = 0;
            }
        } else {
            progress = 0;
        }
    }

    private void checkUpgrade() {
        if (!CommonConfig.allowAmountUpgrade && additionalAmount > 0) {
            additionalAmount = 0;
        } else {
            if (CommonConfig.allowAmountUpgrade && additionalAmount == 0 && !upgradeHandler.getItem(1).isEmpty()) {
                getAdditionalAmount();
            }
        }
    }

    private void handleUpgrade() {
        maxProgress = CommonConfig.processingTime;
        ItemStack upgradeStack = upgradeHandler.getItem(0);

        if (!upgradeStack.isEmpty() && upgradeStack.getItem() instanceof SpeedUpgradeItem upgradeItem) {
            float multiplier = upgradeItem.getSpeedMultiplier();
            float baseMultiplier = 1f;
            int upgradeCount = Math.min(CommonConfig.maxSpeedUpgrade, upgradeStack.getCount());

            for (int i = 0; i < upgradeCount; i++) {
                baseMultiplier *= multiplier;
            }

            this.multiplier = baseMultiplier;
            maxProgress = (int) (maxProgress / this.multiplier);
        } else {
            this.multiplier = 1f;
        }
    }

    private void handleFuel() {
        if (fuel <= 0) {
            ItemStack fuelStack = fuelHandler.getItem(0);
            if (fuelStack.is(Items.BLAZE_POWDER)) {
                fuelHandler.removeItem(0, 1);
                fuel = 20;
            }
        }
    }

    private Optional<RecipeHolder<BrewingRecipe>> getRecipe(ServerLevel level) {
        ItemStack ingredient = inputHandler.getItem(0);
        ItemStack potion = potionHandler.getItem(0);
        if (ingredient.isEmpty() || potion.isEmpty()) {
            return Optional.empty();
        }

        return quickCheck.getRecipeFor(new BrewingInput(potion, ingredient), level);
    }

    private boolean isBrewable(ServerLevel level) {
        Optional<RecipeHolder<BrewingRecipe>> recipe = getRecipe(level);
        return recipe.isPresent() && hasEnoughInput() && canInsertIntoOutputSlot(recipe.get().value()) && hasFuel();
    }

    public void getAdditionalAmount() {
        if (CommonConfig.allowAmountUpgrade && upgradeHandler.getItem(1).getItem() instanceof AmountUpgradeItem upgradeItem) {
            int effectiveUpgrade = Math.min(upgradeHandler.getItem(1).getCount(), CommonConfig.maxAmountUpgrade);
            additionalAmount = upgradeItem.getAmount() * effectiveUpgrade;
        } else {
            additionalAmount = 0;
        }
    }

    private boolean hasEnoughInput() {
        return potionHandler.getItem(0).getCount() >= CommonConfig.potionCount() + additionalAmount;
    }

    public boolean hasFuel() {
        return fuel > 0;
    }

    private boolean canInsertIntoOutputSlot(BrewingRecipe recipe) {
        ItemStack output = recipe.getOutput().create();
        output.setCount(CommonConfig.potionCount() + additionalAmount);
        return outputHandler.innerInsertItem(0, output, true) == output.getCount();
    }

    private void doBrew(ServerLevel level) {
        Optional<RecipeHolder<BrewingRecipe>> recipe = getRecipe(level);
        if (recipe.isEmpty()) return;

        ItemStack ingredient = inputHandler.getItem(0).copy();
        ItemStack output = recipe.get().value().getOutput().create();

        output.setCount(CommonConfig.potionCount() + additionalAmount);

        if (ingredient.getItem().getCraftingRemainder() != null) {
            ItemStack leftover = ingredient.getItem().getCraftingRemainder().create();
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
        potionHandler.removeItem(0, CommonConfig.potionCount() + additionalAmount);
        outputHandler.innerInsertItem(0, output, false);
        fuel--;
    }

    /**
     * Returns the handler for the given side. Used by platform-specific capability registration.
     */
    public @Nullable Container getHandlerForSide(@Nullable Direction direction) {
        if (direction == null) return null;

        Direction facing = getBlockState().getValue(FACING);
        RelativeSide side = getRelativeSide(direction, facing);
        if (side == null) return null;

        Slot slot = capabilityBySide.get(side);
        if (slot == null) return null;

        return switch (slot) {
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

    public void setCapabilityBySide(RelativeSide direction, Slot slot) {
        capabilityBySide.put(direction, slot);
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public Slot getCapabilityBySide(RelativeSide direction) {
        return capabilityBySide.get(direction);
    }

    private boolean isValidPotion(ItemStack stack) {
        return Services.POTION.isInput(level, stack) || (Services.PLATFORM.isModLoaded("cobblemon") && CobblemonBottleIngredientCheck.isCobblemonBottle(stack, level));
    }

    private boolean isIngredientValid(ItemStack stack){
        return Services.POTION.isIngredient(level, stack) || (Services.PLATFORM.isModLoaded("cobblemon") && CobblemonBottleIngredientCheck.isCobblemonIngredient(stack, level));
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(
                fuelHandler.getItem(0),
                potionHandler.getItem(0),
                inputHandler.getItem(0),
                outputHandler.getItem(0),
                upgradeHandler.getItem(0),
                upgradeHandler.getItem(1)
        );

        Containers.dropContents(level, getBlockPos(), container);
    }

    public enum Slot {
        FUEL(0xFFdbbd0f, 36),
        POTION(0xFF2c65f5, 37),
        INPUT(0xFF1fff26, 38),
        OUTPUT(0xFFde5d07, 39);

        public static final Codec<Slot> CODEC = Codec.STRING.xmap(Slot::valueOf, Slot::name);
        public static final StreamCodec<RegistryFriendlyByteBuf, Slot> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

        public final int color;
        public final int slotIndex;

        Slot(int color, int slotIndex) {
            this.color = color;
            this.slotIndex = slotIndex;
        }

        public Slot next() {
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
