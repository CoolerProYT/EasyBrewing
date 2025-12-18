package com.coolerpromc.easybrewing.compat.rei.brewing;

import com.google.common.collect.Sets;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PotionHelper {
    public static List<BrewingRecipe> registerPotions() {
        List<BrewingRecipe> recipes = new ArrayList<>();
        for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
            if (recipe instanceof VanillaBrewingRecipe) {
                registerVanillaPotions(recipes);
            } else if (recipe instanceof net.minecraftforge.common.brewing.BrewingRecipe) {
                net.minecraftforge.common.brewing.BrewingRecipe brewingRecipe = (net.minecraftforge.common.brewing.BrewingRecipe) recipe;
                recipes.add(new BrewingRecipe(brewingRecipe.getInput(), brewingRecipe.getIngredient(), brewingRecipe.getOutput().copy()));
            }
        }
        return recipes;
    }

    private static void registerVanillaPotions(List<BrewingRecipe> recipes) {
        Set<Potion> potions = Sets.newLinkedHashSet();
        for (Ingredient container : PotionBrewing.ALLOWED_CONTAINERS) {
            for (PotionBrewing.Mix<Potion> mix : PotionBrewing.POTION_MIXES) {
                Holder.Reference<Potion> from = mix.from;
                Ingredient ingredient = mix.ingredient;
                Holder.Reference<Potion> to = mix.to;
                Ingredient base = Ingredient.of(Arrays.stream(container.getItems())
                        .map(ItemStack::copy)
                        .map(stack -> PotionUtils.setPotion(stack, from.get())));
                ItemStack output = Arrays.stream(container.getItems())
                        .map(ItemStack::copy)
                        .map(stack -> PotionUtils.setPotion(stack, to.get()))
                        .findFirst().orElse(ItemStack.EMPTY);
                recipes.add(new BrewingRecipe(base, ingredient, output));
                potions.add(from.get());
                potions.add(to.get());
            }
        }
        for (Potion potion : potions) {
            for (PotionBrewing.Mix<Item> mix : PotionBrewing.CONTAINER_MIXES) {
                Holder.Reference<Item> from = mix.from;
                Ingredient ingredient = mix.ingredient;
                Holder.Reference<Item> to = mix.to;
                Ingredient base = Ingredient.of(PotionUtils.setPotion(new ItemStack(from.get()), potion));
                ItemStack output = PotionUtils.setPotion(new ItemStack(to.get()), potion);
                recipes.add(new BrewingRecipe(base, ingredient, output));
            }
        }
    }
}
