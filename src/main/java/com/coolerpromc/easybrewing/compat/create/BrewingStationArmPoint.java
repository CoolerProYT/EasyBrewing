package com.coolerpromc.easybrewing.compat.create;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class BrewingStationArmPoint extends ArmInteractionPoint {
    public BrewingStationArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    @Nullable
    protected IItemHandler getHandler() {
        if (getMode() == Mode.DEPOSIT && level.getBlockEntity(pos) instanceof ItemBrewingStationBE be) {
            cachedHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
        }

        if (getMode() == Mode.TAKE && level.getBlockEntity(pos) instanceof ItemBrewingStationBE itemBrewingStationBE) {
            return itemBrewingStationBE.outputHandler;
        }

        return cachedHandler == null ? null : cachedHandler.orElse(null);
    }
}