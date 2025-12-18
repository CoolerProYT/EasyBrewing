package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record PotionCountSyncS2CPacket(int potionCount) {
    public static final Identifier TYPE = EasyBrewing.id("potion_count_sync");
    public static int POTION_COUNT = CommonConfig.CONFIG.potionCount();

    public static PacketByteBuf encode(PacketByteBuf buf, PotionCountSyncS2CPacket packet){
        buf.writeInt(packet.potionCount());
        return buf;
    }

    public static PotionCountSyncS2CPacket decode(PacketByteBuf buf){
        return new PotionCountSyncS2CPacket(buf.readInt());
    }

    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        PotionCountSyncS2CPacket packet = decode(buf);
        POTION_COUNT = packet.potionCount();
    }
}
