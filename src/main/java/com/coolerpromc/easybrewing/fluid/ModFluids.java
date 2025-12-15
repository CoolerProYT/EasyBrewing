package com.coolerpromc.easybrewing.fluid;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, EasyBrewing.MODID);

    /*public static final Supplier<FlowingFluid> SOURCE_POTION_FLUID = FLUIDS.register("source_potion_fluid", () -> new BaseFlowingFluid.Source(ModFluids.BLACK_OPAL_WATER_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_POTION_FLUID = FLUIDS.register("flowing_potion_fluid", () -> new BaseFlowingFluid.Flowing(ModFluids.BLACK_OPAL_WATER_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> SOURCE_POTION_BLOCK = EasyBrewing.BLOCKS.register("source_potion_block", () -> new LiquidBlock(ModFluids.SOURCE_POTION_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> SOURCE_POTION_BUCKET = EasyBrewing.ITEMS.registerItem("source_potion_bucket", properties -> new BucketItem(ModFluids.SOURCE_POTION_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final BaseFlowingFluid.Properties BLACK_OPAL_WATER_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.POTION_FLUID, SOURCE_POTION_FLUID, FLOWING_POTION_FLUID).slopeFindDistance(2).levelDecreasePerBlock(1).block(ModFluids.SOURCE_POTION_BLOCK).bucket(ModFluids.SOURCE_POTION_BUCKET);
*/
    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}