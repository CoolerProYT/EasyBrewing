package com.coolerpromc.easybrewing.compat.rei;

import com.coolerpromc.easybrewing.EasyBrewing;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.List;

public class ItemBrewingCategory implements DisplayCategory<ItemBrewingDisplay> {
    @Override
    public CategoryIdentifier<? extends ItemBrewingDisplay> getCategoryIdentifier() {
        return ItemBrewingDisplay.CATEGORY_IDENTIFIER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.easybrewing.item_brewing_station");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EasyBrewing.ITEM_BREWING_STATION);
    }

    @Override
    public int getDisplayWidth(ItemBrewingDisplay display) {
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        return 66;
    }

    @Override
    public List<Widget> setupDisplay(ItemBrewingDisplay display, Rectangle bounds) {
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(EasyBrewing.id("textures/gui/item_brewing_station.png"), bounds.x + 4, bounds.y + 3, 39, 15, 92, 60));
        widgets.add(Widgets.createSlot(new Point(bounds.x + 44, bounds.y + 5)).backgroundEnabled(false).entries(EntryIngredients.ofItemStacks(display.recipe().potion())).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 24, bounds.y + 25)).backgroundEnabled(false).entries(EntryIngredients.ofItemStacks(display.recipe().input())).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 44, bounds.y + 45)).backgroundEnabled(false).entries(List.of(EntryStacks.of(display.recipe().output()))).markOutput());
        return widgets;
    }
}
