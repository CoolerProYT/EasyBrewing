package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.ItemBrewingStationBlock;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.event.CapabilitiesEvent;
import com.coolerpromc.easybrewing.event.ConfigEvent;
import com.coolerpromc.easybrewing.event.NetworkEvent;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class EasyBrewing implements ModInitializer {
	public static final String MODID = "easybrewing";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    // BLOCK & BLOCK ENTITY & MENU
    public static final ItemBrewingStationBlock ITEM_BREWING_STATION = registerBlock("item_brewing_station", properties -> new ItemBrewingStationBlock(properties.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(0.5F).noOcclusion()));
    public static final BlockEntityType<ItemBrewingStationBE> ITEM_BREWING_STATION_BE = registerBlockEntity("item_brewing_station_be", ItemBrewingStationBE::new, ITEM_BREWING_STATION);
    public static final MenuType<ItemBrewingStationMenu> ITEM_BREWING_STATION_MENU = registerMenu("item_brewing_station_menu", ItemBrewingStationMenu::new);

    // ITEM
    public static final Item UPGRADE_BASE = registerItem("upgrade_base", Item::new);
    public static final SpeedUpgradeItem SPEED_UPGRADE_1 = registerItem("speed_upgrade_1", properties -> new SpeedUpgradeItem(properties, 1.25f));
    public static final SpeedUpgradeItem SPEED_UPGRADE_2 = registerItem("speed_upgrade_2", properties -> new SpeedUpgradeItem(properties, 1.5f));
    public static final SpeedUpgradeItem SPEED_UPGRADE_3 = registerItem("speed_upgrade_3", properties -> new SpeedUpgradeItem(properties, 1.75f));
    public static final AmountUpgradeItem AMOUNT_UPGRADE_1 = registerItem("amount_upgrade_1", properties -> new AmountUpgradeItem(properties, 1));
    public static final AmountUpgradeItem AMOUNT_UPGRADE_2 = registerItem("amount_upgrade_2", properties -> new AmountUpgradeItem(properties, 2));

    // CREATIVE TAB
    public static final CreativeModeTab EASY_BREWING_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceKey.create(Registries.CREATIVE_MODE_TAB, id("easy_brewing")), FabricCreativeModeTab.builder()
            .title(Component.translatable("creativetab.easybrewing"))
            .icon(ITEM_BREWING_STATION.asItem()::getDefaultInstance)
            .displayItems((parameters, output) -> {
                output.accept(ITEM_BREWING_STATION);
                output.accept(UPGRADE_BASE);
                output.accept(SPEED_UPGRADE_1);
                output.accept(SPEED_UPGRADE_2);
                output.accept(SPEED_UPGRADE_3);
                output.accept(AMOUNT_UPGRADE_1);
                output.accept(AMOUNT_UPGRADE_2);
            }).build());


	@Override
	public void onInitialize() {
        CapabilitiesEvent.registerCapabilities();
        ConfigEvent.onDatapackSync();
        NetworkEvent.onRegisterPayloadHandlers();
        CommonConfig.CONFIG.load();

        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(item -> item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem, (builder, item) -> {
                builder.set(DataComponents.MAX_STACK_SIZE, CommonConfig.CONFIG.potionStackSize);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CapabilityChangeSyncC2SPacket.TYPE, CapabilityChangeSyncC2SPacket::handle);
	}

    public static <T extends Block> T registerBlock(String name, Function<BlockBehaviour.Properties, T> func){
        T block = Registry.register(BuiltInRegistries.BLOCK, ResourceKey.create(Registries.BLOCK, id(name)), func.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id(name)))));
        registerItem(name, properties -> new BlockItem(block, properties.useBlockDescriptionPrefix()));
        return block;
    }

    public static <T extends BlockEntity, B extends Block> BlockEntityType<T> registerBlockEntity(String name, FabricBlockEntityTypeBuilder.Factory<T> supplier, B block){
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, id(name)), FabricBlockEntityTypeBuilder.create(supplier, block).build());
    }

    public static <T extends Item> T registerItem(String name, Function<Item.Properties, T> func){
        return Registry.register(BuiltInRegistries.ITEM, ResourceKey.create(Registries.ITEM, id(name)), func.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id(name)))));
    }

    public static <T extends AbstractContainerMenu> MenuType<T> registerMenu(String name, ExtendedMenuType.ExtendedFactory<T, BlockPos> factory){
        return Registry.register(BuiltInRegistries.MENU, ResourceKey.create(Registries.MENU, id(name)), new ExtendedMenuType<>(factory, BlockPos.STREAM_CODEC));
    }

    public static Identifier id(String path){
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}