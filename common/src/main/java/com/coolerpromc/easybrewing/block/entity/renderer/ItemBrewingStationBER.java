package com.coolerpromc.easybrewing.block.entity.renderer;

import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.coolerpromc.easybrewing.block.entity.renderer.state.ItemBrewingStationRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ItemBrewingStationBER implements BlockEntityRenderer<ItemBrewingStationBE, ItemBrewingStationRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ItemBrewingStationBER(BlockEntityRendererProvider.Context context){
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public ItemBrewingStationRenderState createRenderState() {
        return new ItemBrewingStationRenderState();
    }

    @Override
    public void extractRenderState(ItemBrewingStationBE blockEntity, ItemBrewingStationRenderState renderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        List<ItemStack> itemStacks = List.of(
                blockEntity.outputHandler.getItem(0),
                blockEntity.potionHandler.getItem(0),
                blockEntity.inputHandler.getItem(0)
        );

        List<ItemStackRenderState> itemStackRenderStates = List.of(new ItemStackRenderState(), new ItemStackRenderState(), new ItemStackRenderState());
        
        for (int i = 0; i < itemStacks.size(); i++){
            this.itemModelResolver.updateForTopItem(itemStackRenderStates.get(i), itemStacks.get(i), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 1);
        }
        
        renderState.itemStackRenderStates = itemStackRenderStates;
    }

    @Override
    public void submit(ItemBrewingStationRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        Direction facing = renderState.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        
        ItemStackRenderState output = renderState.itemStackRenderStates.get(0);
        ItemStackRenderState potion = renderState.itemStackRenderStates.get(1);
        ItemStackRenderState input = renderState.itemStackRenderStates.get(2);
        
        int packedLight = renderState.lightCoords;

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

        renderItemStack(output, poseStack, nodeCollector, packedLight, 1.4, 0.3, -45f);
        renderItemStack(output, poseStack, nodeCollector, packedLight, 2.6, 0.3, 225);
        renderItemStack(output, poseStack, nodeCollector, packedLight, 2, 1.5, 90f);

        renderItemStack(potion, poseStack, nodeCollector, packedLight, 0.5, 2.625,  0f);
        renderItemStack(potion, poseStack, nodeCollector, packedLight, 1.25, 3.4,  90f);
        renderItemStack(potion, poseStack, nodeCollector, packedLight, 1.75, 2.1,  45f);

        renderItemStack(input, poseStack, nodeCollector, packedLight, 2.15, 2.05,  -45f);
        renderItemStack(input, poseStack, nodeCollector, packedLight, 3.65, 2.625,  0);
        renderItemStack(input, poseStack, nodeCollector, packedLight, 2.775, 3.5,  90);

        poseStack.popPose();
    }

    private void renderItemStack(ItemStackRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, double x, double z, float rotation){
        poseStack.pushPose();
        poseStack.scale(0.25f, 0.25f, 0.25f);
        poseStack.translate(x, 1, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        renderState.submit(poseStack, nodeCollector, packedLight, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

}
