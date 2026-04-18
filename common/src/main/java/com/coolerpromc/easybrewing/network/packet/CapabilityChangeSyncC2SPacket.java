package com.coolerpromc.easybrewing.network.packet;

import com.coolerpromc.easybrewing.Constants;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public record CapabilityChangeSyncC2SPacket(BlockPos pos, ItemBrewingStationBE.RelativeSide direction, ItemBrewingStationBE.Slot slot) implements CustomPacketPayload {
    public static final Type<CapabilityChangeSyncC2SPacket> TYPE = new Type<>(Constants.id("capability_change_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CapabilityChangeSyncC2SPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CapabilityChangeSyncC2SPacket::pos,
            ItemBrewingStationBE.RelativeSide.STREAM_CODEC,
            CapabilityChangeSyncC2SPacket::direction,
            ItemBrewingStationBE.Slot.STREAM_CODEC,
            CapabilityChangeSyncC2SPacket::slot,
            CapabilityChangeSyncC2SPacket::new
    );

    public static void handleOnServer(CapabilityChangeSyncC2SPacket packet, ServerPlayer player) {
        BlockEntity blockEntity = player.level().getBlockEntity(packet.pos);
        if (blockEntity instanceof ItemBrewingStationBE be) {
            be.setCapabilityBySide(packet.direction, packet.slot);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
