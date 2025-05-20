package com.altnoir.poopsky.block.custom;

import com.altnoir.poopsky.block.AbstractToilet;
import com.altnoir.poopsky.effect.PSEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ToiletLavaBlock extends AbstractToilet {
    public static final MapCodec<ToiletLavaBlock> CODEC = createCodec(ToiletLavaBlock::new);
    public static final BooleanProperty LAVA = BooleanProperty.of("lava");
    public ToiletLavaBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LAVA, false));
    }

    @Override
    protected MapCodec<ToiletLavaBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LAVA);
    }
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            if (player.isSneaking() && isPlayerCentered(pos, player) && !state.get(LAVA)) {
                if (player.hasStatusEffect(PSEffect.FECAL_INCONTINENCE)) {
                    onPoop(world, player);
                    player.addExhaustion(0.05F);
                } else if (player.hasStatusEffect(PSEffect.INTESTINAL_SPASM)) {
                    world.setBlockState(pos, state.with(LAVA, true), Block.NOTIFY_ALL);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BUCKET_EMPTY_LAVA, player.getSoundCategory(), 1.0F, 1.0F);
                    player.removeStatusEffect(PSEffect.INTESTINAL_SPASM);
                    player.addExhaustion(1.0F);
                } else if (world.getTime() % 20 == 0) {
                    onPoop(world, player);
                    player.addExhaustion(1.0F);
                }
            }
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(LAVA)) {
            ItemStack stack1 = player.getStackInHand(Hand.MAIN_HAND);
            if (stack1.isOf(Items.BUCKET)) {
                stack1.decrementUnlessCreative(1, player);
                if (!player.isCreative() || !player.getInventory().contains(Items.LAVA_BUCKET.getDefaultStack())) {
                    player.giveItemStack(new ItemStack(Items.LAVA_BUCKET));
                }
                world.setBlockState(pos, state.with(LAVA, false), Block.NOTIFY_ALL);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        }
        return ActionResult.PASS;
    }
}
