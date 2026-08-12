package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.ChiliVines;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class ChiliVinesPlantBlock extends GrowingPlantBodyBlock implements BonemealableBlock, ChiliVines {
    public static final MapCodec<ChiliVinesPlantBlock> CODEC = simpleCodec(ChiliVinesPlantBlock::new);

    @Override
    protected MapCodec<ChiliVinesPlantBlock> codec() {
        return CODEC;
    }

    public ChiliVinesPlantBlock(Properties properties) {
        super(properties, Direction.DOWN, SHAPE, false);
        this.registerDefaultState(this.stateDefinition.any().setValue(CHILI, Boolean.FALSE));
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return PoBlocks.CHILI_VINES.get();
    }

    @Override
    protected BlockState updateHeadAfterConvertedFromBody(BlockState head, BlockState body) {
        return body.setValue(CHILI, head.getValue(CHILI));
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(PoItems.DRAGON_BREATH_CHILI.get());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return ChiliVines.use(player, state, level, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHILI);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return !state.getValue(CHILI);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(CHILI, Boolean.TRUE), 2);
    }
}
