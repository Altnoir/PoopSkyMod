package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.fabric.port.util.ItemAbilities;
import com.altnoir.poopsky.fabric.port.util.ItemAbility;
import com.altnoir.poopsky.init.PoBlocks;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class PoopEmptyLogBlock extends EmptyRotatedPillarBlock {
    public static final BooleanProperty ARROW = BooleanProperty.create("arrow");

    private static final Supplier<Map<Block, Block>> STRIPPABLES = Suppliers.memoize(() ->
            ImmutableMap.<Block, Block>builder()
                    .put(PoBlocks.POOP_EMPTY_LOG.get(), PoBlocks.STRIPPED_POOP_EMPTY_LOG.get())
                    .build()
    );

    public PoopEmptyLogBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ARROW, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ARROW);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.SHEARS) && !state.getValue(ARROW)) {
            level.setBlockAndUpdate(pos, state.setValue(ARROW, true));
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.AXE_STRIP) {
            Block stripped = STRIPPABLES.get().get(state.getBlock());
            if (stripped != null) {
                return stripped.defaultBlockState().setValue(FACING, state.getValue(FACING));
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}