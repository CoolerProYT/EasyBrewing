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

    public static boolean isCobblemonIngredient(ItemStack stack, Level level){
        /*if(Services.PLATFORM.isModLoaded("cobblemon")){
            try{
                RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "brewing_stand"));
                if (brewingStandType != null){
                    List<Ingredient> ingredients = level.getRecipeManager().getAllRecipesFor(brewingStandType).stream().map(RecipeHolder::value).map(BrewingStandRecipe::getInput).toList();
                    for (Ingredient ingredient : ingredients){
                        if (ingredient.test(stack)) return true;
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
