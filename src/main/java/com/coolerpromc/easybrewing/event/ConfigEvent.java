package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ConfigEvent {
    public static void onDatapackSync(){
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayer, b) ->
                ServerPlayNetworking.send(serverPlayer, PotionCountSyncS2CPacket.TYPE, PotionCountSyncS2CPacket.encode(PacketByteBufs.create(), new PotionCountSyncS2CPacket(CommonConfig.CONFIG.potionCount()))));
    }
}
