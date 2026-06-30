package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.ToiletTypeProperty;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.block.ToiletType;
import com.altnoir.poopsky.item.p.ToiletBlockItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ToiletBlock extends AbstractToiletBlock {
    public static final MapCodec<ToiletBlock> CODEC = simpleCodec(ToiletBlock::new);
    private static final Object _INIT;
    static {
        _INIT = PToiletTypes.OAK;
    }

    public static final ToiletTypeProperty WOOD_TYPE = new ToiletTypeProperty("wood_type", ToiletType.Category.WOOD);

    public ToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, AbstractToiletBlock.ToiletState.DEFAULT)
                .setValue(WOOD_TYPE, PToiletTypes.OAK));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WOOD_TYPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            ToiletType toiletType = ToiletType.bySourceBlock(blockItem.getBlock());
            if (toiletType != null && toiletType.category() == ToiletType.Category.WOOD) {
                ToiletType currentType = state.getValue(WOOD_TYPE);
                if (currentType != toiletType) {
                    level.setBlock(pos, state.setValue(WOOD_TYPE, toiletType), 3);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return withVariant(this, state.getValue(WOOD_TYPE));
    }

    public static ItemStack withVariant(Block block, ToiletType toiletType) {
        return ToiletBlockItem.withType(block, toiletType);
    }

    public BlockState applyVariant(BlockState state, ToiletType toiletType) {
        if (toiletType != null && toiletType.category() == ToiletType.Category.WOOD) {
            return state.setValue(WOOD_TYPE, toiletType);
        }
        return state;
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
        Block sourceBlock = state.getValue(WOOD_TYPE).sourceBlock();
        if (sourceBlock != null) {
            return sourceBlock.defaultBlockState().getMapColor(level, pos);
        }
        return super.getMapColor(state, level, pos, defaultColor);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Block sourceBlock = state.getValue(WOOD_TYPE).sourceBlock();
        if (sourceBlock != null) {
            return sourceBlock.defaultBlockState().getSoundType();
        }
        return super.getSoundType(state, level, pos, entity);
    }
}