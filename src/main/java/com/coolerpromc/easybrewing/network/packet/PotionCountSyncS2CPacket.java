package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record PotionCountSyncS2CPacket(int potionCount, int cobblemonPotionCount) implements CustomPayload {
    public static final Id<PotionCountSyncS2CPacket> TYPE = new Id<>(EasyBrewing.id("potion_count_sync"));
    public static int POTION_COUNT = CommonConfig.CONFIG.potionCount();
    public static int COBBLEMON_POTION_COUNT = CommonConfig.CONFIG.cobblemonPotionCount;

    public static final PacketCodec<RegistryByteBuf, PotionCountSyncS2CPacket> STREAM_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            PotionCountSyncS2CPacket::potionCount,
            PacketCodecs.INTEGER,
            PotionCountSyncS2CPacket::cobblemonPotionCount,
            PotionCountSyncS2CPacket::new
    );

    public static void handle(PotionCountSyncS2CPacket packet, ClientPlayNetworking.Context context){
        POTION_COUNT = packet.potionCount();
        COBBLEMON_POTION_COUNT = packet.cobblemonPotionCount();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
