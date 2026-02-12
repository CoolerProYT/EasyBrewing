package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NetworkEvent {
    public static void onRegisterPayloadHandlers(){
        PayloadTypeRegistry.clientboundPlay().register(PotionCountSyncS2CPacket.TYPE, PotionCountSyncS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(CapabilityChangeSyncC2SPacket.TYPE, CapabilityChangeSyncC2SPacket.STREAM_CODEC);
    }
}
