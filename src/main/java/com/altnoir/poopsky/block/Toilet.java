package com.altnoir.poopsky.block;

import com.altnoir.poopsky.effect.PSEffect;
import com.altnoir.poopsky.entity.ToiletBlockEntity;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticle;
import com.altnoir.poopsky.sound.PSSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.block.HorizontalFacingBlock.FACING;

public class Toilet extends BlockWithEntity implements Portal{
    private static final VoxelShape SHAPE = VoxelShapes.union(createCuboidShape(0, 0, 0, 16, 16, 16));
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(Toilet::new);
    }

    public Toilet(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
        );
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(Properties.HORIZONTAL_FACING, facing);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.canUsePortals(false) && !world.isClient) {
            if (fallDistance >= 1.0F) {
                entity.tryUsePortal(this, pos);
            }
            if (entity instanceof LivingEntity livingEntity) {
                float fd = Math.max(fallDistance / 10, 0.0F);
                float damageMultiplier = 1.0F / (1.0F + fd);

                livingEntity.handleFallDamage(fallDistance, damageMultiplier, world.getDamageSources().fall());
            }
        }
    }
    @Nullable
    @Override
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ToiletBlockEntity toilet) {
            BlockPos targetPos = toilet.getLinkedPos();
            String targetDim = toilet.getLinkedDim();

            if (targetPos == null || targetDim == null) return null;

            ServerWorld targetWorld = world.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier.tryParse(targetDim)));
            world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);

            Vec3d vec3d = Vec3d.ofBottomCenter(targetPos).add(0, 1, 0);
            Vec3d velocity = new Vec3d(entity.getVelocity().x, 0.45, entity.getVelocity().z);

            if (entity instanceof ServerPlayerEntity player) {
                player.setVelocity(velocity);
                player.velocityModified = true;
            }

            return new TeleportTarget(targetWorld, vec3d, velocity, entity.getYaw(), entity.getPitch(), TeleportTarget.ADD_PORTAL_CHUNK_TICKET);

        }
        return null;
    }


    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            if (player.isSneaking() && isPlayerCentered(pos, player)) {
                if (player.hasStatusEffect(PSEffect.FECAL_INCONTINENCE)) {
                    onPoop(world,player);
                    player.addExhaustion(0.05F);
                }else if (world.getTime() % 20 == 0) {
                    onPoop(world,entity);
                    player.addExhaustion(1.0F);
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }
    private boolean isPlayerCentered(BlockPos blockPos, PlayerEntity player) {
        Box blockBox = new Box(blockPos).expand(0.2);
        return blockBox.contains(player.getPos());
    }
    private void onPoop(World world, Entity entity) {
        ItemEntity poop = new ItemEntity(
                world,
                entity.getX(),
                entity.getY() + 0.1,
                entity.getZ(),
                new ItemStack(PSItems.POOP)
        );
        poop.setPickupDelay(20);

        float pitch = world.random.nextFloat() + 0.5F;
        world.playSound(null, entity.getX(), entity.getY() + 0.1, entity.getZ(), PSSounds.FART, entity.getSoundCategory(), 1.0F, pitch);

        ((ServerWorld) world).spawnParticles(PSParticle.POOP_PARTICLE,
                entity.getX(), entity.getY() + 0.1, entity.getZ(),
                8, 0.0, -0.1, 0.0, 3.0);
        world.spawnEntity(poop);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
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
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return true;
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ToiletBlockEntity(pos, state);
    }
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
