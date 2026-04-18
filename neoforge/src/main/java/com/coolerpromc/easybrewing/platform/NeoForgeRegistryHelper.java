package com.coolerpromc.easybrewing.platform;

import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.platform.services.IRegistryHelper;
import com.coolerpromc.easybrewing.platform.util.BlockEntityTypeFactory;
import com.coolerpromc.easybrewing.platform.util.BlockRegistryHandler;
import com.coolerpromc.easybrewing.platform.util.MenuFactory;
import com.coolerpromc.easybrewing.platform.util.RegistryHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Constants.MODID);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENUS.register(modEventBus);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Block> BlockRegistryHandler<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties p) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, func);
        ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
        return new BlockRegistryHandler<>() {
            @Override
            public Identifier id() { return block.getId(); }
            @Override
            public Holder<T> holder() { return (Holder<T>) block; }
            @Override
            public T get() { return block.get(); }
            @Override
            public Item asItem() { return block.get().asItem(); }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Item> RegistryHandler<T> registerItem(String name, Function<Item.Properties, T> func) {
        DeferredItem<T> item = ITEMS.registerItem(name, func);
        return new RegistryHandler<>() {
            @Override
            public Identifier id() { return item.getId(); }
            @Override
            public Holder<T> holder() { return (Holder<T>) item; }
            @Override
            public T get() { return item.get(); }
        };
    }

    @Override
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T extends BlockEntity> RegistryHandler<BlockEntityType<T>> registerBlockEntity(String name, BlockEntityTypeFactory<T> factory, Supplier<? extends Block>... blocks) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder = BLOCK_ENTITIES.register(name, () -> {
            Block[] blockArray = new Block[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                blockArray[i] = blocks[i].get();
            }
            return new BlockEntityType<>(factory::create, blockArray);
        });
        return new RegistryHandler<>() {
            @Override
            public Identifier id() { return holder.getId(); }
            @Override
            public Holder<BlockEntityType<T>> holder() { return (Holder<BlockEntityType<T>>) (Holder<?>) holder; }
            @Override
            public BlockEntityType<T> get() { return holder.get(); }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public RegistryHandler<CreativeModeTab> registerCreativeTab(String name, Supplier<ItemStack> icon, Component title, Function<CreativeModeTab.ItemDisplayParameters, ItemStack[]> func) {
        DeferredHolder<CreativeModeTab, CreativeModeTab> holder = CREATIVE_MODE_TABS.register(name, () -> CreativeModeTab.builder()
                .title(title)
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(icon)
                .displayItems((parameters, output) -> {
                    for (ItemStack stack : func.apply(parameters)) {
                        output.accept(stack);
                    }
                }).build());
        return new RegistryHandler<>() {
            @Override
            public Identifier id() { return holder.getId(); }
            @Override
            public Holder<CreativeModeTab> holder() { return (Holder<CreativeModeTab>) (Holder<?>) holder; }
            @Override
            public CreativeModeTab get() { return holder.get(); }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractContainerMenu, D> RegistryHandler<MenuType<T>> registerMenu(String name, MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> dataCodec) {
        DeferredHolder<MenuType<?>, MenuType<T>> holder = MENUS.register(name, () ->
                IMenuTypeExtension.create((syncId, inventory, buf) -> {
                    D data = dataCodec.decode(buf);
                    return factory.create(syncId, inventory, data);
                })
        );
        return new RegistryHandler<>() {
            @Override
            public Identifier id() { return holder.getId(); }
            @Override
            public Holder<MenuType<T>> holder() { return (Holder<MenuType<T>>) (Holder<?>) holder; }
            @Override
            public MenuType<T> get() { return holder.get(); }
        };
    }
}

