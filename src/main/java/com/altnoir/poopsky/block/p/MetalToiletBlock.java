package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

public class MetalToiletBlock extends ToiletLavaBlock {
    public static final MapCodec<MetalToiletBlock> CODEC = simpleCodec(MetalToiletBlock::new);

    public enum MetalType implements StringRepresentable {
        IRON, GOLD, COPPER, NETHERITE, RAW_IRON, RAW_GOLD, RAW_COPPER, DIAMOND, EMERALD, LAPIS, REDSTONE, QUARTZ;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }

        public Component getDisplayName() {
            return Component.translatable("toilet_type.poopsky." + getSerializedName());
        }
    }

    public static final EnumProperty<MetalType> METAL_TYPE = EnumProperty.create("metal_type", MetalType.class);

    private static final Map<Block, MetalType> BLOCK_TO_TYPE = Map.ofEntries(
            Map.entry(Blocks.IRON_BLOCK, MetalType.IRON),
            Map.entry(Blocks.GOLD_BLOCK, MetalType.GOLD),
            Map.entry(Blocks.COPPER_BLOCK, MetalType.COPPER),
            Map.entry(Blocks.NETHERITE_BLOCK, MetalType.NETHERITE),
            Map.entry(Blocks.RAW_IRON_BLOCK, MetalType.RAW_IRON),
            Map.entry(Blocks.RAW_GOLD_BLOCK, MetalType.RAW_GOLD),
            Map.entry(Blocks.RAW_COPPER_BLOCK, MetalType.RAW_COPPER),
            Map.entry(Blocks.DIAMOND_BLOCK, MetalType.DIAMOND),
            Map.entry(Blocks.EMERALD_BLOCK, MetalType.EMERALD),
            Map.entry(Blocks.LAPIS_BLOCK, MetalType.LAPIS),
            Map.entry(Blocks.REDSTONE_BLOCK, MetalType.REDSTONE),
            Map.entry(Blocks.QUARTZ_BLOCK, MetalType.QUARTZ)
    );

    public MetalToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, AbstractToiletBlock.ToiletState.DEFAULT)
                .setValue(LAVA, false)
                .setValue(METAL_TYPE, MetalType.IRON));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(METAL_TYPE);
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
            MetalType type = BLOCK_TO_TYPE.get(blockItem.getBlock());
            if (type != null && state.getValue(METAL_TYPE) != type) {
                if (!state.getValue(LAVA)) {
                    level.setBlock(pos, state.setValue(METAL_TYPE, type), 3);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return withVariant(this, state.getValue(METAL_TYPE).getSerializedName());
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        if (level instanceof ServerLevel) {
            popResource(level, pos, withVariant(this, state.getValue(METAL_TYPE).getSerializedName()));
        }
    }

    public static ItemStack withVariant(Block block, String variant) {
        var stack = new ItemStack(block);
        stack.set(PComponents.TOILET_TYPE.get(), variant);
        return stack;
    }

    public void addToCreativeTab(CreativeModeTab.Output output) {
        for (var type : MetalType.values()) {
            output.accept(withVariant(this, type.getSerializedName()));
        }
    }

    public BlockState applyVariant(BlockState state, String variant) {
        try {
            var type = MetalType.valueOf(variant.toUpperCase());
            return state.setValue(METAL_TYPE, type);
        } catch (IllegalArgumentException e) {
            return state;
        }
    }
}