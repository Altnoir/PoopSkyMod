package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
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
import net.minecraft.world.level.BlockGetter;
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

public class ToiletBlock extends AbstractToiletBlock {
    public static final MapCodec<ToiletBlock> CODEC = simpleCodec(ToiletBlock::new);

    public enum WoodType implements StringRepresentable {
        OAK(PToiletTypes.OAK),
        SPRUCE(PToiletTypes.SPRUCE),
        BIRCH(PToiletTypes.BIRCH),
        JUNGLE(PToiletTypes.JUNGLE),
        ACACIA(PToiletTypes.ACACIA),
        CHERRY(PToiletTypes.CHERRY),
        DARK_OAK(PToiletTypes.DARK_OAK),
        MANGROVE(PToiletTypes.MANGROVE),
        BAMBOO(PToiletTypes.BAMBOO),
        CRIMSON(PToiletTypes.CRIMSON),
        WARPED(PToiletTypes.WARPED);

        private final ToiletType toiletType;

        WoodType(ToiletType toiletType) {
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

    public static final EnumProperty<WoodType> WOOD_TYPE = EnumProperty.create("wood_type", WoodType.class);

    private static final Map<Block, WoodType> PLANK_TO_TYPE = Map.ofEntries(
            Map.entry(Blocks.OAK_PLANKS, WoodType.OAK),
            Map.entry(Blocks.SPRUCE_PLANKS, WoodType.SPRUCE),
            Map.entry(Blocks.BIRCH_PLANKS, WoodType.BIRCH),
            Map.entry(Blocks.JUNGLE_PLANKS, WoodType.JUNGLE),
            Map.entry(Blocks.ACACIA_PLANKS, WoodType.ACACIA),
            Map.entry(Blocks.CHERRY_PLANKS, WoodType.CHERRY),
            Map.entry(Blocks.DARK_OAK_PLANKS, WoodType.DARK_OAK),
            Map.entry(Blocks.MANGROVE_PLANKS, WoodType.MANGROVE),
            Map.entry(Blocks.BAMBOO_PLANKS, WoodType.BAMBOO),
            Map.entry(Blocks.CRIMSON_PLANKS, WoodType.CRIMSON),
            Map.entry(Blocks.WARPED_PLANKS, WoodType.WARPED)
    );

    public ToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, AbstractToiletBlock.ToiletState.DEFAULT)
                .setValue(WOOD_TYPE, WoodType.OAK));
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
            WoodType type = PLANK_TO_TYPE.get(blockItem.getBlock());
            if (type != null && state.getValue(WOOD_TYPE) != type) {
                level.setBlock(pos, state.setValue(WOOD_TYPE, type), 3);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
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
        return withVariant(this, state.getValue(WOOD_TYPE).getToiletType());
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
        for (var type : WoodType.values()) {
            output.accept(withVariant(this, type.getToiletType()));
        }
    }

    public BlockState applyVariant(BlockState state, ToiletType toiletType) {
        for (var woodType : WoodType.values()) {
            if (woodType.getToiletType().equals(toiletType)) {
                return state.setValue(WOOD_TYPE, woodType);
            }
        }
        return state;
    }
}