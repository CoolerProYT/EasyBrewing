package com.coolerpromc.easybrewing.datagen.loot;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class ModBlockLootTables extends FabricBlockLootSubProvider {
    public ModBlockLootTables(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        dropSelf(EasyBrewing.ITEM_BREWING_STATION);
    }
}
