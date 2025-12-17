package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    private final HolderGetter<Item> item;
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput recipeOutput) {
        super(registries, recipeOutput);
        this.item = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EasyBrewing.ITEM_BREWING_STATION.asItem(), 1)
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

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EasyBrewing.UPGRADE_BASE.get(), 1)
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

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_1.get(), 1)
                .pattern("GRG")
                .pattern("RSR")
                .pattern("GRG")
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', EasyBrewing.UPGRADE_BASE)
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(EasyBrewing.UPGRADE_BASE), has(EasyBrewing.UPGRADE_BASE))
                .save(output);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_2.get(), 1)
                .pattern("LRL")
                .pattern("RSR")
                .pattern("LRL")
                .define('L', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', EasyBrewing.SPEED_UPGRADE_1)
                .unlockedBy("has_lapis_lazuli_block", has(Tags.Items.STORAGE_BLOCKS_LAPIS))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(EasyBrewing.SPEED_UPGRADE_1), has(EasyBrewing.SPEED_UPGRADE_1))
                .save(output);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_3.get(), 1)
                .pattern("BRB")
                .pattern("RSR")
                .pattern("BRB")
                .define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', EasyBrewing.SPEED_UPGRADE_2)
                .unlockedBy("has_redstone_block", has(Tags.Items.STORAGE_BLOCKS_REDSTONE))
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy(getHasName(EasyBrewing.SPEED_UPGRADE_2), has(EasyBrewing.SPEED_UPGRADE_2))
                .save(output);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_1.get(), 1)
                .pattern("GBG")
                .pattern("BUB")
                .pattern("GBG")
                .define('G', Items.GLASS_BOTTLE)
                .define('B', Items.BLAZE_POWDER)
                .define('U', EasyBrewing.UPGRADE_BASE)
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                .unlockedBy(getHasName(EasyBrewing.UPGRADE_BASE), has(EasyBrewing.UPGRADE_BASE))
                .save(output);

        ShapedRecipeBuilder.shaped(item, RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_2.get(), 1)
                .pattern("GDG")
                .pattern("DAD")
                .pattern("GDG")
                .define('G', Items.GLASS_BOTTLE)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('A', EasyBrewing.AMOUNT_UPGRADE_1)
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy(getHasName(EasyBrewing.AMOUNT_UPGRADE_1), has(EasyBrewing.AMOUNT_UPGRADE_1))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner{
        protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new ModRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "easybrewing recipe provider";
        }
    }
}
