package com.coolerpromc.easybrewing.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class SpeedUpgradeItem extends Item {
    private final float speedMultiplier;

    public SpeedUpgradeItem(net.minecraft.world.item.Item.Properties properties, float speedMultiplier) {
        super(properties);
        this.speedMultiplier = speedMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        textConsumer.accept(Component.translatable("tooltip.easybrewing.speed_multiplier_tooltip", String.format("%.2f", speedMultiplier)).withStyle(ChatFormatting.BLUE));
    }
}
