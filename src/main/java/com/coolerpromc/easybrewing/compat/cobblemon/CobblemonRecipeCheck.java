/*
package com.coolerpromc.easybrewing.compat.cobblemon;

import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandInput;
import com.cobblemon.mod.common.item.crafting.brewingstand.BrewingStandRecipe;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.config.CommonConfig;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class CobblemonRecipeCheck {
    public static Optional<RecipeEntry<BrewingStandRecipe>> getRecipe(World level, ItemStack inputStack, ItemStack potionStack){
        if (FabricLoader.getInstance().isModLoaded("cobblemon")){
            try{
                RecipeType<BrewingStandRecipe> brewingStandType = (RecipeType<BrewingStandRecipe>) Registries.RECIPE_TYPE.get(Identifier.of("cobblemon", "brewing_stand"));
                if (brewingStandType != null){
                    return level.getRecipeManager().getFirstMatch(brewingStandType, new BrewingStandInput(inputStack, List.of(potionStack, potionStack, potionStack)), level);
                }
            }
            catch (Exception e){
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static boolean hasRecipe(ItemBrewingStationBE be){
        Optional<RecipeEntry<BrewingStandRecipe>> optional = getRecipe(be.getWorld(), be.inputHandler.getStack(0), be.potionHandler.getStack(0));
        return optional.filter(recipeHolder -> be.hasFuel() && canInsertIntoOutputSlot(be, recipeHolder) && hasEnoughInput(be)).isPresent();
    }

    public static void craft(ItemBrewingStationBE be){
        Optional<RecipeEntry<BrewingStandRecipe>> recipe = getRecipe(be.getWorld(), be.inputHandler.getStack(0), be.potionHandler.getStack(0));
        if (recipe.isPresent()){
            RecipeEntry<BrewingStandRecipe> recipeHolder = recipe.get();
            ItemStack ingredient = be.inputHandler.getStack(0);
            ItemStack output = recipeHolder.value().getResult().copy();

            output.setCount(CommonConfig.CONFIG.cobblemonPotionCount + be.additionalAmount);

            if (!ingredient.getRecipeRemainder().isEmpty()) {
                ItemStack leftover = ingredient.getRecipeRemainder();
                ingredient.decrement(1);
                if (ingredient.isEmpty()) {
                    ingredient = leftover;
                } else {
                    ItemScatterer.spawn(be.getWorld(), be.getPos().getX(), be.getPos().getY(), be.getPos().getZ(), leftover);
                }
            } else {
                ingredient.decrement(1);
            }

            be.inputHandler.setStack(0, ingredient);
            try(Transaction tx = Transaction.openOuter()){
                be.potionStorage.extract(be.potionStorage.getSlot(0).getResource(), CommonConfig.CONFIG.cobblemonPotionCount + be.additionalAmount, tx);
                tx.commit();
            }
            try(Transaction tx = Transaction.openOuter()){
                be.outputStorage.insert(ItemVariant.of(output), output.getCount(), tx);
                tx.commit();
            }
            be.fuel--;

            be.markDirty();
        }
    }

    private static boolean hasEnoughInput(ItemBrewingStationBE be){
        return be.potionHandler.getStack(0).getCount() >= CommonConfig.CONFIG.cobblemonPotionCount + be.additionalAmount;
    }

    private static boolean canInsertIntoOutputSlot(ItemBrewingStationBE be, RecipeEntry<BrewingStandRecipe> recipeHolder){
        ItemStack output = recipeHolder.value().getResult();
        output.setCount(CommonConfig.CONFIG.cobblemonPotionCount + be.additionalAmount);
        try(Transaction tx = Transaction.openOuter()){
            long inserted = be.outputStorage.insert(ItemVariant.of(output), output.getCount(), tx);
            return inserted == output.getCount();
        }
    }
}
*/
