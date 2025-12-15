package com.coolerpromc.easybrewing.mixin;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SplashPotionItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SplashPotionItem.class)
public class SplashPotionItemMixin extends Item {
    public SplashPotionItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultMaxStackSize() {
        return CommonConfig.CONFIG.potionStackSize;
    }
}
