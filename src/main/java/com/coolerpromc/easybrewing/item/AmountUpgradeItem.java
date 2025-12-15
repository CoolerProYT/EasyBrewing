package com.coolerpromc.easybrewing.item;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AmountUpgradeItem extends Item {
    private final int amount;

    public AmountUpgradeItem(Properties properties, int amount) {
        super(properties);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!CommonConfig.CONFIG.allowAmountUpgrade){
            tooltipComponents.add(Component.translatable("tooltip.easybrewing.amount_upgrade_disabled").withStyle(ChatFormatting.RED));
        }
        tooltipComponents.add(Component.translatable("tooltip.easybrewing.amount_upgrade_tooltip", amount).withStyle(ChatFormatting.BLUE));
    }
}
