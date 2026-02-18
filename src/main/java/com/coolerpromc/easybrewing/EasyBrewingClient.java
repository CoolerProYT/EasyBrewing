package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.entity.renderer.ItemBrewingStationBER;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = EasyBrewing.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EasyBrewing.MODID, value = Dist.CLIENT)
public class EasyBrewingClient {
    public EasyBrewingClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(EasyBrewing.ITEM_BREWING_STATION_MENU.get(), ItemBrewingStationScreen::new);
    }

    @SubscribeEvent
    public static void onEntityRenderersRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EasyBrewing.ITEM_BREWING_STATION_BE.get(), ItemBrewingStationBER::new);
    }
}
