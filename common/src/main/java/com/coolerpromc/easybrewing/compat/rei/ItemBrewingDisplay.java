package com.coolerpromc.easybrewing.compat.rei;/*
package com.coolerpromc.easybrewing.compat.rei;

import com.coolerpromc.easybrewing.EasyBrewing;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}
*/
