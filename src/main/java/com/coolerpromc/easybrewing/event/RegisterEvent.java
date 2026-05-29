package com.coolerpromc.easybrewing.event;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.compat.create.BrewingStationArmPointType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EasyBrewing.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterEvent {
    @SubscribeEvent
    public static void onRegister(net.minecraftforge.registries.RegisterEvent event) {
        if (ModList.get().isLoaded("create") && event.getRegistryKey().equals(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE.key())){
            Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, EasyBrewing.id("brewing_station"), new BrewingStationArmPointType());
        }
    }
}
