package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Constants.MODID)
public class NetworkEvent {
    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(PotionCountSyncS2CPacket.TYPE, PotionCountSyncS2CPacket.STREAM_CODEC, (packet, context) -> {
            context.enqueueWork(() -> PotionCountSyncS2CPacket.handleOnClient(packet));
        });
        registrar.playToServer(CapabilityChangeSyncC2SPacket.TYPE, CapabilityChangeSyncC2SPacket.STREAM_CODEC, (packet, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    CapabilityChangeSyncC2SPacket.handleOnServer(packet, serverPlayer);
                }
            });
        });
    }
}
