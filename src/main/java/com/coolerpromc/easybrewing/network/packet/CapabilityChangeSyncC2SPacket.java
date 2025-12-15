package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;

public record CapabilityChangeSyncC2SPacket(BlockPos pos, Direction direction, ItemBrewingStationBE.Slot slot) implements CustomPacketPayload {
    public static final Type<CapabilityChangeSyncC2SPacket> TYPE = new Type<>(EasyBrewing.id("capability_change_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CapabilityChangeSyncC2SPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CapabilityChangeSyncC2SPacket::pos,
            Direction.STREAM_CODEC,
            CapabilityChangeSyncC2SPacket::direction,
            ItemBrewingStationBE.Slot.STREAM_CODEC,
            CapabilityChangeSyncC2SPacket::slot,
            CapabilityChangeSyncC2SPacket::new
    );

    public static void handle(CapabilityChangeSyncC2SPacket packet, ClientPlayNetworking.Context context){
        BlockEntity blockEntity = context.player().level().getBlockEntity(packet.pos);
        if (blockEntity instanceof ItemBrewingStationBE be){
            be.setCapabilityBySide(packet.direction, packet.slot);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
