package com.coolerpromc.easybrewing.mixin;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getDefaultMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void changeMaxCount(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item)(Object)this;
        
        if (item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem){
            cir.setReturnValue(CommonConfig.CONFIG.potionStackSize);
        }
    }
}