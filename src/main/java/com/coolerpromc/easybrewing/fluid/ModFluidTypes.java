package com.coolerpromc.easybrewing.fluid;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Supplier;

public class ModFluidTypes {
    public static final Identifier WATER_STILL_RL = Identifier.parse("block/water_still");
    public static final Identifier WATER_FLOWING_RL = Identifier.parse("block/water_flow");
    public static final Identifier WATER_OVERLAY_RL = Identifier.parse("block/water_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, EasyBrewing.MODID);

    public static final Supplier<BaseFluidType> POTION_FLUID = registerFluidType("potion_fluid", new BaseFluidType(WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL, 0xA1343E69, new Vector4f(108f / 255f, 168f / 255f, 212f / 255f, 1f), FluidType.Properties.create()));

    private static <T extends FluidType> Supplier<T> registerFluidType(String name, T fluidType) {
        return FLUID_TYPES.register(name, () -> fluidType);
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
