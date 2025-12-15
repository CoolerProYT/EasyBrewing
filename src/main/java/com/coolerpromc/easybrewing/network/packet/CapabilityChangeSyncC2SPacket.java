package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record CapabilityChangeSyncC2SPacket(BlockPos pos, Direction direction, ItemBrewingStationBE.Slot slot) implements CustomPayload {
    public static final Id<CapabilityChangeSyncC2SPacket> TYPE = new Id<>(EasyBrewing.id("capability_change_sync"));

    public static final PacketCodec<RegistryByteBuf, CapabilityChangeSyncC2SPacket> STREAM_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC,
            CapabilityChangeSyncC2SPacket::pos,
            Direction.PACKET_CODEC,
            CapabilityChangeSyncC2SPacket::direction,
            ItemBrewingStationBE.Slot.STREAM_CODEC,
            CapabilityChangeSyncC2SPacket::slot,
            CapabilityChangeSyncC2SPacket::new
    );

    public static void handle(CapabilityChangeSyncC2SPacket packet, ServerPlayNetworking.Context context){
        BlockEntity blockEntity = context.player().getWorld().getBlockEntity(packet.pos);
        if (blockEntity instanceof ItemBrewingStationBE be){
            be.setCapabilityBySide(packet.direction, packet.slot);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
