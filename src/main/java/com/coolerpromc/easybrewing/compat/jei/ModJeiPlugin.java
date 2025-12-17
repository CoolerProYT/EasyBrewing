package com.coolerpromc.easybrewing.compat.jei;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.neoforge.platform.BrewingRecipeMaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ItemBrewingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(ItemBrewingCategory.TYPE, EasyBrewing.ITEM_BREWING_STATION.toStack());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        assert level != null;
        List<ItemBrewingRecipe> recipeList = new ArrayList<>(BrewingRecipeMaker.getBrewingRecipes(registration.getIngredientManager(), registration.getVanillaRecipeFactory(), level.potionBrewing()).stream().map(iJeiBrewingRecipe -> {
            ItemStack output = iJeiBrewingRecipe.getPotionOutput().copy();
            output.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
            return new ItemBrewingRecipe(iJeiBrewingRecipe.getIngredients().stream().map(ItemStack::copy).toList(), iJeiBrewingRecipe.getPotionInputs().stream().map(ItemStack::copy).peek(stack -> stack.setCount(PotionCountSyncS2CPacket.POTION_COUNT)).toList(), output);
        }).toList());
//        if (ModList.get().isLoaded("cobblemon")) CobblemonRecipeViewer.addRecipes(recipeList, level);
        registration.addRecipes(ItemBrewingCategory.TYPE, recipeList);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ItemBrewingStationScreen.class, 76, 34, 20, 22, ItemBrewingCategory.TYPE);
    }

    @Override
    public Identifier getPluginUid() {
        return EasyBrewing.id("jei_plugin");
    }
}
