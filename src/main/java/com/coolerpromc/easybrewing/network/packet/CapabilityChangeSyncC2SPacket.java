package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record CapabilityChangeSyncC2SPacket(BlockPos pos, Direction direction, ItemBrewingStationBE.Slot slot) {
    public static final Identifier TYPE = EasyBrewing.id("capability_change_sync");

    public static PacketByteBuf encode(PacketByteBuf buf, CapabilityChangeSyncC2SPacket packet){
        buf.writeBlockPos(packet.pos());
        buf.writeEnumConstant(packet.direction());
        buf.writeEnumConstant(packet.slot());
        return buf;
    }

    public static CapabilityChangeSyncC2SPacket decode(PacketByteBuf buf){
        return new CapabilityChangeSyncC2SPacket(buf.readBlockPos(), buf.readEnumConstant(Direction.class), buf.readEnumConstant(ItemBrewingStationBE.Slot.class));
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender){
        CapabilityChangeSyncC2SPacket packet = decode(buf);
        BlockEntity blockEntity = playerEntity.getServerWorld().getBlockEntity(packet.pos);
        if (blockEntity instanceof ItemBrewingStationBE be){
            be.setCapabilityBySide(packet.direction, packet.slot);
        }
    }
}
