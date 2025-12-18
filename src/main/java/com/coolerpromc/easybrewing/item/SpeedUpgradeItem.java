package com.coolerpromc.easybrewing.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> tooltipComponents, TooltipFlag p_41424_) {
        tooltipComponents.add(Component.translatable("tooltip.easybrewing.speed_multiplier_tooltip", String.format("%.2f", speedMultiplier)).withStyle(ChatFormatting.BLUE));
    }
}
