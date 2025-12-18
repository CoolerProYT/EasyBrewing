package com.coolerpromc.easybrewing.item;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!CommonConfig.CONFIG.allowAmountUpgrade){
            tooltip.add(Text.translatable("tooltip.easybrewing.amount_upgrade_disabled").formatted(Formatting.RED));
        }
        tooltip.add(Text.translatable("tooltip.easybrewing.amount_upgrade_tooltip", amount).formatted(Formatting.BLUE));
    }
}
