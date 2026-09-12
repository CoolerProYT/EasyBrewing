package com.coolerpromc.easybrewing.platform;

import com.coolerpromc.easybrewing.platform.services.IPotionHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;

public class FabricPotionHelper implements IPotionHelper {
    @Override
    public boolean isInput(Level level, ItemStack stack) {
        return PotionIngredient.isPotionInput(stack, level.recipeAccess());
    }

    @Override
    public boolean isIngredient(Level level, ItemStack stack) {
        return level.recipeAccess().propertySet(RecipePropertySet.BREWING_REAGENTS).test(stack);
    }
}
