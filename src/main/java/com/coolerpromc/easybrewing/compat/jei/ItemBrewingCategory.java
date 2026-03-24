package com.coolerpromc.easybrewing.compat.jei;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public record ItemBrewingCategory(IGuiHelper helper) implements IRecipeCategory<ItemBrewingRecipe> {
    public static final IRecipeType<ItemBrewingRecipe> TYPE = IRecipeType.create(EasyBrewing.MODID, "item_brewing", ItemBrewingRecipe.class);

    @Override
    public IRecipeType<ItemBrewingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.easybrewing.item_brewing_station");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, EasyBrewing.ITEM_BREWING_STATION.toStack());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ItemBrewingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(44, 2).addItemStacks(recipe.potion());
        builder.addInputSlot(24, 22).addItemStacks(recipe.input());
        builder.addOutputSlot(44, 42).add(recipe.output());
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
    public void draw(ItemBrewingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        Identifier ITEM_BREWING_STATION = EasyBrewing.id("textures/gui/item_brewing_station.png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ITEM_BREWING_STATION, 0, 0, 35, 15, 100, 60, 256, 256);
    }
}
