package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.ItemBrewingStationBlock;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.fluid.ModFluidTypes;
import com.coolerpromc.easybrewing.fluid.ModFluids;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(EasyBrewing.MODID)
public class EasyBrewing {
    public static final String MODID = "easybrewing";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);

    // BLOCK & BLOCK ENTITY & MENU
    public static final DeferredBlock<ItemBrewingStationBlock> ITEM_BREWING_STATION = registerBlock("item_brewing_station", properties -> new ItemBrewingStationBlock(properties.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(0.5F).noOcclusion()));
    public static final Supplier<BlockEntityType<ItemBrewingStationBE>> ITEM_BREWING_STATION_BE = registerBlockEntity("item_brewing_station_be", ItemBrewingStationBE::new, ITEM_BREWING_STATION);
    public static final Supplier<MenuType<ItemBrewingStationMenu>> ITEM_BREWING_STATION_MENU = registerMenu("item_brewing_station_menu", ItemBrewingStationMenu::new);

    // ITEM
    public static final DeferredItem<Item> UPGRADE_BASE = registerItem("upgrade_base", Item::new);
    public static final DeferredItem<SpeedUpgradeItem> SPEED_UPGRADE_1 = registerItem("speed_upgrade_1", properties -> new SpeedUpgradeItem(properties, 1.25f));
    public static final DeferredItem<SpeedUpgradeItem> SPEED_UPGRADE_2 = registerItem("speed_upgrade_2", properties -> new SpeedUpgradeItem(properties, 1.5f));
    public static final DeferredItem<SpeedUpgradeItem> SPEED_UPGRADE_3 = registerItem("speed_upgrade_3", properties -> new SpeedUpgradeItem(properties, 1.75f));
    public static final DeferredItem<AmountUpgradeItem> AMOUNT_UPGRADE_1 = registerItem("amount_upgrade_1", properties -> new AmountUpgradeItem(properties, 1));
    public static final DeferredItem<AmountUpgradeItem> AMOUNT_UPGRADE_2 = registerItem("amount_upgrade_2", properties -> new AmountUpgradeItem(properties, 2));

    // CREATIVE TAB
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EASY_BREWING_TAB = CREATIVE_MODE_TABS.register("easy_brewing", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.easybrewing"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(ITEM_BREWING_STATION::toStack)
            .displayItems((parameters, output) -> {
                output.accept(ITEM_BREWING_STATION);
                output.accept(UPGRADE_BASE);
                output.accept(SPEED_UPGRADE_1);
                output.accept(SPEED_UPGRADE_2);
                output.accept(SPEED_UPGRADE_3);
                output.accept(AMOUNT_UPGRADE_1);
                output.accept(AMOUNT_UPGRADE_2);
            }).build());

    public EasyBrewing(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);

        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC);
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func){
        DeferredBlock<T> block = BLOCKS.registerBlock(name, func);
        registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
        return block;
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> supplier, DeferredBlock<?> block){
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(supplier, block.get()));
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> func){
        return ITEMS.registerItem(name, func);
    }

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String name, IContainerFactory<T> factory){
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static Identifier id(String path){
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
