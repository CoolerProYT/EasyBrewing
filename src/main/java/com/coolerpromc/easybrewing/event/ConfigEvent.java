package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = EasyBrewing.MODID)
public class ConfigEvent {
    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        EasyBrewing.CHANNEL.send(new PotionCountSyncS2CPacket(CommonConfig.CONFIG.potionCount()), PacketDistributor.ALL.noArg());
    }

    @SubscribeEvent
    public static void onOnDatapackSync(OnDatapackSyncEvent event) {
        EasyBrewing.CHANNEL.send(new PotionCountSyncS2CPacket(CommonConfig.CONFIG.potionCount()), PacketDistributor.ALL.noArg());
    }
}
