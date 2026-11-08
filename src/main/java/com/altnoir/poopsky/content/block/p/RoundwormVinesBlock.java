package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.impl.type.damageType.PoDamageTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RoundwormVinesBlock extends GrowingPlantHeadBlock {
    public static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

    public RoundwormVinesBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, false, 0.1);
    }


    @Override
    protected Block getBodyBlock() {
        return PoBlocks.ROUNDWORM_VINES_PLANT.get();
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, net.minecraft.world.level.BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(PoItems.ROUNDWORM.get());
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof FlyEntity fly && fly.isAlive()) {
            fly.hurt(PoDamageTypes.source(level, PoDamageTypes.ROUNDWORM), 2.0F);
        } else if (entity instanceof LivingEntity livingEntity) {
            livingEntity.hurt(PoDamageTypes.source(level, PoDamageTypes.ROUNDWORM), 0.5F);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return BlockPathTypes.DAMAGE_OTHER;
    }
}