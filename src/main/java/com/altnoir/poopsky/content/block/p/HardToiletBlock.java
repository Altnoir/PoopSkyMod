package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.ToiletTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class HardToiletBlock extends BaseToiletLavaBlock {
    public static final MapCodec<HardToiletBlock> CODEC = simpleCodec(HardToiletBlock::new);

    public enum ToiletMode implements StringRepresentable {
        DEFAULT("default"),
        REDSTONE("redstone");

        private final String name;

        ToiletMode(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<ToiletMode> TOILET_MODE = EnumProperty.create("toilet_mode", ToiletMode.class);

    public HardToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TOILET_MODE, ToiletMode.DEFAULT));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public ToiletType getDefaultToiletType() {
        return ToiletTypes.WHITE_TILE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOILET_MODE);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        boolean isGolden = ToiletUtil.isGoldenToilet(level, pos);
        ToiletUtil.lavaToiletStepOn(level, pos, state, entity, isGolden);
    }

    @Override
    protected boolean canReplaceVariant(BlockState state, ToiletType type) {
        return !isLavaFilled(state) && type.category() == ToiletType.Category.HARD;
    }

    @Override
    public BlockState applyToiletType(BlockState state, ToiletType toiletType) {
        if (toiletType.category() == ToiletType.Category.HARD) {
            ToiletMode mode = toiletType.isRedstone() ? ToiletMode.REDSTONE : ToiletMode.DEFAULT;
            return state.setValue(TOILET_MODE, mode);
        }
        return state;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(TOILET_MODE) == ToiletMode.REDSTONE;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(TOILET_MODE) == ToiletMode.REDSTONE ? 15 : 0;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        ToiletType type = getToiletType(level, pos);
        if (type != null) {
            float hardness = type.hardness();
            int i = EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
            return player.getDestroySpeed(state, pos) / hardness / (float) i;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return MapColor.STONE;
    }
}
