package com.coolerpromc.easybrewing.datagen.loot;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(EasyBrewing.ITEM_BREWING_STATION.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> Optional.of(BuiltInRegistries.BLOCK.getKey(block)).filter(key -> key.getNamespace().equals(EasyBrewing.MODID)).isPresent()).collect(Collectors.toSet());
    }
}
