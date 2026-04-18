package com.coolerpromc.easybrewing.compat.jei;

import com.coolerpromc.easybrewing.CommonClass;
import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;

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
        registration.addCraftingStation(ItemBrewingCategory.TYPE, CommonClass.ITEM_BREWING_STATION.toStack());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        assert level != null;
        PotionBrewing brewing = level.potionBrewing();
        List<ItemBrewingRecipe> recipeList = new ArrayList<>();

        // Register vanilla potion recipes
        for (Ingredient container : brewing.containers) {
            for (PotionBrewing.Mix<Potion> mix : brewing.potionMixes) {
                Holder<Potion> from = mix.from();
                Ingredient ingredient = mix.ingredient();
                Holder<Potion> to = mix.to();

                List<ItemStack> inputs = new ArrayList<>();
                for (ItemStack containerStack : container.items().map(Holder::value).map(ItemStack::new).toList()) {
                    ItemStack input = containerStack.copy();
                    input.set(DataComponents.POTION_CONTENTS, new PotionContents(from));
                    input.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                    inputs.add(input);
                }

                List<ItemStack> ingredients = new ArrayList<>();
                for (ItemStack ingStack : ingredient.items().map(Holder::value).map(ItemStack::new).toList()) {
                    ingredients.add(ingStack.copy());
                }

                List<ItemStack> outputs = new ArrayList<>();
                for (ItemStack containerStack : container.items().map(Holder::value).map(ItemStack::new).toList()) {
                    ItemStack output = containerStack.copy();
                    output.set(DataComponents.POTION_CONTENTS, new PotionContents(to));
                    output.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
                    outputs.add(output);
                }

                if (!inputs.isEmpty() && !outputs.isEmpty()) {
                    recipeList.add(new ItemBrewingRecipe(ingredients, inputs, outputs.get(0)));
                }
            }
        }

        registration.addRecipes(ItemBrewingCategory.TYPE, recipeList);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ItemBrewingStationScreen.class, 76, 34, 20, 22, ItemBrewingCategory.TYPE);
    }

    @Override
    public Identifier getPluginUid() {
        return Constants.id("jei_plugin");
    }
}
