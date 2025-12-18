package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.datagen.loot.ModBlockLootTables;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack packOutput = generator.createPack();

        packOutput.addProvider(ModBlockLootTables::new);
        packOutput.addProvider(ModBlockTagProvider::new);
        packOutput.addProvider(ModRecipeProvider::new);
	}
}
