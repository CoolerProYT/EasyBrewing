package com.coolerpromc.easybrewing.item;

import com.coolerpromc.easybrewing.config.CommonConfig;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class AmountUpgradeItem extends Item {
    private final int amount;

    public AmountUpgradeItem(net.minecraft.world.item.Item.Properties properties, int amount) {
        super(properties);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        if (!CommonConfig.CONFIG.allowAmountUpgrade){
            textConsumer.accept(Component.translatable("tooltip.easybrewing.amount_upgrade_disabled").withStyle(ChatFormatting.RED));
        }
        textConsumer.accept(Component.translatable("tooltip.easybrewing.amount_upgrade_tooltip", amount).withStyle(ChatFormatting.BLUE));
    }
}
