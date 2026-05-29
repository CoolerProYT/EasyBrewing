package com.coolerpromc.easybrewing.compat.create;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class BrewingStationArmPoint extends ArmInteractionPoint {
    public BrewingStationArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    @Nullable
    protected IItemHandler getHandler(ArmBlockEntity armBlockEntity) {
        if (cachedHandler == null && level instanceof ServerLevel serverLevel) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null)
                return null;

            cachedHandler = BlockCapabilityCache.create(
                    Capabilities.ItemHandler.BLOCK,
                    serverLevel,
                    pos,
                    null,
                    () -> !armBlockEntity.isRemoved(),
                    () -> cachedHandler = null
            );
        }


        if (getMode() == Mode.TAKE && level.getBlockEntity(pos) instanceof ItemBrewingStationBE itemBrewingStationBE) {
            return itemBrewingStationBE.outputHandler;
        }

        return cachedHandler == null ? null : cachedHandler.getCapability();
    }
}