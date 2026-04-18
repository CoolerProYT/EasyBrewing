package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.config.NeoForgeCommonConfig;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Constants.MODID)
public class ConfigEvent {
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        NeoForgeCommonConfig.syncToCommon();
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        NeoForgeCommonConfig.syncToCommon();
        PacketDistributor.sendToAllPlayers(new PotionCountSyncS2CPacket(CommonConfig.potionCount(), CommonConfig.cobblemonPotionCount));
    }

    @SubscribeEvent
    public static void onOnDatapackSync(OnDatapackSyncEvent event) {
        PacketDistributor.sendToAllPlayers(new PotionCountSyncS2CPacket(CommonConfig.potionCount(), CommonConfig.cobblemonPotionCount));
    }
}
