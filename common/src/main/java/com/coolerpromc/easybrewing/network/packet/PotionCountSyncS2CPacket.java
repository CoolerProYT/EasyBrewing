package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PotionCountSyncS2CPacket(int potionCount, int cobblemonPotionCount) implements CustomPacketPayload {
    public static final Type<PotionCountSyncS2CPacket> TYPE = new Type<>(Constants.id("potion_count_sync"));
    public static int POTION_COUNT = CommonConfig.potionCount();
    public static int COBBLEMON_POTION_COUNT = CommonConfig.cobblemonPotionCount;

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionCountSyncS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PotionCountSyncS2CPacket::potionCount,
            ByteBufCodecs.INT,
            PotionCountSyncS2CPacket::cobblemonPotionCount,
            PotionCountSyncS2CPacket::new
    );

    public static void handleOnClient(PotionCountSyncS2CPacket packet) {
        POTION_COUNT = packet.potionCount();
        COBBLEMON_POTION_COUNT = packet.cobblemonPotionCount();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
