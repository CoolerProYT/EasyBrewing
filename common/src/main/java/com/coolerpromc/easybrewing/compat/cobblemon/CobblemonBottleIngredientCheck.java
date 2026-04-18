package com.coolerpromc.easybrewing.compat.cobblemon;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@SuppressWarnings("unchecked")
public class CobblemonBottleIngredientCheck {
    public static boolean isCobblemonBottle(ItemStack stack, Level level){
        /*if(Services.PLATFORM.isModLoaded("cobblemon") && level instanceof ServerLevel serverLevel){
            // ... cobblemon check logic
        }*/
        return false;
    }
}
