package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.p.PoopFarmlandBlock;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

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
            PoopFarmlandBlock.FarmMode mode = state.getValue(PoopFarmlandBlock.MODE);
            if (!mode.isEnriched()) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, state.setValue(PoopFarmlandBlock.MODE, mode.withEnriched(true)));
                    level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS);
                    context.getItemInHand().consume(1, context.getPlayer());
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.poopsky.jinkela.info"));
    }
}