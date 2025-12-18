package com.coolerpromc.easybrewing.item;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> tooltipComponents, TooltipFlag p_41424_) {
        if (!CommonConfig.CONFIG.allowAmountUpgrade.get()){
            tooltipComponents.add(Component.translatable("tooltip.easybrewing.amount_upgrade_disabled").withStyle(ChatFormatting.RED));
        }
        tooltipComponents.add(Component.translatable("tooltip.easybrewing.amount_upgrade_tooltip", amount).withStyle(ChatFormatting.BLUE));
    }
}
