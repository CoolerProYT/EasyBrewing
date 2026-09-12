package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.CommonClass;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    public static MultiRegistryBootstrap create() {
        return RecipeProvider.asBootstrap(ModRecipeProvider::new);
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, CommonClass.ITEM_BREWING_STATION.asItem(), 1)
                .pattern(" D ")
                .pattern("DBD")
                .pattern("SSS")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('B', Items.BLAZE_POWDER)
                .define('S', ItemTags.STONE_CRAFTING_MATERIALS)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy(getHasName(Items.BLAZE_POWDER), has(Items.BLAZE_POWDER))
                .unlockedBy("has_stone_material", has(ItemTags.STONE_CRAFTING_MATERIALS))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, CommonClass.UPGRADE_BASE.get(), 1)
                .pattern("IPI")
                .pattern("PBP")
                .pattern("IPI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('P', Items.PAPER)
                .define('B', Items.BLAZE_POWDER)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_paper", has(Items.PAPER))
                .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, CommonClass.SPEED_UPGRADE_1.get(), 1)
                .pattern("GRG")
                .pattern("RSR")
                .pattern("GRG")
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', CommonClass.UPGRADE_BASE.get())
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(CommonClass.UPGRADE_BASE.get()), has(CommonClass.UPGRADE_BASE.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, CommonClass.SPEED_UPGRADE_2.get(), 1)
                .pattern("LRL")
                .pattern("RSR")
                .pattern("LRL")
                .define('L', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', CommonClass.SPEED_UPGRADE_1.get())
                .unlockedBy("has_lapis_lazuli_block", has(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(CommonClass.SPEED_UPGRADE_1.get()), has(CommonClass.SPEED_UPGRADE_1.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, CommonClass.SPEED_UPGRADE_3.get(), 1)
                .pattern("BRB")
                .pattern("RSR")
                .pattern("BRB")
                .define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', CommonClass.SPEED_UPGRADE_2.get())
                .unlockedBy("has_redstone_block", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(CommonClass.SPEED_UPGRADE_2.get()), has(CommonClass.SPEED_UPGRADE_2.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, CommonClass.AMOUNT_UPGRADE_1.get(), 1)
                .pattern("GBG")
                .pattern("BUB")
                .pattern("GBG")
                .define('G', Items.GLASS_BOTTLE)
                .define('B', Items.BLAZE_POWDER)
                .define('U', CommonClass.UPGRADE_BASE.get())
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                .unlockedBy(getHasName(CommonClass.UPGRADE_BASE.get()), has(CommonClass.UPGRADE_BASE.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, CommonClass.AMOUNT_UPGRADE_2.get(), 1)
                .pattern("GDG")
                .pattern("DAD")
                .pattern("GDG")
                .define('G', Items.GLASS_BOTTLE)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('A', CommonClass.AMOUNT_UPGRADE_1.get())
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy(getHasName(CommonClass.AMOUNT_UPGRADE_1.get()), has(CommonClass.AMOUNT_UPGRADE_1.get()))
                .save(output);
    }
}
