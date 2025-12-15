package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class CapabilitiesEvent {
    public static void registerCapabilities(){
        ItemStorage.SIDED.registerForBlockEntity(ItemBrewingStationBE::getCapability, EasyBrewing.ITEM_BREWING_STATION_BE);
    }
}
