package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.CommonClass;
import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

@EventBusSubscriber(modid = Constants.MODID)
public class CapabilitiesEvent {
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, CommonClass.ITEM_BREWING_STATION_BE.get(),
                (be, direction) -> {
                    Container container = be.getHandlerForSide(direction);
                    return container != null ? VanillaContainerWrapper.of(container) : null;
                });
    }
}
