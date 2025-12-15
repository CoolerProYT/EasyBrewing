package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.entity.renderer.ItemBrewingStationBER;
import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class EasyBrewingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(EasyBrewing.ITEM_BREWING_STATION_MENU, ItemBrewingStationScreen::new);
        BlockEntityRenderers.register(EasyBrewing.ITEM_BREWING_STATION_BE, ItemBrewingStationBER::new);
        ClientPlayNetworking.registerGlobalReceiver(CapabilityChangeSyncC2SPacket.TYPE, CapabilityChangeSyncC2SPacket::handle);
    }
}
