package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.entity.renderer.ItemBrewingStationBER;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class EasyBrewingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(EasyBrewing.ITEM_BREWING_STATION_MENU, ItemBrewingStationScreen::new);
        BlockEntityRendererFactories.register(EasyBrewing.ITEM_BREWING_STATION_BE, ItemBrewingStationBER::new);
        ClientPlayNetworking.registerGlobalReceiver(PotionCountSyncS2CPacket.TYPE, PotionCountSyncS2CPacket::handle);
    }
}
