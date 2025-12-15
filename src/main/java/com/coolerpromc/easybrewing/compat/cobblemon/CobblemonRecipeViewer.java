package com.coolerpromc.easybrewing.compat.cobblemon;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@SuppressWarnings("unchecked")
public class CobblemonRecipeViewer {
    public static void addRecipes(List<ItemBrewingRecipe> recipes, World level){
        try{
            RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) Registries.RECIPE_TYPE.get(Identifier.of("cobblemon", "brewing_stand"));
            if (brewingStandType != null){
                List<BrewingStandRecipe> brewingStandRecipes = level.getRecipeManager().listAllOfType(brewingStandType).stream().map(RecipeEntry::value).toList();
                for (BrewingStandRecipe recipe : brewingStandRecipes){
                    ItemStack output = recipe.getResult().copy();
                    output.setCount(PotionCountSyncS2CPacket.COBBLEMON_POTION_COUNT);
                    recipes.add(new ItemBrewingRecipe(Arrays.stream(recipe.getInput().getMatchingStacks()).map(ItemStack::copy).toList(), Arrays.stream(recipe.getBottle().getMatchingStacks()).map(ItemStack::copy).peek(stack -> stack.setCount(PotionCountSyncS2CPacket.COBBLEMON_POTION_COUNT)).toList(), output));
                }
            }
        }
        catch (Exception e){
            EasyBrewing.LOGGER.error("Error while adding cobblemon brewing recipes to JEI.");
        }
    }
}
