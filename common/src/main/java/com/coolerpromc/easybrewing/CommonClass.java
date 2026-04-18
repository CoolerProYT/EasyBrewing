package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.ItemBrewingStationBlock;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.platform.Services;
import com.coolerpromc.easybrewing.platform.services.IRegistryHelper;
import com.coolerpromc.easybrewing.platform.util.BlockRegistryHandler;
import com.coolerpromc.easybrewing.platform.util.RegistryHandler;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class CommonClass {
    private static final IRegistryHelper REGISTRY = Services.REGISTRY;

    // BLOCK & BLOCK ENTITY & MENU
    public static final BlockRegistryHandler<ItemBrewingStationBlock> ITEM_BREWING_STATION = REGISTRY.registerBlock(
            "item_brewing_station",
            properties -> new ItemBrewingStationBlock(properties.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(0.5F).noOcclusion()),
            BlockBehaviour.Properties.of()
    );

    @SuppressWarnings("unchecked")
    public static final RegistryHandler<BlockEntityType<ItemBrewingStationBE>> ITEM_BREWING_STATION_BE = REGISTRY.registerBlockEntity(
            "item_brewing_station_be",
            ItemBrewingStationBE::new,
            ITEM_BREWING_STATION
    );

    public static final RegistryHandler<MenuType<ItemBrewingStationMenu>> ITEM_BREWING_STATION_MENU = REGISTRY.registerMenu(
            "item_brewing_station_menu",
            ItemBrewingStationMenu::new,
            BlockPos.STREAM_CODEC
    );

    // ITEMS
    public static final RegistryHandler<Item> UPGRADE_BASE = REGISTRY.registerItem("upgrade_base", Item::new);
    public static final RegistryHandler<SpeedUpgradeItem> SPEED_UPGRADE_1 = REGISTRY.registerItem("speed_upgrade_1", p -> new SpeedUpgradeItem(p, 1.25f));
    public static final RegistryHandler<SpeedUpgradeItem> SPEED_UPGRADE_2 = REGISTRY.registerItem("speed_upgrade_2", p -> new SpeedUpgradeItem(p, 1.5f));
    public static final RegistryHandler<SpeedUpgradeItem> SPEED_UPGRADE_3 = REGISTRY.registerItem("speed_upgrade_3", p -> new SpeedUpgradeItem(p, 1.75f));
    public static final RegistryHandler<AmountUpgradeItem> AMOUNT_UPGRADE_1 = REGISTRY.registerItem("amount_upgrade_1", p -> new AmountUpgradeItem(p, 1));
    public static final RegistryHandler<AmountUpgradeItem> AMOUNT_UPGRADE_2 = REGISTRY.registerItem("amount_upgrade_2", p -> new AmountUpgradeItem(p, 2));

    // CREATIVE TAB
    public static final RegistryHandler<CreativeModeTab> EASY_BREWING_TAB = REGISTRY.registerCreativeTab(
            "easy_brewing",
            ITEM_BREWING_STATION::toStack,
            Component.translatable("creativetab.easybrewing"),
            parameters -> new ItemStack[]{
                    ITEM_BREWING_STATION.toStack(),
                    UPGRADE_BASE.toStack(),
                    SPEED_UPGRADE_1.toStack(),
                    SPEED_UPGRADE_2.toStack(),
                    SPEED_UPGRADE_3.toStack(),
                    AMOUNT_UPGRADE_1.toStack(),
                    AMOUNT_UPGRADE_2.toStack()
            }
    );

    public static void init() {
        // Force class loading to trigger registration
    }

}