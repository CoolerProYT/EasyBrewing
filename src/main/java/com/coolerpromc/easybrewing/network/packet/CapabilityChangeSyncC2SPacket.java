package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CapabilityChangeSyncC2SPacket(BlockPos pos, ItemBrewingStationBE.RelativeSide direction, ItemBrewingStationBE.Slot slot) {
    public static CapabilityChangeSyncC2SPacket decode(FriendlyByteBuf buf){
        return new CapabilityChangeSyncC2SPacket(buf.readBlockPos(), buf.readEnum(ItemBrewingStationBE.RelativeSide.class), buf.readEnum(ItemBrewingStationBE.Slot.class));
    }

    public static void encode(CapabilityChangeSyncC2SPacket packet, FriendlyByteBuf buf){
        buf.writeBlockPos(packet.pos());
        buf.writeEnum(packet.direction());
        buf.writeEnum(packet.slot());
    }

    public static void handle(CapabilityChangeSyncC2SPacket packet, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            BlockEntity blockEntity = context.get().getSender().level().getBlockEntity(packet.pos);
            if (blockEntity instanceof ItemBrewingStationBE be){
                be.setCapabilityBySide(packet.direction, packet.slot);
            }
        }).exceptionally(e -> {
            EasyBrewing.LOGGER.error(e.getMessage());
            return null;
        });
    }
}
