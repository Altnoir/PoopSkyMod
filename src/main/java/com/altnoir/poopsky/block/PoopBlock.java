package com.altnoir.poopsky.block;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PoopBlock extends Block {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 12.0, 15.0);

    public PoopBlock(AbstractBlock.Settings settings) {
        super(settings.pistonBehavior(PistonBehavior.NORMAL).slipperiness(0.8F));
    }
    private static boolean poopBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TntEntity || entity instanceof BoatEntity;
    }
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        if (entity.bypassesLandingEffects()) {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        } else {
            entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
        }
    }
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (this.isSliding(pos, entity)) {
            this.triggerAdvancement(entity, pos);
            this.updateSlidingVelocity(entity);
            this.addCollisionEffects(world, entity);
        }

        super.onEntityCollision(state, world, pos, entity);
    }
    private boolean isSliding(BlockPos pos, Entity entity) {
        if (entity.isOnGround()) {
            return false;
        } else if (entity.getY() > (double)pos.getY() + 0.9375 - 1.0E-7) {
            return false;
        } else if (entity.getVelocity().y >= -0.08) {
            return false;
        } else {
            double d = Math.abs((double)pos.getX() + 0.5 - entity.getX());
            double e = Math.abs((double)pos.getZ() + 0.5 - entity.getZ());
            double f = 0.4375 + (double)(entity.getWidth() / 2.0F);
            return d + 1.0E-7 > f || e + 1.0E-7 > f;
        }
    }
    private void triggerAdvancement(Entity entity, BlockPos pos) {
        if (entity instanceof ServerPlayerEntity && entity.getWorld().getTime() % 20L == 0L) {
            Criteria.SLIDE_DOWN_BLOCK.trigger((ServerPlayerEntity)entity, entity.getWorld().getBlockState(pos));
        }
    }
    private void updateSlidingVelocity(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < -0.13) {
            double d = -0.04 / vec3d.y;
            entity.setVelocity(new Vec3d(vec3d.x * d, -0.04, vec3d.z * d));
        } else {
            entity.setVelocity(new Vec3d(vec3d.x, -0.04, vec3d.z));
        }

        entity.onLanding();
    }
    private void addCollisionEffects(World world, Entity entity) {
        if (poopBlockEffects(entity)) {
            if (world.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }
        }
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        double d = Math.abs(entity.getVelocity().y);
        if (d < 0.1 && !entity.bypassesSteppingEffects()) {
            double e = 0.4 + d * 0.2;
            entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
        }

        super.onSteppedOn(world, pos, state, entity);
    }


    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }
    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }
}
