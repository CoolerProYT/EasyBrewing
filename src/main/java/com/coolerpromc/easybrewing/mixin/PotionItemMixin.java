package com.coolerpromc.easybrewing.mixin;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PotionItem.class)
public class PotionItemMixin extends Item {
    public PotionItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultMaxStackSize() {
        return CommonConfig.CONFIG.potionStackSize;
    }
}
