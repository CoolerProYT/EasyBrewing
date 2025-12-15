package com.coolerpromc.easybrewing.compat.jei.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record ItemBrewingRecipe(List<ItemStack> input, List<ItemStack> potion, ItemStack output) {
}
