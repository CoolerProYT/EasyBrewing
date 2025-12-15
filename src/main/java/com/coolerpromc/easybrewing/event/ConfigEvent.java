package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = EasyBrewing.MODID)
public class ConfigEvent {
    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        PacketDistributor.sendToAllPlayers(new PotionCountSyncS2CPacket(CommonConfig.CONFIG.potionCount(), CommonConfig.CONFIG.cobblemonPotionCount.get()));
    }

    @SubscribeEvent
    public static void onOnDatapackSync(OnDatapackSyncEvent event) {
        PacketDistributor.sendToAllPlayers(new PotionCountSyncS2CPacket(CommonConfig.CONFIG.potionCount(), CommonConfig.CONFIG.cobblemonPotionCount.get()));
    }
}
