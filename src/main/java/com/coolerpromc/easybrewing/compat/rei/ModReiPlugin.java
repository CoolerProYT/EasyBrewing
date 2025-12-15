package com.coolerpromc.easybrewing.compat.rei;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.cobblemon.CobblemonRecipeViewer;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import com.coolerpromc.easybrewing.compat.rei.brewing.PotionHelper;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModReiPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ItemBrewingCategory(), config -> config.addWorkstations(EntryStacks.of(EasyBrewing.ITEM_BREWING_STATION)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        List<ItemBrewingRecipe> recipes = new ArrayList<>(PotionHelper.registerPotions().stream().map(iJeiBrewingRecipe -> {
            ItemStack output = iJeiBrewingRecipe.output.copy();
            output.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
            return new ItemBrewingRecipe(Arrays.stream(iJeiBrewingRecipe.ingredient.getMatchingStacks()).map(ItemStack::copy).toList(), Arrays.stream(iJeiBrewingRecipe.input.getMatchingStacks()).map(ItemStack::copy).peek(stack -> stack.setCount(PotionCountSyncS2CPacket.POTION_COUNT)).toList(), output);
        }).toList());
        if (FabricLoader.getInstance().isModLoaded("cobblemon")){
            CobblemonRecipeViewer.addRecipes(recipes, MinecraftClient.getInstance().world);
        }
        recipes.forEach(recipe -> registry.add(new ItemBrewingDisplay(recipe)));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 76, ((screen.height - 166) / 2) + 34, 20, 22), ItemBrewingStationScreen.class, ItemBrewingDisplay.CATEGORY_IDENTIFIER);
    }
}
