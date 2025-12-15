package com.coolerpromc.easybrewing.block.entity.renderer;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public record ItemBrewingStationBER(BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<ItemBrewingStationBE> {
    @Override
    public void render(ItemBrewingStationBE be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        ItemStack output = be.outputHandler.getStackInSlot(0);
        ItemStack potion = be.potionHandler.getStackInSlot(0);
        ItemStack input = be.inputHandler.getStackInSlot(0);

        float yRotation = switch (facing) {
            case NORTH -> -0f;
            case SOUTH -> 180f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default   -> 0f;
        };

        poseStack.pushPose();

        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
        poseStack.translate(-0.5, 0, -0.5);

        renderItemStack(output, poseStack, bufferSource, packedLight, packedOverlay, 1.4, 0.3, -45f);
        renderItemStack(output, poseStack, bufferSource, packedLight, packedOverlay, 2.6, 0.3, 225);
        renderItemStack(output, poseStack, bufferSource, packedLight, packedOverlay, 2, 1.5, 90f);

        renderItemStack(potion, poseStack, bufferSource, packedLight, packedOverlay, 0.5, 2.625,  0f);
        renderItemStack(potion, poseStack, bufferSource, packedLight, packedOverlay, 1.25, 3.4,  90f);
        renderItemStack(potion, poseStack, bufferSource, packedLight, packedOverlay, 1.75, 2.1,  45f);

        renderItemStack(input, poseStack, bufferSource, packedLight, packedOverlay, 2.15, 2.05,  -45f);
        renderItemStack(input, poseStack, bufferSource, packedLight, packedOverlay, 3.65, 2.625,  0);
        renderItemStack(input, poseStack, bufferSource, packedLight, packedOverlay, 2.775, 3.5,  90);

        poseStack.popPose();
    }

    private void renderItemStack(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, double x, double z, float rotation){
        poseStack.pushPose();
        poseStack.scale(0.25f, 0.25f, 0.25f);
        poseStack.translate(x, 1, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        context.getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, null, 1);
        poseStack.popPose();
    }
}
