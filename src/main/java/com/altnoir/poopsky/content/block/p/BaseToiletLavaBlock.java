package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.ToiletTypes;
import com.altnoir.poopsky.impl.util.toiletUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BaseToiletLavaBlock extends AbstractToiletBlock {
    public static final MapCodec<BaseToiletLavaBlock> CODEC = simpleCodec(BaseToiletLavaBlock::new);
    public static final BooleanProperty LAVA = BooleanProperty.create("lava");

    public BaseToiletLavaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(LAVA, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public ToiletType getDefaultToiletType() {
        return ToiletTypes.COBBLESTONE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LAVA);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        toiletUtil.lavaToiletStepOn(level, pos, state, entity, false);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(LAVA)) {
            if (stack.is(Items.BUCKET)) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlock(pos, state.setValue(LAVA, false), 3);

                ItemStack itemStack = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.LAVA_BUCKET));
                player.setItemInHand(hand, itemStack);

                return ItemInteractionResult.sidedSuccess(false);
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
