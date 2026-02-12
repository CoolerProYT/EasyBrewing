/*
package com.coolerpromc.easybrewing.compat.rei;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.rei.brewing.PotionHelper;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class ModReiPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ItemBrewingCategory(), config -> config.addWorkstations(EntryStacks.of(EasyBrewing.ITEM_BREWING_STATION)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        PotionHelper.registerPotions().forEach(recipe -> registry.add(new ItemBrewingDisplay(recipe)));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 76, ((screen.height - 166) / 2) + 34, 20, 22), ItemBrewingStationScreen.class, ItemBrewingDisplay.CATEGORY_IDENTIFIER);
    }
}
*/
