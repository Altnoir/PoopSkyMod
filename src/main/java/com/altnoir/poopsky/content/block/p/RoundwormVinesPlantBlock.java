package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.impl.PoDamageTypes;
import com.altnoir.poopsky.init.PoItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RoundwormVinesPlantBlock extends GrowingPlantBodyBlock implements BonemealableBlock {
    public static final MapCodec<RoundwormVinesPlantBlock> CODEC = simpleCodec(RoundwormVinesPlantBlock::new);
    public static final BooleanProperty SEEDS = BooleanProperty.create("seeds");
    public static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public RoundwormVinesPlantBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, false);
        this.registerDefaultState(this.stateDefinition.any().setValue(SEEDS, Boolean.FALSE));
    }

    @Override
    protected MapCodec<RoundwormVinesPlantBlock> codec() {
        return CODEC;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return PoBlocks.ROUNDWORM_VINES.get();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(PoItems.ROUNDWORM.get());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(SEEDS)) {
            int chance = level.getRandom().nextInt(3);
            ItemStack dropItem = switch (chance) {
                case 0 -> new ItemStack(Items.MELON_SEEDS, 1);
                case 1 -> new ItemStack(Items.PUMPKIN_SEEDS, 1);
                default -> new ItemStack(Items.FROGSPAWN, 1);
            };
            Block.popResource(level, pos, dropItem);
            float f = Mth.randomBetween(level.random, 0.8F, 1.2F);
            level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
            BlockState blockstate = state.setValue(SEEDS, Boolean.FALSE);
            level.setBlock(pos, blockstate, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof FlyEntity fly && fly.isAlive()) {
            fly.hurt(level.damageSources().source(PoDamageTypes.ROUNDWORM), 2.0F);
        } else if (entity instanceof LivingEntity livingEntity) {
            livingEntity.hurt(level.damageSources().source(PoDamageTypes.ROUNDWORM), 0.5F);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEEDS);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return !state.getValue(SEEDS);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(SEEDS, Boolean.TRUE), 2);
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.DAMAGE_OTHER;
    }
}