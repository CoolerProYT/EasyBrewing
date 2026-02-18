package com.coolerpromc.easybrewing.compat.cobblemon;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;

@SuppressWarnings("unchecked")
public class CobblemonBottleIngredientCheck {
    public static boolean isCobblemonBottle(ItemResource resource, Level level){
        /*if(ModList.get().isLoaded("cobblemon") && level instanceof ServerLevel serverLevel){
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
        }*/
        return false;
    }
}
