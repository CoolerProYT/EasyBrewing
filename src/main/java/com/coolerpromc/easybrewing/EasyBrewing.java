package com.coolerpromc.easybrewing;

import com.coolerpromc.easybrewing.block.ItemBrewingStationBlock;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.config.CommonConfig;
import com.coolerpromc.easybrewing.item.AmountUpgradeItem;
import com.coolerpromc.easybrewing.item.SpeedUpgradeItem;
import com.coolerpromc.easybrewing.network.packet.CapabilityChangeSyncC2SPacket;
import com.coolerpromc.easybrewing.network.packet.PotionCountSyncS2CPacket;
import com.coolerpromc.easybrewing.screen.ItemBrewingStationMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Set;
import java.util.function.Function;

@Mod(EasyBrewing.MODID)
public final class EasyBrewing {
    public static final String MODID = "easybrewing";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);

    // BLOCK & BLOCK ENTITY & MENU
    public static final RegistryObject<ItemBrewingStationBlock> ITEM_BREWING_STATION = registerBlock("item_brewing_station", properties -> new ItemBrewingStationBlock(properties.mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(0.5F).noOcclusion()));
    public static final RegistryObject<BlockEntityType<ItemBrewingStationBE>> ITEM_BREWING_STATION_BE = registerBlockEntity("item_brewing_station_be", ItemBrewingStationBE::new, ITEM_BREWING_STATION);
    public static final RegistryObject<MenuType<ItemBrewingStationMenu>> ITEM_BREWING_STATION_MENU = registerMenu("item_brewing_station_menu", ItemBrewingStationMenu::new);

    // ITEM
    public static final RegistryObject<Item> UPGRADE_BASE = registerItem("upgrade_base", Item::new);
    public static final RegistryObject<SpeedUpgradeItem> SPEED_UPGRADE_1 = registerItem("speed_upgrade_1", properties -> new SpeedUpgradeItem(properties, 1.25f));
    public static final RegistryObject<SpeedUpgradeItem> SPEED_UPGRADE_2 = registerItem("speed_upgrade_2", properties -> new SpeedUpgradeItem(properties, 1.5f));
    public static final RegistryObject<SpeedUpgradeItem> SPEED_UPGRADE_3 = registerItem("speed_upgrade_3", properties -> new SpeedUpgradeItem(properties, 1.75f));
    public static final RegistryObject<AmountUpgradeItem> AMOUNT_UPGRADE_1 = registerItem("amount_upgrade_1", properties -> new AmountUpgradeItem(properties, 1));
    public static final RegistryObject<AmountUpgradeItem> AMOUNT_UPGRADE_2 = registerItem("amount_upgrade_2", properties -> new AmountUpgradeItem(properties, 2));

    // CREATIVE TAB
    public static final RegistryObject<CreativeModeTab> EASY_BREWING_TAB = CREATIVE_MODE_TABS.register("easy_brewing", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.easybrewing"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(ITEM_BREWING_STATION.get().asItem()::getDefaultInstance)
            .displayItems((parameters, output) -> {
                output.accept(ITEM_BREWING_STATION.get());
                output.accept(UPGRADE_BASE.get());
                output.accept(SPEED_UPGRADE_1.get());
                output.accept(SPEED_UPGRADE_2.get());
                output.accept(SPEED_UPGRADE_3.get());
                output.accept(AMOUNT_UPGRADE_1.get());
                output.accept(AMOUNT_UPGRADE_2.get());
            }).build());

    public static final Channel<CustomPacketPayload> CHANNEL = ChannelBuilder
            .named(id("channel"))
            .networkProtocolVersion(0)
            .payloadChannel()
            .play()
            .bidirectional()
            .addMain(PotionCountSyncS2CPacket.TYPE, PotionCountSyncS2CPacket.STREAM_CODEC, PotionCountSyncS2CPacket::handle)
            .addMain(CapabilityChangeSyncC2SPacket.TYPE, CapabilityChangeSyncC2SPacket.STREAM_CODEC, CapabilityChangeSyncC2SPacket::handle)
            .build();

    public EasyBrewing(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        BLOCKS.register(modBusGroup);
        BLOCK_ENTITIES.register(modBusGroup);
        ITEMS.register(modBusGroup);
        CREATIVE_MODE_TABS.register(modBusGroup);
        MENUS.register(modBusGroup);

        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC);
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func){
        RegistryObject<T> block = BLOCKS.register(name, () -> func.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id(name)))));
        registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
        return block;
    }

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> supplier, RegistryObject<? extends Block> block){
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(supplier, Set.of(block.get())));
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Function<Item.Properties, T> func){
        return ITEMS.register(name, () -> func.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id(name)))));
    }

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenu(String name, IContainerFactory<T> factory){
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static Identifier id(String path){
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
