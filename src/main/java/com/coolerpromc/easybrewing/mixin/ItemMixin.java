package com.coolerpromc.easybrewing.mixin;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.item.Item;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void changeMaxCount(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item)(Object)this;
        
        if (item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem){
            cir.setReturnValue(CommonConfig.CONFIG.potionStackSize);
        }
    }
}