package com.coolerpromc.easybrewing.compat.rei;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.List;

public record ItemBrewingDisplay(ItemBrewingRecipe recipe) implements Display {
    public static final CategoryIdentifier<ItemBrewingDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(EasyBrewing.id("item_brewing"));

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(EntryIngredients.ofItemStacks(recipe.input()), EntryIngredients.ofItemStacks(recipe.potion()));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredient.of(EntryStacks.of(recipe.output())));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY_IDENTIFIER;
    }
}
