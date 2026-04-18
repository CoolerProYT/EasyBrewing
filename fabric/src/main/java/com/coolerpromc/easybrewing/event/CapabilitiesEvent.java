package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.CommonClass;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class CapabilitiesEvent {
    public static void registerCapabilities() {
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> {
            var container = be.getHandlerForSide(direction);
            return container != null ? ContainerStorage.of(container, direction) : null;
        }, CommonClass.ITEM_BREWING_STATION_BE.get());
    }
}
