package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.config.NeoForgeCommonConfig;
import com.coolerpromc.easybrewing.platform.NeoForgeRegistryHelper;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.function.Consumer;

@Mod(Constants.MODID)
public class EasyBrewing {
    public EasyBrewing(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeRegistryHelper.register(modEventBus);
        CommonClass.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeCommonConfig.CONFIG_SPEC);

        modEventBus.addListener(this::onModifyDefaultComponents);
    }

    @SubscribeEvent
    public void onModifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        Consumer<DataComponentMap.Builder> consumer = builder -> builder.set(DataComponents.MAX_STACK_SIZE, CommonConfig.potionStackSize);
        event.modify(Items.LINGERING_POTION, consumer);
        event.modify(Items.POTION, consumer);
        event.modify(Items.SPLASH_POTION, consumer);
    }
}