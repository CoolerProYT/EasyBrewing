package com.coolerpromc.easybrewing.item;

import com.coolerpromc.easybrewing.config.CommonConfig;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AmountUpgradeItem extends Item {
    private final int amount;

    public AmountUpgradeItem(net.minecraft.item.Item.Settings properties, int amount) {
        super(properties);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltipComponents, TooltipType tooltipFlag) {
        if (!CommonConfig.CONFIG.allowAmountUpgrade){
            tooltipComponents.add(Text.translatable("tooltip.easybrewing.amount_upgrade_disabled").formatted(Formatting.RED));
        }
        tooltipComponents.add(Text.translatable("tooltip.easybrewing.amount_upgrade_tooltip", amount).formatted(Formatting.BLUE));
    }
}
