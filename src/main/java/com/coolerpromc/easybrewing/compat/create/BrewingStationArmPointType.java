package com.coolerpromc.easybrewing.compat.create;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BrewingStationArmPointType extends ArmInteractionPointType {
    @Override
    public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
        return state.is(EasyBrewing.ITEM_BREWING_STATION.get());
    }

    @Override
    public @Nullable ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
        return new BrewingStationArmPoint(this, level, pos, state);
    }
}
