package com.coolerpromc.easybrewing.mixin;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LingeringPotionItem.class)
public class LingeringPotionItemMixin extends Item {
    public LingeringPotionItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultMaxStackSize() {
        return CommonConfig.CONFIG.potionStackSize;
    }
}
