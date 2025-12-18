package com.coolerpromc.easybrewing.compat.rei.brewing;

import com.coolerpromc.easybrewing.compat.rei.recipe.BrewingRecipe;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.google.gson.internal.LinkedTreeMap;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
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
        List<BrewingRecipe> recipes = new ArrayList<>();
        BrewingRecipeRegistry brewing = MinecraftClient.getInstance().world.getBrewingRecipeRegistry();
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
                EntryIngredient base = EntryIngredients.ofIngredient(container)
                        .map(stack -> {
                            EntryStack<?> copied = stack.copy();
                            copied.<ItemStack>castValue().set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(from));
                            copied.<ItemStack>castValue().setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                            return copied;
                        });
                EntryIngredient output = EntryIngredients.ofIngredient(container)
                        .map(stack -> {
                            EntryStack<?> copied = stack.copy();
                            copied.<ItemStack>castValue().set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(to));
                            copied.<ItemStack>castValue().setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                            return copied;
                        });
                recipes.add(new BrewingRecipe(base, EntryIngredients.ofIngredient(ingredient), output));
                potions.add(from);
                potions.add(to);
            }
        }
        for (RegistryEntry<Potion> potion : potions) {
            for (BrewingRecipeRegistry.Recipe<Item> mix : brewing.itemRecipes) {
                RegistryEntry<Item> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                RegistryEntry<Item> to = mix.to();
                ItemStack baseStack = new ItemStack(from);
                baseStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
                baseStack.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                EntryIngredient base = EntryIngredients.of(baseStack);
                ItemStack output = new ItemStack(to);
                output.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
                output.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                recipes.add(new BrewingRecipe(base, EntryIngredients.ofIngredient(ingredient), EntryIngredients.of(output)));
            }
        }
    }

}