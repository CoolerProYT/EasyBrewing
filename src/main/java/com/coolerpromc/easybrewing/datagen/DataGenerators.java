package com.coolerpromc.easybrewing.datagen;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = EasyBrewing.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput));
        generator.addProvider(event.includeServer(), new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
    }
}
