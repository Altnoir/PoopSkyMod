package com.altnoir.poopsky.item.p;

import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.block.p.BaseToiletLavaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
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
            if (block instanceof BaseToiletLavaBlock && state.getValue(BaseToiletLavaBlock.LAVA)) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide && !stack.isEmpty()) {
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 0.6F);
                level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);

                var result = ItemUtils.createFilledResult(stack, player, new ItemStack(PBlocks.URINE_COMPOOPER.get()));
                player.setItemInHand(context.getHand(), result);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
