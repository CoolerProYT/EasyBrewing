package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> recipeOutput) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EasyBrewing.ITEM_BREWING_STATION.asItem(), 1)
                .pattern(" D ")
                .pattern("DBD")
                .pattern("SSS")
                .input('D', ConventionalItemTags.DIAMONDS)
                .input('B', Items.BLAZE_POWDER)
                .input('S', ItemTags.STONE_CRAFTING_MATERIALS)
                .criterion("has_diamond", conditionsFromTag(ConventionalItemTags.DIAMONDS))
                .criterion(hasItem(Items.BLAZE_POWDER), conditionsFromItem(Items.BLAZE_POWDER))
                .criterion("has_stone_material", conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS))
                .offerTo(recipeOutput);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EasyBrewing.UPGRADE_BASE, 1)
                .pattern("IPI")
                .pattern("PBP")
                .pattern("IPI")
                .input('I', ConventionalItemTags.IRON_INGOTS)
                .input('P', Items.PAPER)
                .input('B', Items.BLAZE_POWDER)
                .criterion("has_iron_ingot", conditionsFromTag(ConventionalItemTags.IRON_INGOTS))
                .criterion("has_paper", conditionsFromItem(Items.PAPER))
                .criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
                .offerTo(recipeOutput);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_1, 1)
                .pattern("GRG")
                .pattern("RSR")
                .pattern("GRG")
                .input('G', ConventionalItemTags.GOLD_INGOTS)
                .input('R', ConventionalItemTags.REDSTONE_DUSTS)
                .input('S', EasyBrewing.UPGRADE_BASE)
                .criterion("has_gold_ingot", conditionsFromTag(ConventionalItemTags.GOLD_INGOTS))
                .criterion("has_redstone_dust", conditionsFromTag(ConventionalItemTags.REDSTONE_DUSTS))
                .criterion(hasItem(EasyBrewing.UPGRADE_BASE), conditionsFromItem(EasyBrewing.UPGRADE_BASE))
                .offerTo(recipeOutput);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_2, 1)
                .pattern("LRL")
                .pattern("RSR")
                .pattern("LRL")
                .input('L', Blocks.LAPIS_BLOCK)
                .input('R', ConventionalItemTags.REDSTONE_DUSTS)
                .input('S', EasyBrewing.SPEED_UPGRADE_1)
                .criterion("has_lapis_lazuli_block", conditionsFromItem(Blocks.LAPIS_BLOCK))
                .criterion("has_redstone_dust", conditionsFromTag(ConventionalItemTags.REDSTONE_DUSTS))
                .criterion(hasItem(EasyBrewing.SPEED_UPGRADE_1), conditionsFromItem(EasyBrewing.SPEED_UPGRADE_1))
                .offerTo(recipeOutput);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EasyBrewing.SPEED_UPGRADE_3, 1)
                .pattern("BRB")
                .pattern("RSR")
                .pattern("BRB")
                .input('B', Blocks.REDSTONE_BLOCK)
                .input('R', ConventionalItemTags.REDSTONE_DUSTS)
                .input('S', EasyBrewing.SPEED_UPGRADE_2)
                .criterion("has_redstone_block", conditionsFromItem(Blocks.REDSTONE_BLOCK))
                .criterion("has_redstone_dust", conditionsFromTag(ConventionalItemTags.REDSTONE_DUSTS))
                .criterion(hasItem(EasyBrewing.SPEED_UPGRADE_2), conditionsFromItem(EasyBrewing.SPEED_UPGRADE_2))
                .offerTo(recipeOutput);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_1, 1)
                .pattern("GBG")
                .pattern("BUB")
                .pattern("GBG")
                .input('G', Items.GLASS_BOTTLE)
                .input('B', Items.BLAZE_POWDER)
                .input('U', EasyBrewing.UPGRADE_BASE)
                .criterion("has_glass_bottle", conditionsFromItem(Items.GLASS_BOTTLE))
                .criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
                .criterion(hasItem(EasyBrewing.UPGRADE_BASE), conditionsFromItem(EasyBrewing.UPGRADE_BASE))
                .offerTo(recipeOutput);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, EasyBrewing.AMOUNT_UPGRADE_2, 1)
                .pattern("GDG")
                .pattern("DAD")
                .pattern("GDG")
                .input('G', Items.GLASS_BOTTLE)
                .input('D', ConventionalItemTags.DIAMONDS)
                .input('A', EasyBrewing.AMOUNT_UPGRADE_1)
                .criterion("has_glass_bottle", conditionsFromItem(Items.GLASS_BOTTLE))
                .criterion("has_diamond", conditionsFromTag(ConventionalItemTags.DIAMONDS))
                .criterion(hasItem(EasyBrewing.AMOUNT_UPGRADE_1), conditionsFromItem(EasyBrewing.AMOUNT_UPGRADE_1))
                .offerTo(recipeOutput);
    }
}
