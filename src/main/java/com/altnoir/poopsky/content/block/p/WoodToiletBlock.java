package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.ToiletTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class WoodToiletBlock extends AbstractToiletBlock {
    public static final MapCodec<WoodToiletBlock> CODEC = simpleCodec(WoodToiletBlock::new);

    public WoodToiletBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public ToiletType getDefaultToiletType() {
        return ToiletTypes.OAK;
    }

    @Override
    protected boolean canReplaceVariant(BlockState state, ToiletType type) {
        return type.category() == ToiletType.Category.WOOD;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return MapColor.WOOD;
    }
}
