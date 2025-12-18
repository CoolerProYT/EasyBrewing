/*
package com.coolerpromc.easybrewing.compat.cobblemon;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import java.util.List;

@SuppressWarnings("unchecked")
public class CobblemonBottleIngredientCheck {
    public static boolean isCobblemonBottle(ItemStack stack, World level){
        if(FabricLoader.getInstance().isModLoaded("cobblemon")){
            try{
                RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) Registries.RECIPE_TYPE.get(Identifier.of("cobblemon", "brewing_stand"));
                if (brewingStandType != null){
                    List<Ingredient> ingredients = level.getRecipeManager().listAllOfType(brewingStandType).stream().map(RecipeEntry::value).map(BrewingStandRecipe::getBottle).toList();
                    for (Ingredient ingredient : ingredients){
                        if (ingredient.test(stack)) return true;
                    }
                }
            }
            catch (Exception e){
                return false;
            }
        }
        return false;
    }
}
*/
