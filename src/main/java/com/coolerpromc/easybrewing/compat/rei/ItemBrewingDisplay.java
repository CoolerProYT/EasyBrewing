/*
package com.coolerpromc.easybrewing.compat.rei;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.rei.recipe.BrewingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public record ItemBrewingDisplay(BrewingRecipe recipe) implements Display {
    public static final CategoryIdentifier<ItemBrewingDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(EasyBrewing.id("item_brewing"));

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(recipe.ingredient(), recipe.input());
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(recipe.output());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY_IDENTIFIER;
    }
}
*/
