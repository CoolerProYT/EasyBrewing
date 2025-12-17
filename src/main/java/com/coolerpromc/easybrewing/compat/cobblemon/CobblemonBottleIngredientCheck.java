package com.coolerpromc.easybrewing.compat.cobblemon;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;

@SuppressWarnings("unchecked")
public class CobblemonBottleIngredientCheck {
    public static boolean isCobblemonBottle(ItemResource resource, Level level){
        if(ModList.get().isLoaded("cobblemon") && level instanceof ServerLevel serverLevel){
            try{
                RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) BuiltInRegistries.RECIPE_TYPE.getValue(Identifier.fromNamespaceAndPath("cobblemon", "brewing_stand"));
                if (brewingStandType != null){
                    List<Ingredient> ingredients = serverLevel.recipeAccess().recipeMap().byType(brewingStandType).stream().map(RecipeHolder::value).map(BrewingStandRecipe::getBottle).toList();
                    for (Ingredient ingredient : ingredients){
                        if (ingredient.test(resource.toStack())) return true;
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
