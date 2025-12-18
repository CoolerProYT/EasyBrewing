package com.coolerpromc.easybrewing.datagen.loot;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModBlockLootTables extends FabricBlockLootTableProvider {
    public ModBlockLootTables(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        addDrop(EasyBrewing.ITEM_BREWING_STATION);
    }
}
