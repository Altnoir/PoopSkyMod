package com.altnoir.poopsky.common.block.p;

import com.altnoir.poopsky.common.block.ToiletType;
import com.altnoir.poopsky.common.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.util.toiletUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class LavaToiletBlock extends BaseToiletLavaBlock {
    public static final MapCodec<LavaToiletBlock> CODEC = simpleCodec(LavaToiletBlock::new);

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

    public LavaToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, AbstractToiletBlock.ToiletState.DEFAULT)
                .setValue(LAVA, false)
                .setValue(TOILET_MODE, ToiletMode.DEFAULT));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public ToiletType getDefaultToiletType() {
        return PToiletTypes.TILE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TOILET_MODE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        return state
                .setValue(LAVA, false);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        boolean isGolden = toiletUtil.isGoldenToilet(level, pos);
        toiletUtil.lavaToiletStepOn(level, pos, state, entity, isGolden);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(LAVA)) {
            ItemInteractionResult result = handleVariantReplacement(stack, state, level, pos, player, ToiletType.Category.HARD);
            if (result != null) return result;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    public BlockState applyVariant(BlockState state, ToiletType toiletType) {
        if (toiletType != null && toiletType.category() == ToiletType.Category.HARD) {
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
            return player.getDigSpeed(state, pos) / hardness / (float) i;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        return MapColor.STONE;
    }
}