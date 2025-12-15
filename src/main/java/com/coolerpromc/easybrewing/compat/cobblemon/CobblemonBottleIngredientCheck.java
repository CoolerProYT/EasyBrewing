package com.coolerpromc.easybrewing.compat.cobblemon;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

@SuppressWarnings("unchecked")
public class CobblemonBottleIngredientCheck {
    public static boolean isCobblemonBottle(ItemStack stack, Level level){
        if(FabricLoader.getInstance().isModLoaded("cobblemon")){
            try{
                RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "brewing_stand"));
                if (brewingStandType != null){
                    List<Ingredient> ingredients = level.getRecipeManager().getAllRecipesFor(brewingStandType).stream().map(RecipeHolder::value).map(BrewingStandRecipe::getBottle).toList();
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
