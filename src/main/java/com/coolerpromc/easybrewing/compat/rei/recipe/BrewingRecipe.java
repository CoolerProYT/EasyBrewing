package com.coolerpromc.easybrewing.compat.rei.recipe;

import me.shedaniel.rei.api.common.entry.EntryIngredient;

public record BrewingRecipe(EntryIngredient input, EntryIngredient ingredient, EntryIngredient output) {
}