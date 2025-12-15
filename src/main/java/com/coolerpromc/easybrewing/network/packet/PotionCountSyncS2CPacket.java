package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PotionCountSyncS2CPacket(int potionCount, int cobblemonPotionCount) implements CustomPacketPayload {
    public static final Type<PotionCountSyncS2CPacket> TYPE = new Type<>(EasyBrewing.id("potion_count_sync"));
    public static int POTION_COUNT = CommonConfig.CONFIG.potionCount();
    public static int COBBLEMON_POTION_COUNT = CommonConfig.CONFIG.cobblemonPotionCount;

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionCountSyncS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PotionCountSyncS2CPacket::potionCount,
            ByteBufCodecs.INT,
            PotionCountSyncS2CPacket::cobblemonPotionCount,
            PotionCountSyncS2CPacket::new
    );

    public static void handle(PotionCountSyncS2CPacket packet, ServerPlayNetworking.Context context){
        POTION_COUNT = packet.potionCount();
        COBBLEMON_POTION_COUNT = packet.cobblemonPotionCount();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
