package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.CompooperType;
import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class PowderSnowCompooperBlock extends AbstractCompooperBlock implements com.altnoir.poopsky.content.block.PoBlockInteraction {

    public PowderSnowCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
    }


    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, net.minecraft.world.level.BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(PoBlocks.COMPOOPER.get());
    }

    @Override
    public InteractionResult poopsky$useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int i = state.getValue(LEVEL);

        if (i == MAX_LEVEL && stack.getItem() == Items.BUCKET) {
            return BucketUse(stack, level, pos, player, hand, SoundEvents.BUCKET_FILL_POWDER_SNOW, Items.POWDER_SNOW_BUCKET.getDefaultInstance());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !isEntityInsideContent(pos, state, entity)) {
            return;
        }
        if (entity.isOnFire()) {
            entity.clearFire();
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
            if (entity.mayInteract(level, pos)) {
                BlockState newState = PoBlocks.WATER_COMPOOPER.get().defaultBlockState().setValue(LEVEL, 2);
                level.setBlockAndUpdate(pos, newState);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            }
            return;
        }
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();

            if (processRecipe(CompooperType.POWDER_SNOW, itemEntity, state, level, pos, SoundEvents.FIREWORK_ROCKET_BLAST)) {
                return;
            }

            if (stack.is(com.altnoir.poopsky.init.PoVanillaBackport.BREEZE_ROD.get())) {
                entity.setDeltaMovement(entity.getDeltaMovement().x, (entity.isNoGravity() ? 0.0D : 0.04D) + 0.1, entity.getDeltaMovement().z);
            }
        }
    }

    @Override
    public net.minecraft.world.InteractionResult use(net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit) {
        return poopsky$interact(state, level, pos, player, hand, hit);
    }
}
