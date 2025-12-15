package com.coolerpromc.easybrewing.compat.rei.brewing;

import com.google.gson.internal.LinkedTreeMap;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class PotionHelper {
    public static List<BrewingRecipe> registerPotions() {
        PotionBrewing brewing = Minecraft.getInstance().level.potionBrewing();
        List<BrewingRecipe> recipes = new ArrayList<>();
        registerVanillaPotions(brewing, recipes);
        return recipes;
    }

    private static void registerVanillaPotions(PotionBrewing brewing, List<BrewingRecipe> recipes) {
        Set<Holder<Potion>> potions = Collections.newSetFromMap(new LinkedTreeMap<>(Comparator.comparing(Holder::getRegisteredName), false));
        for (Ingredient container : brewing.containers) {
            for (PotionBrewing.Mix<Potion> mix : brewing.potionMixes) {
                Holder<Potion> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                Holder<Potion> to = mix.to();
                Ingredient base = Ingredient.of(Arrays.stream(container.getItems())
                        .map(ItemStack::copy)
                        .peek(stack -> stack.set(DataComponents.POTION_CONTENTS, new PotionContents(from))));
                ItemStack output = Arrays.stream(container.getItems())
                        .map(ItemStack::copy)
                        .peek(stack -> stack.set(DataComponents.POTION_CONTENTS, new PotionContents(to)))
                        .findFirst().orElse(ItemStack.EMPTY);
                potions.add(from);
                potions.add(to);
                recipes.add(new BrewingRecipe(base, ingredient, output));
            }
        }
        for (Holder<Potion> potion : potions) {
            for (PotionBrewing.Mix<Item> mix : brewing.containerMixes) {
                Holder<Item> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                Holder<Item> to = mix.to();
                ItemStack baseStack = new ItemStack(from);
                baseStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
                Ingredient base = Ingredient.of(baseStack);
                ItemStack output = new ItemStack(to);
                output.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
                recipes.add(new BrewingRecipe(base, ingredient, output));
            }
        }
    }
}
//./gradlew.bat migrateMappings --mappings "1.21.1+build.3" --overrideInputsIHaveABackup