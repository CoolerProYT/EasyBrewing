package com.coolerpromc.easybrewing.compat.jei.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public record ItemBrewingRecipe(List<ItemStack> input, List<ItemStack> potion, ItemStack output) {
}
