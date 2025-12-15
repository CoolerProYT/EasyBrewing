package com.coolerpromc.easybrewing.item;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SpeedUpgradeItem extends Item {
    private final float speedMultiplier;

    public SpeedUpgradeItem(net.minecraft.item.Item.Settings properties, float speedMultiplier) {
        super(properties);
        this.speedMultiplier = speedMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltipComponents, TooltipType tooltipFlag) {
        tooltipComponents.add(Text.translatable("tooltip.easybrewing.speed_multiplier_tooltip", String.format("%.2f", speedMultiplier)).formatted(Formatting.BLUE));
    }
}
