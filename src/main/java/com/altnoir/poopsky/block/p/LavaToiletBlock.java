package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.LavaToiletVariant;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.ToiletType;
import com.altnoir.poopsky.item.p.ToiletBlockItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class LavaToiletBlock extends BaseToiletLavaBlock {

    public static final MapCodec<LavaToiletBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    LavaToiletVariant.CODEC.fieldOf("default_variant").forGetter(b -> b.defaultVariant),
                    propertiesCodec()
            ).apply(instance, LavaToiletBlock::new)
    );

    public static final EnumProperty<LavaToiletVariant> VARIANT = EnumProperty.create("variant", LavaToiletVariant.class);

    private final LavaToiletVariant defaultVariant;

    public LavaToiletBlock(LavaToiletVariant defaultVariant, Properties properties) {
        super(properties);
        this.defaultVariant = defaultVariant;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, AbstractToiletBlock.ToiletState.DEFAULT)
                .setValue(LAVA, false)
                .setValue(VARIANT, defaultVariant));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VARIANT);
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
            ToiletType toiletType = ToiletType.bySourceBlock(blockItem.getBlock());
            if (toiletType != null) {
                LavaToiletVariant variant = LavaToiletVariant.byToiletType(toiletType);
                if (variant != null && variant.getCategory() == state.getValue(VARIANT).getCategory() && state.getValue(VARIANT) != variant) {
                    if (!state.getValue(LAVA)) {
                        level.setBlock(pos, state.setValue(VARIANT, variant), 3);
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return withVariant(this, state.getValue(VARIANT).getToiletType());
    }

    public static ItemStack withVariant(Block block, ToiletType toiletType) {
        return ToiletBlockItem.withType(block, toiletType);
    }

    public void addToCreativeTab(CreativeModeTab.Output output) {
        ToiletType.Category category = defaultVariant.getCategory();
        for (var variant : LavaToiletVariant.values()) {
            if (variant.getCategory() == category) {
                output.accept(withVariant(this, variant.getToiletType()));
            }
        }
    }

    public BlockState applyVariant(BlockState state, ToiletType toiletType) {
        LavaToiletVariant variant = LavaToiletVariant.byToiletType(toiletType);
        if (variant != null && variant.getCategory() == defaultVariant.getCategory()) {
            return state.setValue(VARIANT, variant);
        }
        return state;
    }
}
