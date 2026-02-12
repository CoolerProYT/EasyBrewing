package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeOutput) {
        return new RecipeProvider(wrapperLookup, recipeOutput) {
            private final HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);

            @Override
            public void buildRecipes() {
                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EasyBrewing.ITEM_BREWING_STATION.asItem(), 1)
                        .pattern(" D ")
                        .pattern("DBD")
                        .pattern("SSS")
                        .define('D', ConventionalItemTags.DIAMOND_GEMS)
                        .define('B', Items.BLAZE_POWDER)
                        .define('S', ItemTags.STONE_CRAFTING_MATERIALS)
                        .unlockedBy("has_diamond", has(ConventionalItemTags.DIAMOND_GEMS))
                        .unlockedBy(getHasName(Items.BLAZE_POWDER), has(Items.BLAZE_POWDER))
                        .unlockedBy("has_stone_material", has(ItemTags.STONE_CRAFTING_MATERIALS))
                        .save(recipeOutput);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EasyBrewing.UPGRADE_BASE, 1)
                        .pattern("IPI")
                        .pattern("PBP")
                        .pattern("IPI")
                        .define('I', ConventionalItemTags.IRON_INGOTS)
                        .define('P', Items.PAPER)
                        .define('B', Items.BLAZE_POWDER)
                        .unlockedBy("has_iron_ingot", has(ConventionalItemTags.IRON_INGOTS))
                        .unlockedBy("has_paper", has(Items.PAPER))
                        .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                        .save(recipeOutput);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_1, 1)
                        .pattern("GRG")
                        .pattern("RSR")
                        .pattern("GRG")
                        .define('G', ConventionalItemTags.GOLD_INGOTS)
                        .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                        .define('S', EasyBrewing.UPGRADE_BASE)
                        .unlockedBy("has_gold_ingot", has(ConventionalItemTags.GOLD_INGOTS))
                        .unlockedBy("has_redstone_dust", has(ConventionalItemTags.REDSTONE_DUSTS))
                        .unlockedBy(getHasName(EasyBrewing.UPGRADE_BASE), has(EasyBrewing.UPGRADE_BASE))
                        .save(recipeOutput);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_2, 1)
                        .pattern("LRL")
                        .pattern("RSR")
                        .pattern("LRL")
                        .define('L', ConventionalItemTags.STORAGE_BLOCKS_LAPIS)
                        .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                        .define('S', EasyBrewing.SPEED_UPGRADE_1)
                        .unlockedBy("has_lapis_lazuli_block", has(ConventionalItemTags.STORAGE_BLOCKS_LAPIS))
                        .unlockedBy("has_redstone_dust", has(ConventionalItemTags.REDSTONE_DUSTS))
                        .unlockedBy(getHasName(EasyBrewing.SPEED_UPGRADE_1), has(EasyBrewing.SPEED_UPGRADE_1))
                        .save(recipeOutput);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_3, 1)
                        .pattern("BRB")
                        .pattern("RSR")
                        .pattern("BRB")
                        .define('B', ConventionalItemTags.STORAGE_BLOCKS_REDSTONE)
                        .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                        .define('S', EasyBrewing.SPEED_UPGRADE_2)
                        .unlockedBy("has_redstone_block", has(ConventionalItemTags.STORAGE_BLOCKS_REDSTONE))
                        .unlockedBy("has_redstone_dust", has(ConventionalItemTags.REDSTONE_DUSTS))
                        .unlockedBy(getHasName(EasyBrewing.SPEED_UPGRADE_2), has(EasyBrewing.SPEED_UPGRADE_2))
                        .save(recipeOutput);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_1, 1)
                        .pattern("GBG")
                        .pattern("BUB")
                        .pattern("GBG")
                        .define('G', Items.GLASS_BOTTLE)
                        .define('B', Items.BLAZE_POWDER)
                        .define('U', EasyBrewing.UPGRADE_BASE)
                        .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                        .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                        .unlockedBy(getHasName(EasyBrewing.UPGRADE_BASE), has(EasyBrewing.UPGRADE_BASE))
                        .save(recipeOutput);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_2, 1)
                        .pattern("GDG")
                        .pattern("DAD")
                        .pattern("GDG")
                        .define('G', Items.GLASS_BOTTLE)
                        .define('D', ConventionalItemTags.DIAMOND_GEMS)
                        .define('A', EasyBrewing.AMOUNT_UPGRADE_1)
                        .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                        .unlockedBy("has_diamond", has(ConventionalItemTags.DIAMOND_GEMS))
                        .unlockedBy(getHasName(EasyBrewing.AMOUNT_UPGRADE_1), has(EasyBrewing.AMOUNT_UPGRADE_1))
                        .save(recipeOutput);
            }
        };
    }

    @Override
    public String getName() {
        return "";
    }
}
