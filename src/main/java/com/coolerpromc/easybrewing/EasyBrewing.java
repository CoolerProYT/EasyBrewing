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
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class EasyBrewing implements ModInitializer {
	public static final String MODID = "easybrewing";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    // BLOCK & BLOCK ENTITY & MENU
    public static final ItemBrewingStationBlock ITEM_BREWING_STATION = registerBlock("item_brewing_station", properties -> new ItemBrewingStationBlock(properties.mapColor(MapColor.IRON_GRAY).requiresTool().strength(0.5F).nonOpaque()));
    public static final BlockEntityType<ItemBrewingStationBE> ITEM_BREWING_STATION_BE = registerBlockEntity("item_brewing_station_be", ItemBrewingStationBE::new, ITEM_BREWING_STATION);
    public static final ScreenHandlerType<ItemBrewingStationMenu> ITEM_BREWING_STATION_MENU = registerMenu("item_brewing_station_menu", ItemBrewingStationMenu::new);

    // ITEM
    public static final Item UPGRADE_BASE = registerItem("upgrade_base", Item::new);
    public static final SpeedUpgradeItem SPEED_UPGRADE_1 = registerItem("speed_upgrade_1", properties -> new SpeedUpgradeItem(properties, 1.25f));
    public static final SpeedUpgradeItem SPEED_UPGRADE_2 = registerItem("speed_upgrade_2", properties -> new SpeedUpgradeItem(properties, 1.5f));
    public static final SpeedUpgradeItem SPEED_UPGRADE_3 = registerItem("speed_upgrade_3", properties -> new SpeedUpgradeItem(properties, 1.75f));
    public static final AmountUpgradeItem AMOUNT_UPGRADE_1 = registerItem("amount_upgrade_1", properties -> new AmountUpgradeItem(properties, 1));
    public static final AmountUpgradeItem AMOUNT_UPGRADE_2 = registerItem("amount_upgrade_2", properties -> new AmountUpgradeItem(properties, 2));

    // CREATIVE TAB
    public static final ItemGroup EASY_BREWING_TAB = Registry.register(Registries.ITEM_GROUP, RegistryKey.of(RegistryKeys.ITEM_GROUP, id("easy_brewing")), FabricItemGroup.builder()
            .displayName(Text.translatable("creativetab.easybrewing"))
            .icon(ITEM_BREWING_STATION.asItem()::getDefaultStack)
            .entries((parameters, output) -> {
                output.add(ITEM_BREWING_STATION);
                output.add(UPGRADE_BASE);
                output.add(SPEED_UPGRADE_1);
                output.add(SPEED_UPGRADE_2);
                output.add(SPEED_UPGRADE_3);
                output.add(AMOUNT_UPGRADE_1);
                output.add(AMOUNT_UPGRADE_2);
            }).build());


    @Override
	public void onInitialize() {
        CapabilitiesEvent.registerCapabilities();
        ConfigEvent.onDatapackSync();
        NetworkEvent.onRegisterPayloadHandlers();
        CommonConfig.CONFIG.load();

        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(item -> item instanceof PotionItem || item instanceof LingeringPotionItem || item instanceof SplashPotionItem, (builder, item) -> {
                builder.add(DataComponentTypes.MAX_STACK_SIZE, CommonConfig.CONFIG.potionStackSize);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CapabilityChangeSyncC2SPacket.TYPE, CapabilityChangeSyncC2SPacket::handle);
	}

    public static <T extends Block> T registerBlock(String name, Function<AbstractBlock.Settings, T> func){
        T block = Registry.register(Registries.BLOCK, RegistryKey.of(RegistryKeys.BLOCK, id(name)), func.apply(AbstractBlock.Settings.create()));
        registerItem(name, properties -> new BlockItem(block, properties));
        return block;
    }

    public static <T extends BlockEntity, B extends Block> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType.BlockEntityFactory<T> supplier, B block){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, RegistryKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, id(name)), BlockEntityType.Builder.create(supplier, block).build(null));
    }

    public static <T extends Item> T registerItem(String name, Function<Item.Settings, T> func){
        return Registry.register(Registries.ITEM, RegistryKey.of(RegistryKeys.ITEM, id(name)), func.apply(new Item.Settings()));
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> registerMenu(String name, ExtendedScreenHandlerType.ExtendedFactory<T, BlockPos> factory){
        return Registry.register(Registries.SCREEN_HANDLER, RegistryKey.of(RegistryKeys.SCREEN_HANDLER, id(name)), new ExtendedScreenHandlerType<>(factory, BlockPos.PACKET_CODEC));
    }

    public static Identifier id(String path){
        return Identifier.of(MODID, path);
    }
}