package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class BaseToiletLavaBlock extends AbstractToiletBlock {
    public static final BooleanProperty LAVA = BooleanProperty.create("lava");

    protected BaseToiletLavaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(LAVA, false));
    }

    @Override
    public boolean isLavaFilled(BlockState state) {
        return state.getValue(LAVA);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LAVA);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        ToiletUtil.lavaToiletStepOn(level, pos, state, entity, false);
    }

    @Override
    protected InteractionResult useToiletItem(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!isLavaFilled(state) || !stack.is(Items.BUCKET)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlock(pos, state.setValue(LAVA, false), 3);
        player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.LAVA_BUCKET)));
        return InteractionResult.SUCCESS_SERVER;
    }
}
