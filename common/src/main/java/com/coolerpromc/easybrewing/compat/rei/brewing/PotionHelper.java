package com.coolerpromc.easybrewing.compat.rei.brewing;/*
package com.coolerpromc.easybrewing.compat.rei.brewing;

import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.google.gson.internal.LinkedTreeMap;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
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
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

import java.util.*;

public class PotionHelper {
    public static List<BrewingRecipe> registerPotions() {
        List<BrewingRecipe> recipes = new ArrayList<>();
        PotionBrewing brewing = Minecraft.getInstance().level.potionBrewing();
        registerVanillaPotions(brewing, recipes);
        for (IBrewingRecipe recipe : brewing.getRecipes()) {
            if (recipe instanceof net.neoforged.neoforge.common.brewing.BrewingRecipe brewingRecipe) {
                recipes.add(new BrewingRecipe(EntryIngredients.ofItemStacks(brewingRecipe.getInput().getValues().stream().map(Holder::value).map(Item::getDefaultInstance).peek(stack -> stack.setCount(PotionCountSyncS2CPacket.POTION_COUNT)).toList()),
                        EntryIngredients.ofIngredient(brewingRecipe.getIngredient()),
                        EntryIngredients.of(brewingRecipe.getOutput().copyWithCount(PotionCountSyncS2CPacket.POTION_COUNT))));
            }
        }
        return recipes;
    }

    private static void registerVanillaPotions(PotionBrewing brewing, List<BrewingRecipe> recipes) {
        Set<Holder<Potion>> potions = Collections.newSetFromMap(new LinkedTreeMap<>(Comparator.comparing(Holder::getRegisteredName), false));
        for (Ingredient container : brewing.containers) {
            for (PotionBrewing.Mix<Potion> mix : brewing.potionMixes) {
                Holder<Potion> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                Holder<Potion> to = mix.to();
                EntryIngredient base = EntryIngredients.ofIngredient(container)
                        .map(stack -> {
                            EntryStack<?> copied = stack.copy();
                            copied.<ItemStack>castValue().set(DataComponents.POTION_CONTENTS, new PotionContents(from));
                            copied.<ItemStack>castValue().setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                            return copied;
                        });
                EntryIngredient output = EntryIngredients.ofIngredient(container)
                        .map(stack -> {
                            EntryStack<?> copied = stack.copy();
                            copied.<ItemStack>castValue().set(DataComponents.POTION_CONTENTS, new PotionContents(to));
                            copied.<ItemStack>castValue().setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                            return copied;
                        });
                recipes.add(new BrewingRecipe(base, EntryIngredients.ofIngredient(ingredient), output));
                potions.add(from);
                potions.add(to);
            }
        }
        for (Holder<Potion> potion : potions) {
            for (PotionBrewing.Mix<Item> mix : brewing.containerMixes) {
                Holder<Item> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                Holder<Item> to = mix.to();
                ItemStack baseStack = new ItemStack(from);
                baseStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
                baseStack.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                EntryIngredient base = EntryIngredients.of(baseStack);
                ItemStack output = new ItemStack(to);
                output.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
                output.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                recipes.add(new BrewingRecipe(base, EntryIngredients.ofIngredient(ingredient), EntryIngredients.of(output)));
            }
        }
    }
}
*/
