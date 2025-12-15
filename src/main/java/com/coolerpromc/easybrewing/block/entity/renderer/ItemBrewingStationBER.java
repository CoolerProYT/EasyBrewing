package com.coolerpromc.easybrewing.block.entity.renderer;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public record ItemBrewingStationBER(BlockEntityRendererFactory.Context context) implements BlockEntityRenderer<ItemBrewingStationBE> {
    @Override
    public void render(ItemBrewingStationBE be, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        Direction facing = be.getCachedState().get(Properties.HORIZONTAL_FACING);
        ItemStack output = be.outputHandler.getStack(0);
        ItemStack potion = be.potionHandler.getStack(0);
        ItemStack input = be.inputHandler.getStack(0);

        float yRotation = switch (facing) {
            case NORTH -> -0f;
            case SOUTH -> 180f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default   -> 0f;
        };

        poseStack.push();

        poseStack.translate(0.5, 0, 0.5);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRotation));
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

        poseStack.pop();
    }

    private void renderItemStack(ItemStack stack, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay, double x, double z, float rotation){
        poseStack.push();
        poseStack.scale(0.25f, 0.25f, 0.25f);
        poseStack.translate(x, 1, z);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        context.getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, packedLight, packedOverlay, poseStack, bufferSource, null, 1);
        poseStack.pop();
    }
}
