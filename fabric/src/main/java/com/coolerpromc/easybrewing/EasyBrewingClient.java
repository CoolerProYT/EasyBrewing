package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.entity.renderer.ItemBrewingStationBER;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class EasyBrewingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(CommonClass.ITEM_BREWING_STATION_MENU.get(), ItemBrewingStationScreen::new);
        BlockEntityRenderers.register(CommonClass.ITEM_BREWING_STATION_BE.get(), ItemBrewingStationBER::new);
        ClientPlayNetworking.registerGlobalReceiver(PotionCountSyncS2CPacket.TYPE, (packet, context) -> {
            context.client().execute(() -> PotionCountSyncS2CPacket.handleOnClient(packet));
        });
    }
}
