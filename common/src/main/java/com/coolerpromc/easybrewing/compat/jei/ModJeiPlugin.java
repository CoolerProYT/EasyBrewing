package com.coolerpromc.easybrewing.compat.jei;

import com.coolerpromc.easybrewing.CommonClass;
import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.compat.jei.recipe.ItemBrewingRecipe;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.PotionIngredient;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ItemBrewingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(ItemBrewingCategory.TYPE, CommonClass.ITEM_BREWING_STATION.toStack());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        assert level != null;

        // Brewing recipes are no longer synced to the client (they're excluded from the recipe book,
        // so ClientboundUpdateRecipesPacket never carries them), so read the datapack jsons directly.
        RegistryOps<JsonElement> ops = level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        List<ItemBrewingRecipe> recipeList = new ArrayList<>();

        var resources = minecraft.getResourceManager().listResources("recipe/brewing", path -> path.getPath().endsWith(".json"));
        for (Resource resource : resources.values()) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                BrewingRecipe.MAP_CODEC.codec().parse(ops, json).result().ifPresent(brewingRecipe -> addJeiRecipe(brewingRecipe, recipeList));
            } catch (IOException e) {
                // ignore unreadable recipe file
            }
        }

        registration.addRecipes(ItemBrewingCategory.TYPE, recipeList);
    }

    private static void addJeiRecipe(BrewingRecipe brewingRecipe, List<ItemBrewingRecipe> recipeList) {
        PotionIngredient input = brewingRecipe.getInput();
        Optional<Holder<Potion>> from = input.potions()
                .flatMap(predicate -> predicate.potions())
                .flatMap(potions -> potions.stream().findFirst());

        List<ItemStack> inputs = new ArrayList<>();
        for (ItemStack containerStack : input.ingredient().items().map(Holder::value).map(ItemStack::new).toList()) {
            ItemStack inputStack = containerStack.copy();
            from.ifPresent(potion -> inputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion)));
            inputStack.setCount(PotionCountSyncS2CPacket.POTION_COUNT);
            inputs.add(inputStack);
        }

        List<ItemStack> ingredients = new ArrayList<>();
        for (ItemStack ingStack : brewingRecipe.getReagent().ingredient().items().map(Holder::value).map(ItemStack::new).toList()) {
            ingredients.add(ingStack.copy());
        }

        ItemStack output = brewingRecipe.getOutput().create();
        output.setCount(PotionCountSyncS2CPacket.POTION_COUNT);

        if (!inputs.isEmpty() && !ingredients.isEmpty() && !output.isEmpty()) {
            recipeList.add(new ItemBrewingRecipe(ingredients, inputs, output));
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ItemBrewingStationScreen.class, 76, 34, 20, 22, ItemBrewingCategory.TYPE);
    }

    @Override
    public Identifier getPluginUid() {
        return Constants.id("jei_plugin");
    }
}
