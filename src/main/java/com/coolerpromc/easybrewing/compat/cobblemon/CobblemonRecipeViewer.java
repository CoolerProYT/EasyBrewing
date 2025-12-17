/*
package com.coolerpromc.easybrewing.compat.cobblemon;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class CobblemonRecipeViewer {
    public static void addRecipes(List<ItemBrewingRecipe> recipes, Level level){
        try{
            RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) BuiltInRegistries.RECIPE_TYPE.get(Identifier.fromNamespaceAndPath("cobblemon", "brewing_stand"));
            if (brewingStandType != null){
                List<BrewingStandRecipe> brewingStandRecipes = level.getRecipeManager().getAllRecipesFor(brewingStandType).stream().map(RecipeHolder::value).toList();
                for (BrewingStandRecipe recipe : brewingStandRecipes){
                    ItemStack output = recipe.getResult().copy();
                    output.setCount(PotionCountSyncS2CPacket.COBBLEMON_POTION_COUNT);
                    recipes.add(new ItemBrewingRecipe(Arrays.stream(recipe.getInput().getItems()).map(ItemStack::copy).toList(), Arrays.stream(recipe.getBottle().getItems()).map(ItemStack::copy).peek(stack -> stack.setCount(PotionCountSyncS2CPacket.COBBLEMON_POTION_COUNT)).toList(), output));
                }
            }
        }
        catch (Exception e){
            EasyBrewing.LOGGER.error("Error while adding cobblemon brewing recipes to JEI.");
        }
    }
}
*/
