package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PComponents;
import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.init.ToiletType;
import com.altnoir.poopsky.item.p.ToiletBlockItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StoneToiletBlock extends ToiletLavaBlock {
    public static final MapCodec<StoneToiletBlock> CODEC = simpleCodec(StoneToiletBlock::new);

    public enum StoneType implements StringRepresentable {
        STONE(PToiletTypes.STONE),
        COBBLESTONE(PToiletTypes.COBBLESTONE),
        MOSSY_COBBLESTONE(PToiletTypes.MOSSY_COBBLESTONE),
        SMOOTH_STONE(PToiletTypes.SMOOTH_STONE),
        STONE_BRICK(PToiletTypes.STONE_BRICK),
        MOSSY_STONE_BRICK(PToiletTypes.MOSSY_STONE_BRICK),
        TILE(PToiletTypes.TILE),
        WHITE_CONCRETE(PToiletTypes.WHITE_CONCRETE),
        ORANGE_CONCRETE(PToiletTypes.ORANGE_CONCRETE),
        MAGENTA_CONCRETE(PToiletTypes.MAGENTA_CONCRETE),
        LIGHT_BLUE_CONCRETE(PToiletTypes.LIGHT_BLUE_CONCRETE),
        YELLOW_CONCRETE(PToiletTypes.YELLOW_CONCRETE),
        LIME_CONCRETE(PToiletTypes.LIME_CONCRETE),
        PINK_CONCRETE(PToiletTypes.PINK_CONCRETE),
        GRAY_CONCRETE(PToiletTypes.GRAY_CONCRETE),
        LIGHT_GRAY_CONCRETE(PToiletTypes.LIGHT_GRAY_CONCRETE),
        CYAN_CONCRETE(PToiletTypes.CYAN_CONCRETE),
        PURPLE_CONCRETE(PToiletTypes.PURPLE_CONCRETE),
        BLUE_CONCRETE(PToiletTypes.BLUE_CONCRETE),
        BROWN_CONCRETE(PToiletTypes.BROWN_CONCRETE),
        GREEN_CONCRETE(PToiletTypes.GREEN_CONCRETE),
        RED_CONCRETE(PToiletTypes.RED_CONCRETE),
        BLACK_CONCRETE(PToiletTypes.BLACK_CONCRETE);

        private final ToiletType toiletType;

        StoneType(ToiletType toiletType) {
            this.toiletType = toiletType;
        }

        public ToiletType getToiletType() {
            return toiletType;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static final EnumProperty<StoneType> STONE_TYPE = EnumProperty.create("stone_type", StoneType.class);

    private static final Map<Block, StoneType> BLOCK_TO_TYPE = Map.ofEntries(
            Map.entry(Blocks.STONE, StoneType.STONE),
            Map.entry(Blocks.COBBLESTONE, StoneType.COBBLESTONE),
            Map.entry(Blocks.MOSSY_COBBLESTONE, StoneType.MOSSY_COBBLESTONE),
            Map.entry(Blocks.SMOOTH_STONE, StoneType.SMOOTH_STONE),
            Map.entry(Blocks.STONE_BRICKS, StoneType.STONE_BRICK),
            Map.entry(Blocks.MOSSY_STONE_BRICKS, StoneType.MOSSY_STONE_BRICK),
            Map.entry(PBlocks.TILE_BLOCK.get(), StoneType.TILE),
            Map.entry(Blocks.WHITE_CONCRETE, StoneType.WHITE_CONCRETE),
            Map.entry(Blocks.ORANGE_CONCRETE, StoneType.ORANGE_CONCRETE),
            Map.entry(Blocks.MAGENTA_CONCRETE, StoneType.MAGENTA_CONCRETE),
            Map.entry(Blocks.LIGHT_BLUE_CONCRETE, StoneType.LIGHT_BLUE_CONCRETE),
            Map.entry(Blocks.YELLOW_CONCRETE, StoneType.YELLOW_CONCRETE),
            Map.entry(Blocks.LIME_CONCRETE, StoneType.LIME_CONCRETE),
            Map.entry(Blocks.PINK_CONCRETE, StoneType.PINK_CONCRETE),
            Map.entry(Blocks.GRAY_CONCRETE, StoneType.GRAY_CONCRETE),
            Map.entry(Blocks.LIGHT_GRAY_CONCRETE, StoneType.LIGHT_GRAY_CONCRETE),
            Map.entry(Blocks.CYAN_CONCRETE, StoneType.CYAN_CONCRETE),
            Map.entry(Blocks.PURPLE_CONCRETE, StoneType.PURPLE_CONCRETE),
            Map.entry(Blocks.BLUE_CONCRETE, StoneType.BLUE_CONCRETE),
            Map.entry(Blocks.BROWN_CONCRETE, StoneType.BROWN_CONCRETE),
            Map.entry(Blocks.GREEN_CONCRETE, StoneType.GREEN_CONCRETE),
            Map.entry(Blocks.RED_CONCRETE, StoneType.RED_CONCRETE),
            Map.entry(Blocks.BLACK_CONCRETE, StoneType.BLACK_CONCRETE)
    );

    public StoneToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, AbstractToiletBlock.ToiletState.DEFAULT)
                .setValue(LAVA, false)
                .setValue(STONE_TYPE, StoneType.COBBLESTONE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STONE_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LAVA, false);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            StoneType type = BLOCK_TO_TYPE.get(blockItem.getBlock());
            if (type != null && state.getValue(STONE_TYPE) != type) {
                if (!state.getValue(LAVA)) {
                    level.setBlock(pos, state.setValue(STONE_TYPE, type), 3);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return withVariant(this, state.getValue(STONE_TYPE).getToiletType());
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        Block.dropResources(state, level, pos, blockEntity, player, tool);
    }

    public static ItemStack withVariant(Block block, ToiletType toiletType) {
        return ToiletBlockItem.withType(block, toiletType);
    }

    public void addToCreativeTab(CreativeModeTab.Output output) {
        for (var type : StoneType.values()) {
            output.accept(withVariant(this, type.getToiletType()));
        }
    }

    public BlockState applyVariant(BlockState state, ToiletType toiletType) {
        for (var stoneType : StoneType.values()) {
            if (stoneType.getToiletType().equals(toiletType)) {
                return state.setValue(STONE_TYPE, stoneType);
            }
        }
        return state;
    }
}