package com.coolerpromc.easybrewing.compat.jei;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record ItemBrewingCategory(IGuiHelper helper) implements IRecipeCategory<ItemBrewingRecipe> {
    public static final RecipeType<ItemBrewingRecipe> TYPE = RecipeType.create(EasyBrewing.MODID, "item_brewing", ItemBrewingRecipe.class);

    @Override
    public RecipeType<ItemBrewingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.easybrewing.item_brewing_station");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, EasyBrewing.ITEM_BREWING_STATION.asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ItemBrewingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(44, 2).addItemStacks(recipe.potion());
        builder.addInputSlot(24, 22).addItemStacks(recipe.input());
        builder.addOutputSlot(44, 42).addItemStack(recipe.output());
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public void draw(ItemBrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ResourceLocation ITEM_BREWING_STATION = EasyBrewing.id("textures/gui/item_brewing_station.png");
        guiGraphics.blit(ITEM_BREWING_STATION, 0, 0, 35, 15, 100, 60);
    }
}
