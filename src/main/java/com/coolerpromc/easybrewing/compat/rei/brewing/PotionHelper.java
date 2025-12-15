package com.coolerpromc.easybrewing.compat.rei.brewing;

import com.google.gson.internal.LinkedTreeMap;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import java.util.*;

public class PotionHelper {
    public static List<BrewingRecipe> registerPotions() {
        BrewingRecipeRegistry brewing = MinecraftClient.getInstance().world.getBrewingRecipeRegistry();
        List<BrewingRecipe> recipes = new ArrayList<>();
        registerVanillaPotions(brewing, recipes);
        return recipes;
    }

    private static void registerVanillaPotions(BrewingRecipeRegistry brewing, List<BrewingRecipe> recipes) {
        Set<RegistryEntry<Potion>> potions = Collections.newSetFromMap(new LinkedTreeMap<>(Comparator.comparing(RegistryEntry::getIdAsString), false));
        for (Ingredient container : brewing.potionTypes) {
            for (BrewingRecipeRegistry.Recipe<Potion> mix : brewing.potionRecipes) {
                RegistryEntry<Potion> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                RegistryEntry<Potion> to = mix.to();
                Ingredient base = Ingredient.ofStacks(Arrays.stream(container.getMatchingStacks())
                        .map(ItemStack::copy)
                        .peek(stack -> stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(from))));
                ItemStack output = Arrays.stream(container.getMatchingStacks())
                        .map(ItemStack::copy)
                        .peek(stack -> stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(to)))
                        .findFirst().orElse(ItemStack.EMPTY);
                potions.add(from);
                potions.add(to);
                recipes.add(new BrewingRecipe(base, ingredient, output));
            }
        }
        for (RegistryEntry<Potion> potion : potions) {
            for (BrewingRecipeRegistry.Recipe<Item> mix : brewing.itemRecipes) {
                RegistryEntry<Item> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                RegistryEntry<Item> to = mix.to();
                ItemStack baseStack = new ItemStack(from);
                baseStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
                Ingredient base = Ingredient.ofStacks(baseStack);
                ItemStack output = new ItemStack(to);
                output.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
                recipes.add(new BrewingRecipe(base, ingredient, output));
            }
        }
    }
}