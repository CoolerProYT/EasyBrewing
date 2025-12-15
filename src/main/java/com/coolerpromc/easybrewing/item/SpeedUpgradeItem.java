package com.coolerpromc.easybrewing.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpeedUpgradeItem extends Item {
    private final float speedMultiplier;

    public SpeedUpgradeItem(Properties properties, float speedMultiplier) {
        super(properties);
        this.speedMultiplier = speedMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.easybrewing.speed_multiplier_tooltip", String.format("%.2f", speedMultiplier)).withStyle(ChatFormatting.BLUE));
    }
}
