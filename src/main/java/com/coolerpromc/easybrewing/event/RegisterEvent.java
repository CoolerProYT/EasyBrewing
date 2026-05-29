package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.create.BrewingStationArmPointType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = EasyBrewing.MODID)
public class RegisterEvent {
    @SubscribeEvent
    public static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (ModList.get().isLoaded("create") && event.getRegistryKey().equals(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE.key())){
            Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, EasyBrewing.id("brewing_station"), new BrewingStationArmPointType());
        }
    }
}
