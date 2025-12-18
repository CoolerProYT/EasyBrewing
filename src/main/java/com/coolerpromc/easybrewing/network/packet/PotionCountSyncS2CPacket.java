package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.config.CommonConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PotionCountSyncS2CPacket(int potionCount) {
    public static int POTION_COUNT = CommonConfig.CONFIG.potionCount();

    public static PotionCountSyncS2CPacket decode(FriendlyByteBuf buf){
        return new PotionCountSyncS2CPacket(buf.readInt());
    }

    public static void encode(PotionCountSyncS2CPacket packet, FriendlyByteBuf buf){
        buf.writeInt(packet.potionCount());
    }

    public static void handle(PotionCountSyncS2CPacket packet, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            POTION_COUNT = packet.potionCount();
        }).exceptionally(throwable -> {
            context.get().getNetworkManager().disconnect(Component.literal(throwable.getLocalizedMessage()));
            return null;
        });
    }
}
