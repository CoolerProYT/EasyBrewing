package com.coolerpromc.easybrewing.mixin;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    private void modifyPotionStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Item item = stack.getItem();
        if (item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem) {
            cir.setReturnValue(CommonConfig.CONFIG.potionStackSize.get());
        }
    }

    @Inject(method = "isStackable", at = @At("HEAD"), cancellable = true)
    private void makePotionsStackable(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Item item = stack.getItem();
        if (item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem) {
            cir.setReturnValue(true);
        }
    }
}