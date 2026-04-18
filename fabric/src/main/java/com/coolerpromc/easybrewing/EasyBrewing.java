package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.config.FabricCommonConfig;
import com.coolerpromc.easybrewing.event.CapabilitiesEvent;
import com.coolerpromc.easybrewing.event.ConfigEvent;
import com.coolerpromc.easybrewing.event.NetworkEvent;
import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;

public class EasyBrewing implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        FabricCommonConfig.CONFIG.load();

        CapabilitiesEvent.registerCapabilities();
        ConfigEvent.onDatapackSync();
        NetworkEvent.onRegisterPayloadHandlers();

        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(item -> item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem, (builder, item) -> {
                builder.set(DataComponents.MAX_STACK_SIZE, FabricCommonConfig.CONFIG.potionStackSize);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CapabilityChangeSyncC2SPacket.TYPE, (packet, context) -> {
            context.server().execute(() -> CapabilityChangeSyncC2SPacket.handleOnServer(packet, context.player()));
        });
    }
}
