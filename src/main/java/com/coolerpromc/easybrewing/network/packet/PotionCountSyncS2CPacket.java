package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record PotionCountSyncS2CPacket(int potionCount) implements CustomPacketPayload {
    public static final Type<PotionCountSyncS2CPacket> TYPE = new Type<>(EasyBrewing.id("potion_count_sync"));
    public static int POTION_COUNT = 3;

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionCountSyncS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PotionCountSyncS2CPacket::potionCount,
            PotionCountSyncS2CPacket::new
    );

    public static void handle(PotionCountSyncS2CPacket packet, CustomPayloadEvent.Context context){
        context.enqueueWork(() -> {
            POTION_COUNT = packet.potionCount();
        }).exceptionally(throwable -> {
            context.getConnection().disconnect(Component.literal(throwable.getLocalizedMessage()));
            return null;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
