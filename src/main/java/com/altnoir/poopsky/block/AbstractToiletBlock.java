package com.altnoir.poopsky.block;

import com.altnoir.poopsky.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.block.p.ToiletLavaBlock;
import com.altnoir.poopsky.effect.PSEffects;
import com.altnoir.poopsky.entity.PSEntityType;
import com.altnoir.poopsky.entity.p.ToiletEntity;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticles;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public abstract class AbstractToiletBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ToiletState> CONNECTION = EnumProperty.create("connection", ToiletState.class);

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public enum ToiletState implements StringRepresentable {
        DEFAULT("default"),
        FRONT("front"),
        BACK("back"),
        BOTH("both");

        private final String name;

        ToiletState(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

    public AbstractToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(CONNECTION, ToiletState.DEFAULT)
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            Entity entity;
            List<ToiletEntity> entities = level.getEntities(PSEntityType.TOILET.get(), new AABB(pos), toiletEntity -> true);

            if (entities.isEmpty()) {
                entity = PSEntityType.TOILET.get().spawn((ServerLevel) level, pos, MobSpawnType.TRIGGERED);
            } else {
                entity = entities.getFirst();
            }
            player.startRiding(entity);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ToiletBlockEntity toilet) {
                toilet.clearLinkedBlock();
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
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

            if (fallDistance >= 1.0f && isEntityCentered(pos, entity)) {
                var be = (ToiletBlockEntity) level.getBlockEntity(pos);
                if (be == null) return;
                teleportEntity(level, entity, be, fallDistance);
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
            if (player.isShiftKeyDown() && isEntityCentered(pos, player)) {
                if (player.hasEffect(PSEffects.FECAL_INCONTINENCE)) {
                    onPoop(level, player, player.hasEffect(PSEffects.INTESTINAL_SPASM));
                    player.causeFoodExhaustion(0.05F);
                } else {
                    var playerData = player.getPersistentData();
                    long poopTime = playerData.getLong("poopTime");
                    long gameTime = level.getGameTime();

                    if (poopTime == 0 || gameTime - poopTime >= 20) {
                        onPoop(level, player, player.hasEffect(PSEffects.INTESTINAL_SPASM));
                        player.causeFoodExhaustion(1.0F);
                        playerData.putLong("poopTime", gameTime);
                    }
                }
            }
        }
    }

    protected boolean isEntityCentered(BlockPos blockPos, Entity entity) {
        var blockAABB = new AABB(blockPos).inflate(0.2);
        return blockAABB.contains(entity.position());
    }

    protected void onPoop(Level level, Player player, boolean isFire) {
        if (player.getFoodData().getFoodLevel() <= 0) {
            player.hurt(level.damageSources().wither(), 1.0F);

            var redStone = new ItemEntity(
                    level,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    new ItemStack(Items.REDSTONE)
            );
            redStone.setDefaultPickUpDelay();
            level.addFreshEntity(redStone);
        } else {
            var poop = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), new ItemStack(PSItems.POOP.get()));
            var chili_poop = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), new ItemStack(PSItems.CHILI_POOP.get()));

            poop.setDefaultPickUpDelay();
            chili_poop.setDefaultPickUpDelay();

            level.addFreshEntity(isFire ? chili_poop : poop);
        }
        var pitch = level.random.nextFloat() + 0.5F;
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
    }

    public void teleportEntity(Level level, Entity entity, ToiletBlockEntity blockEntity, float fallDistance) {
        var server = level.getServer();
        var linkedDim = blockEntity.getLinkedDim();
        var targetPos = blockEntity.getLinkedPos();
        if (linkedDim == null || linkedDim.isBlank() || targetPos == null) return;

        var targetDimension = ResourceLocation.tryParse(linkedDim);
        if (targetDimension == null) return;

        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;
        targetWorld.getChunk(targetPos);

        if (entity.isVehicle() && !entity.getPassengers().isEmpty()) {
            entity.getControllingPassenger().teleportTo(targetWorld, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
            entity.teleportTo(targetWorld, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
        } else {
            entity.teleportTo(targetWorld, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
        }

        var pitch = targetWorld.random.nextFloat() + 0.1F;
        targetWorld.playSound(null, entity.getX(), entity.getY() + 0.1, entity.getZ(), SoundEvents.MUD_BREAK, SoundSource.PLAYERS, 1.0F, pitch);
        var bounce = Math.sqrt(2 * 0.08 * fallDistance) * 0.85;
        server.tell(new TickTask(server.getTickCount() + 1, () -> {
            entity.setDeltaMovement(entity.getDeltaMovement().x, bounce, entity.getDeltaMovement().z);
            entity.hurtMarked = true;
            entity.hasImpulse = true;
        }));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();
        Direction facing = ctx.getHorizontalDirection().getOpposite();

        var forwardPos = pos.relative(facing);
        var forwardConnected = isValidNeighbor(level, forwardPos, facing);

        var backwardPos = pos.relative(facing.getOpposite());
        var backwardConnected = isValidNeighbor(level, backwardPos, facing);

        ToiletState connection;
        if (forwardConnected && backwardConnected) {
            connection = ToiletState.BOTH;
        } else if (forwardConnected) {
            connection = ToiletState.FRONT;
        } else if (backwardConnected) {
            connection = ToiletState.BACK;
        } else {
            connection = ToiletState.DEFAULT;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(CONNECTION, connection);
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
    public @NotNull RenderShape getRenderShape(BlockState state) {
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
        if (!level.isClientSide) {
            level.neighborChanged(pos, this, pos);
            if (hasHot((ServerLevel) level, pos)) {
                level.scheduleTick(pos, this, 1);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        if (!level.isClientSide && neighborPos.equals(pos.above()) && hasHot((ServerLevel) level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        var facing = state.getValue(FACING);
        var forwardPos = pos.relative(facing);
        var backwardPos = pos.relative(facing.getOpposite());

        var forwardConnected = isValidNeighbor(level, forwardPos, facing);
        var backwardConnected = isValidNeighbor(level, backwardPos, facing);
        ToiletState connection;
        if (forwardConnected && backwardConnected) {
            connection = ToiletState.BOTH;
        } else if (forwardConnected) {
            connection = ToiletState.FRONT;
        } else if (backwardConnected) {
            connection = ToiletState.BACK;
        } else {
            connection = ToiletState.DEFAULT;
        }
        if (connection != state.getValue(CONNECTION)) {
            level.setBlockAndUpdate(pos, state.setValue(CONNECTION, connection));
        }
    }

    protected boolean hasHot(ServerLevel level, BlockPos pos) {
        var above = pos.above();
        if (!level.isInWorldBounds(above)) return false;
        return level.getBlockState(above).is(Blocks.FIRE);
    }

    protected boolean isValidNeighbor(LevelReader level, BlockPos pos, Direction facing) {
        var neighbor = level.getBlockState(pos);

        if (neighbor.getBlock() instanceof AbstractToiletBlock) {
            if (!(neighbor.getBlock() instanceof ToiletLavaBlock)) {
                return neighbor.getValue(FACING) == facing || neighbor.getValue(FACING) == facing.getOpposite();
            } else if (this instanceof ToiletLavaBlock) {
                return neighbor.getValue(ToiletLavaBlock.LAVA) == this.defaultBlockState().getValue(ToiletLavaBlock.LAVA) &&
                        (neighbor.getValue(FACING) == facing || neighbor.getValue(FACING) == facing.getOpposite());
            }
        }
        return false;
    }
}
