package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.AbstractCompooperBlock;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3f;

public class UrineCompooperBlock extends AbstractCompooperBlock {
    public static final MapCodec<UrineCompooperBlock> CODEC = simpleCodec(UrineCompooperBlock::new);

    public UrineCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LEVEL, 3)
                        .setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int i = state.getValue(LEVEL);

        Item bottle = PSItems.URINE_BOTTLE.get();
        Item bucket = PSItems.POOP_BUCKET.get();

        if (i >= MAX_LEVEL) {
            if (stack.getItem() == Items.BUCKET) {
                return BucketUse(stack, level, pos, player, hand, SoundEvents.BUCKET_FILL, bucket.getDefaultInstance());
            }
        } else if (stack.getItem() == bottle) {
            return liquidBottleUse(stack, state, level, pos, player, hand, SoundEvents.BOTTLE_EMPTY);
        }
        if (i > MIN_LEVEL && stack.getItem() == Items.GLASS_BOTTLE) {
            return glassBottleUse(stack, state, level, pos, player, hand, SoundEvents.BOTTLE_FILL, bottle.getDefaultInstance());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (neighborPos.equals(pos.below()) && state.getValue(LEVEL) == MAX_LEVEL && isHot((ServerLevel) level, pos)) {
            level.scheduleTick(pos, this, 20);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LEVEL) == MAX_LEVEL && isHot(level, pos)) {
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);

            var waterColor = level.getBiome(pos).value().getWaterColor();
            var red = (waterColor >> 16 & 0xFF) / 255.0F;
            var green = (waterColor >> 8 & 0xFF) / 255.0F;
            var blue = (waterColor & 0xFF) / 255.0F;

            var color = new Vector3f(red, green, blue);

            level.sendParticles(
                    new DustColorTransitionOptions(color, color, 1.0F),
                    pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                    15,
                    0.3, 0.1, 0.3,
                    0.02
            );

            BlockState compooperBlock = PSBlocks.WATER_COMPOOPER.get().defaultBlockState().setValue(LEVEL, MAX_LEVEL);
            level.setBlockAndUpdate(pos, compooperBlock);
        }
        level.scheduleTick(pos, this, 20);
    }
}
