package com.coolerpromc.easybrewing.compat.cobblemon;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandInput;
import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class CobblemonRecipeCheck {
    public static Optional<RecipeHolder<BrewingStandRecipe>> getRecipe(Level level, ItemStack inputStack, ItemStack potionStack){
        if (ModList.get().isLoaded("cobblemon")){
            try{
                RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.fromNamespaceAndPath("cobblemon", "brewing_stand"));
                if (brewingStandType != null){
                    return level.getRecipeManager().getRecipeFor(brewingStandType, new BrewingStandInput(inputStack, List.of(potionStack, potionStack, potionStack)), level);
                }
            }
            catch (Exception e){
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static boolean hasRecipe(ItemBrewingStationBE be){
        Optional<RecipeHolder<BrewingStandRecipe>> optional = getRecipe(be.getLevel(), be.inputHandler.getStackInSlot(0), be.potionHandler.getStackInSlot(0));
        return optional.filter(recipeHolder -> be.hasFuel() && canInsertIntoOutputSlot(be, recipeHolder) && hasEnoughInput(be)).isPresent();
    }

    public static void craft(ItemBrewingStationBE be){
        Optional<RecipeHolder<BrewingStandRecipe>> recipe = getRecipe(be.getLevel(), be.inputHandler.getStackInSlot(0), be.potionHandler.getStackInSlot(0));
        if (recipe.isPresent()){
            RecipeHolder<BrewingStandRecipe> recipeHolder = recipe.get();
            ItemStack ingredient = be.inputHandler.getStackInSlot(0);
            ItemStack output = recipeHolder.value().getResult().copy();

            output.setCount(CommonConfig.CONFIG.cobblemonPotionCount.get() + be.additionalAmount);

            if (ingredient.hasCraftingRemainingItem()) {
                ItemStack leftover = ingredient.getCraftingRemainingItem();
                ingredient.shrink(1);
                if (ingredient.isEmpty()) {
                    ingredient = leftover;
                } else {
                    Containers.dropItemStack(be.getLevel(), be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ(), leftover);
                }
            } else {
                ingredient.shrink(1);
            }

            be.inputHandler.setStackInSlot(0, ingredient);
            be.potionHandler.extractItem(0, CommonConfig.CONFIG.cobblemonPotionCount.get() + be.additionalAmount, false);
            be.outputHandler.innerInsertItem(0, output, false);
            be.fuel--;

            be.setChanged();
        }
    }

    private static boolean hasEnoughInput(ItemBrewingStationBE be){
        return be.potionHandler.getStackInSlot(0).getCount() >= CommonConfig.CONFIG.cobblemonPotionCount.get() + be.additionalAmount;
    }

    private static boolean canInsertIntoOutputSlot(ItemBrewingStationBE be, RecipeHolder<BrewingStandRecipe> recipeHolder){
        ItemStack output = recipeHolder.value().getResult();
        output.setCount(CommonConfig.CONFIG.cobblemonPotionCount.get() + be.additionalAmount);
        return be.outputHandler.innerInsertItem(0, output, true).isEmpty();
    }
}
