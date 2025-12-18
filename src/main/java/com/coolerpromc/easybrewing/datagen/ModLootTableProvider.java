package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.datagen.loot.ModBlockLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)));
    }
}
