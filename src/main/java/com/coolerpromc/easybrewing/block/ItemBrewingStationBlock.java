package com.coolerpromc.easybrewing.block;

import com.coolerpromc.easybrewing.EasyBrewing;
import com.coolerpromc.easybrewing.block.entity.ItemBrewingStationBE;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class ItemBrewingStationBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE_NORTH = Stream.of(
            Block.createCuboidShape(7.5, 0, 7, 8.5, 16, 8),
            Block.createCuboidShape(4.5, 0, 0, 11.5, 16, 7),
            Block.createCuboidShape(0, 0, 7, 16, 16, 15.5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        SHAPES.put(Direction.NORTH, SHAPE_NORTH);
        SHAPES.put(Direction.EAST, rotateShape(Direction.NORTH, Direction.EAST, SHAPE_NORTH));
        SHAPES.put(Direction.SOUTH, rotateShape(Direction.NORTH, Direction.SOUTH, SHAPE_NORTH));
        SHAPES.put(Direction.WEST, rotateShape(Direction.NORTH, Direction.WEST, SHAPE_NORTH));
    }

    public ItemBrewingStationBlock(Settings properties) {
        super(properties);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(ItemBrewingStationBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ItemBrewingStationBE(blockPos, blockState);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (player instanceof ServerPlayerEntity serverPlayer){
            if (level.getBlockEntity(pos) instanceof NamedScreenHandlerFactory be){
                serverPlayer.openHandledScreen(be);
            }
        }
        return ItemActionResult.success(level.isClient());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> blockEntityType) {
        return validateTicker(blockEntityType, EasyBrewing.ITEM_BREWING_STATION_BE, (level1, blockPos, blockState, be) -> be.tick(level1, blockPos, blockState));
    }

    @Override
    public void randomDisplayTick(BlockState state, World level, BlockPos pos, Random random) {
        double d0 = (double)pos.getX() + 0.4 + (double)random.nextFloat() * 0.2;
        double d1 = (double)pos.getY() + 0.7 + (double)random.nextFloat() * 0.3;
        double d2 = (double)pos.getZ() + 0.4 + (double)random.nextFloat() * 0.2;
        level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()){
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ItemBrewingStationBE be){
                be.drops();
            }
        }
        super.onStateReplaced(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView level, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = VoxelShapes.union(buffer[1],
                        VoxelShapes.cuboid(
                                1 - maxZ, minY, minX,
                                1 - minZ, maxY, maxX
                        ));
            });
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }
}
