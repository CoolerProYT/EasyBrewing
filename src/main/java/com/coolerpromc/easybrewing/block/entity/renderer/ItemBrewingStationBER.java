package com.coolerpromc.easybrewing.block.entity.renderer;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.block.entity.renderer.state.ItemBrewingStationRenderState;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ItemBrewingStationBER(ItemModelManager itemModelManager) implements BlockEntityRenderer<ItemBrewingStationBE, ItemBrewingStationRenderState> {
    public ItemBrewingStationBER(BlockEntityRendererFactory.Context itemModelManager) {
        this(itemModelManager.itemModelManager());
    }

    @Override
    public ItemBrewingStationRenderState createRenderState() {
        return new ItemBrewingStationRenderState();
    }

    @Override
    public void updateRenderState(ItemBrewingStationBE blockEntity, ItemBrewingStationRenderState renderState, float tickProgress, Vec3d cameraPos, ModelCommandRenderer.@Nullable CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, renderState, tickProgress, cameraPos, crumblingOverlay);

        List<ItemStack> itemStacks = List.of(
                blockEntity.outputHandler.getStack(0),
                blockEntity.potionHandler.getStack(0),
                blockEntity.inputHandler.getStack(0)
        );

        List<ItemRenderState> itemStackRenderStates = List.of(new ItemRenderState(), new ItemRenderState(), new ItemRenderState());

        for (int i = 0; i < itemStacks.size(); i++) {
            this.itemModelManager.update(itemStackRenderStates.get(i), itemStacks.get(i), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 1);
        }

        renderState.itemRenderStates = itemStackRenderStates;
    }

    @Override
    public void render(ItemBrewingStationRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        Direction facing = renderState.blockState.get(Properties.HORIZONTAL_FACING);

        ItemRenderState output = renderState.itemRenderStates.get(0);
        ItemRenderState potion = renderState.itemRenderStates.get(1);
        ItemRenderState input = renderState.itemRenderStates.get(2);

        int packedLight = renderState.lightmapCoordinates;

        float yRotation = switch (facing) {
            case NORTH -> -0f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> -90f;
            default -> 0f;
        };

        matrices.push();

        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRotation));
        matrices.translate(-0.5, 0, -0.5);

        renderItemStack(output, matrices, queue, packedLight, 1.4, 0.3, -45f);
        renderItemStack(output, matrices, queue, packedLight, 2.6, 0.3, 225);
        renderItemStack(output, matrices, queue, packedLight, 2, 1.5, 90f);

        renderItemStack(potion, matrices, queue, packedLight, 0.5, 2.625, 0f);
        renderItemStack(potion, matrices, queue, packedLight, 1.25, 3.4, 90f);
        renderItemStack(potion, matrices, queue, packedLight, 1.75, 2.1, 45f);

        renderItemStack(input, matrices, queue, packedLight, 2.15, 2.05, -45f);
        renderItemStack(input, matrices, queue, packedLight, 3.65, 2.625, 0);
        renderItemStack(input, matrices, queue, packedLight, 2.775, 3.5, 90);

        matrices.pop();
    }

    private void renderItemStack(ItemRenderState renderState, MatrixStack poseStack, OrderedRenderCommandQueue queue, int packedLight, double x, double z, float rotation) {
        poseStack.push();
        poseStack.scale(0.25f, 0.25f, 0.25f);
        poseStack.translate(x, 1, z);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        renderState.render(poseStack, queue, packedLight, OverlayTexture.DEFAULT_UV, 0);
        poseStack.pop();
    }
}
