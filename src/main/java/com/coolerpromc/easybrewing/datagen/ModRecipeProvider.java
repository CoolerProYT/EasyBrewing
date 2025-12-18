package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EasyBrewing.ITEM_BREWING_STATION.get(), 1)
                .pattern(" D ")
                .pattern("DBD")
                .pattern("SSS")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('B', Items.BLAZE_POWDER)
                .define('S', ItemTags.STONE_CRAFTING_MATERIALS)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy(getHasName(Items.BLAZE_POWDER), has(Items.BLAZE_POWDER))
                .unlockedBy("has_stone_material", has(ItemTags.STONE_CRAFTING_MATERIALS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EasyBrewing.UPGRADE_BASE.get(), 1)
                .pattern("IPI")
                .pattern("PBP")
                .pattern("IPI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('P', Items.PAPER)
                .define('B', Items.BLAZE_POWDER)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_paper", has(Items.PAPER))
                .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_1.get(), 1)
                .pattern("GRG")
                .pattern("RSR")
                .pattern("GRG")
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', EasyBrewing.UPGRADE_BASE.get())
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(EasyBrewing.UPGRADE_BASE.get()), has(EasyBrewing.UPGRADE_BASE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_2.get(), 1)
                .pattern("LRL")
                .pattern("RSR")
                .pattern("LRL")
                .define('L', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', EasyBrewing.SPEED_UPGRADE_1.get())
                .unlockedBy("has_lapis_lazuli_block", has(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(EasyBrewing.SPEED_UPGRADE_1.get()), has(EasyBrewing.SPEED_UPGRADE_1.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_3.get(), 1)
                .pattern("BRB")
                .pattern("RSR")
                .pattern("BRB")
                .define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', EasyBrewing.SPEED_UPGRADE_2.get())
                .unlockedBy("has_redstone_block", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(EasyBrewing.SPEED_UPGRADE_2.get()), has(EasyBrewing.SPEED_UPGRADE_2.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_1.get(), 1)
                .pattern("GBG")
                .pattern("BUB")
                .pattern("GBG")
                .define('G', Items.GLASS_BOTTLE)
                .define('B', Items.BLAZE_POWDER)
                .define('U', EasyBrewing.UPGRADE_BASE.get())
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                .unlockedBy(getHasName(EasyBrewing.UPGRADE_BASE.get()), has(EasyBrewing.UPGRADE_BASE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_2.get(), 1)
                .pattern("GDG")
                .pattern("DAD")
                .pattern("GDG")
                .define('G', Items.GLASS_BOTTLE)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('A', EasyBrewing.AMOUNT_UPGRADE_1.get())
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy(getHasName(EasyBrewing.AMOUNT_UPGRADE_1.get()), has(EasyBrewing.AMOUNT_UPGRADE_1.get()))
                .save(recipeOutput);
    }
}
