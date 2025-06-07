package com.altnoir.poopsky.block;

import com.altnoir.poopsky.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.effect.PSEffects;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticles;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractToiletBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public AbstractToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide) {
            if (fallDistance >= 1.0F && entity instanceof LivingEntity living) {
                var factor = Math.max(fallDistance / 10.0F, 0.0F);
                var damageMultiplier = 1.0F / (1.0F + factor);
                living.causeFallDamage(fallDistance, damageMultiplier, level.damageSources().fall());
            }
            if (entity instanceof FallingBlockEntity falling &&
                            falling.getBlockState().is(Blocks.ANVIL) |
                            falling.getBlockState().is(Blocks.CHIPPED_ANVIL) |
                            falling.getBlockState().is(Blocks.DAMAGED_ANVIL)) {
                poopAnvil(level, entity);
            }
        }
    }

    private void poopAnvil(Level level, Entity entity) {
        var poop = new ItemEntity(
                level,
                entity.getX(), entity.getY() + 0.1, entity.getZ(),
                new ItemStack(PSItems.POOP.get(), 8)
        );
        poop.setDefaultPickUpDelay();
        level.addFreshEntity(poop);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (player.isShiftKeyDown() && isPlayerCentered(pos, player)) {
                if (player.hasEffect(PSEffects.FECAL_INCONTINENCE)) {
                    onPoop(level, player);
                    player.causeFoodExhaustion(0.05F);
                } else if (level.getGameTime() % 20 == 0) {
                    onPoop(level, player);
                    player.causeFoodExhaustion(1.0F);
                }
            }
        }
    }

    protected boolean isPlayerCentered(BlockPos blockPos, Player player) {
        var blockAABB = new AABB(blockPos).inflate(0.2);
        return blockAABB.contains(player.position());
    }

    protected void onPoop(Level level, Player player) {
        var poop = new ItemEntity(
                level,
                player.getX(), player.getY() + 0.1, player.getZ(),
                new ItemStack(PSItems.POOP.get())
        );
        poop.setDefaultPickUpDelay();
        float pitch = level.random.nextFloat() + 0.5F;
        level.playSound(null, player.getX(), player.getY() + 0.1, player.getZ(), PSSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PSParticles.POOP_PARTICLE.get(),
                player.getX(),
                player.getY() + 0.1,
                player.getZ(),
                8,
                0.0,
                -0.1,
                0.0,
                3.0
        );
        level.addFreshEntity(poop);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToiletBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (hasHot(level, pos)) {
            level.explode(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 4.0F, Level.ExplosionInteraction.BLOCK);
            level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
            var poop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, new ItemStack(PSItems.POOP.get(), 88));
            level.addFreshEntity(poop);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (!level.isClientSide && hasHot((ServerLevel) level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        if (!level.isClientSide && neighborPos.equals(pos.above()) && hasHot((ServerLevel) level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    private boolean hasHot(ServerLevel level, BlockPos pos) {
        var above = pos.above();
        if (!level.isInWorldBounds(above)) return false;
        return level.getBlockState(above).is(Blocks.FIRE);
    }
}
