package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class CompooperBlockItem extends BlockItem {
    public CompooperBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (block instanceof AbstractToiletBlock && player != null && !player.isShiftKeyDown()) {
            if (!level.isClientSide && !stack.isEmpty()) {
                boolean isLava = block instanceof BaseToiletLavaBlock && state.getValue(BaseToiletLavaBlock.LAVA);

                SoundEvent soundEvent = isLava ? PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL_LAVA.get() : PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL.get();
                level.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 1.0F, 0.6F);
                level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);

                Block resultBlock = isLava ? PoBlocks.LAVA_COMPOOPER.get() : PoBlocks.URINE_COMPOOPER.get();
                var result = ItemUtils.createFilledResult(stack, player, new ItemStack(resultBlock));
                player.setItemInHand(context.getHand(), result);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}