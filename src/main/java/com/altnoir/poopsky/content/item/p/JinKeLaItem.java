package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.p.PoopFarmlandBlock;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class JinKeLaItem extends BoneMealItem {
    public JinKeLaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(PoBlocks.POOP_FARMLAND.get())) {
            PoopFarmlandBlock.FarmlandMode mode = state.getValue(PoopFarmlandBlock.MODE);
            if (!mode.isEnriched()) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(PoopFarmlandBlock.MODE, mode.withEnriched(true)), 3);
                    level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    context.getItemInHand().consume(1, context.getPlayer());
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        }

        return super.useOn(context);
    }
}