package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.entity.renderer.ItemBrewingStationBER;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EasyBrewing.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EasyBrewingClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(EasyBrewing.ITEM_BREWING_STATION_MENU.get(), ItemBrewingStationScreen::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EasyBrewing.ITEM_BREWING_STATION_BE.get(), ItemBrewingStationBER::new);
    }
}