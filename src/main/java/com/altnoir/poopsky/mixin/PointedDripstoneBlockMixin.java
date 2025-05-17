package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.enums.Thickness;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
    @Inject(
            method = "dripTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;isEmpty()Z",
                    ordinal = 0
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void injectPoopDrying(BlockState state, ServerWorld world, BlockPos pos, float dripChance, CallbackInfo info, Optional optional) {
        if (optional.isPresent()) {
            try {
                // 反射DrippingFluid（byd麻将）
                Object fluidData = optional.get();
                Class<?> fluidDataClass = Class.forName("net.minecraft.block.PointedDripstoneBlock$DrippingFluid");
                Field posField = fluidDataClass.getDeclaredField("pos");
                Field fluidField = fluidDataClass.getDeclaredField("fluid");
                Field sourceStateField = fluidDataClass.getDeclaredField("sourceState");

                posField.setAccessible(true);
                fluidField.setAccessible(true);
                sourceStateField.setAccessible(true);

                BlockPos fluidPos = ((BlockPos) posField.get(fluidData));
                BlockState sourceState = (BlockState) sourceStateField.get(fluidData);

                if (isHeldByPointedDripstone(state, world, pos) && sourceState.isOf(PSBlocks.POOP_BLOCK)) {
                    BlockPos tipPos = getTipPos(state, world, pos, 11, false);

                    if (tipPos != null) {
                        BlockState dirtState = Blocks.DIRT.getDefaultState();
                        world.setBlockState(fluidPos, dirtState);
                        world.updateNeighbor(fluidPos, dirtState.getBlock(), pos);
                        Block.pushEntitiesUpBeforeBlockChange(sourceState, dirtState, world, fluidPos);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, fluidPos, GameEvent.Emitter.of(dirtState));
                        world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS, tipPos, 0);
                        info.cancel();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to handle dripstone reflection", e);
            }
        }
    }

    //麻将不用public，我也没啥好办法了
    @Unique
    @Nullable
    private static BlockPos getTipPos(BlockState state, WorldAccess world, BlockPos pos, int range, boolean allowMerged) {
        if (isTip(state, allowMerged)) {
            return pos;
        } else {
            Direction direction = state.get(PointedDripstoneBlock.VERTICAL_DIRECTION);
            BiPredicate<BlockPos, BlockState> biPredicate = (posx, statex) -> statex.isOf(Blocks.POINTED_DRIPSTONE) && statex.get(PointedDripstoneBlock.VERTICAL_DIRECTION) == direction;
            return (BlockPos)searchInDirection(world, pos, direction.getDirection(), biPredicate, statex -> isTip(statex, allowMerged), range).orElse(null);
        }
    }
    @Unique
    private static boolean isTip(BlockState state, boolean allowMerged) {
        if (!state.isOf(Blocks.POINTED_DRIPSTONE)) {
            return false;
        } else {
            Thickness thickness = state.get(PointedDripstoneBlock.THICKNESS);
            return thickness == Thickness.TIP || allowMerged && thickness == Thickness.TIP_MERGE;
        }
    }
    @Unique
    private static boolean isHeldByPointedDripstone(BlockState state, WorldView world, BlockPos pos) {
        return isPointingDown(state) && !world.getBlockState(pos.up()).isOf(Blocks.POINTED_DRIPSTONE);
    }
    @Unique
    private static boolean isPointingDown(BlockState state) {
        return isPointedDripstoneFacingDirection(state, Direction.DOWN);
    }
    @Unique
    private static boolean isPointedDripstoneFacingDirection(BlockState state, Direction direction) {
        return state.isOf(Blocks.POINTED_DRIPSTONE) && state.get(PointedDripstoneBlock.VERTICAL_DIRECTION) == direction;
    }
    @Unique
    private static Optional<BlockPos> searchInDirection(
            WorldAccess world,
            BlockPos pos,
            Direction.AxisDirection direction,
            BiPredicate<BlockPos, BlockState> continuePredicate,
            Predicate<BlockState> stopPredicate,
            int range
    ) {
        Direction direction2 = Direction.get(direction, Direction.Axis.Y);
        BlockPos.Mutable mutable = pos.mutableCopy();

        for (int i = 1; i < range; i++) {
            mutable.move(direction2);
            BlockState blockState = world.getBlockState(mutable);
            if (stopPredicate.test(blockState)) {
                return Optional.of(mutable.toImmutable());
            }

            if (world.isOutOfHeightLimit(mutable.getY()) || !continuePredicate.test(mutable, blockState)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}