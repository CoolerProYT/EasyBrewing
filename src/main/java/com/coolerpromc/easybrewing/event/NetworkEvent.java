package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NetworkEvent {
    public static void onRegisterPayloadHandlers(){
        PayloadTypeRegistry.playS2C().register(PotionCountSyncS2CPacket.TYPE, PotionCountSyncS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(CapabilityChangeSyncC2SPacket.TYPE, CapabilityChangeSyncC2SPacket.STREAM_CODEC);
    }
}
