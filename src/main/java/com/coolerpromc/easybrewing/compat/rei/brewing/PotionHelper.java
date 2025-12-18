package com.coolerpromc.easybrewing.compat.rei.brewing;

import com.google.common.collect.Sets;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PotionHelper {
    public static List<BrewingRecipe> registerPotions() {
        List<BrewingRecipe> recipes = new ArrayList<>();
        registerVanillaPotions(recipes);
        return recipes;
    }

    private static void registerVanillaPotions(List<BrewingRecipe> recipes) {
        Set<Potion> potions = Sets.newLinkedHashSet();

        for(Ingredient container : BrewingRecipeRegistry.POTION_TYPES) {
            for(BrewingRecipeRegistry.Recipe<Potion> mix : BrewingRecipeRegistry.POTION_RECIPES) {
                Potion from = mix.input;
                Ingredient ingredient = mix.ingredient;
                Potion to = mix.output;
                Ingredient base = Ingredient.ofStacks(Arrays.stream(container.getMatchingStacks()).map(ItemStack::copy).map((stackx) -> PotionUtil.setPotion(stackx, from)));
                ItemStack output = Arrays.stream(container.getMatchingStacks()).map(ItemStack::copy).map((stackx) -> PotionUtil.setPotion(stackx, to)).findFirst().orElse(ItemStack.EMPTY);
                recipes.add(new BrewingRecipe(base, ingredient, output));
                potions.add(from);
                potions.add(to);
            }
        }

        for(Potion potion : potions) {
            for(BrewingRecipeRegistry.Recipe<Item> mix : BrewingRecipeRegistry.ITEM_RECIPES) {
                Item from = mix.input;
                Ingredient ingredient = mix.ingredient;
                Item to = mix.output;
                Ingredient base = Ingredient.ofStacks(PotionUtil.setPotion(new ItemStack(from), potion));
                ItemStack output = PotionUtil.setPotion(new ItemStack(to), potion);
                recipes.add(new BrewingRecipe(base, ingredient, output));
            }
        }
    }
}