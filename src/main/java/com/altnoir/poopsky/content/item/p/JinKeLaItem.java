package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.p.PoopFarmlandBlock;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class JinKeLaItem extends Item {
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
            if (mode.isEnriched()) {
                return InteractionResult.PASS;
            }
            if (level instanceof ServerLevel serverLevel && tryApplyToBlock(serverLevel, pos, state)) {
                context.getItemInHand().consume(1, context.getPlayer());
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else if (state.isRandomlyTicking()) {
            if (level instanceof ServerLevel serverLevel && tryApplyToBlock(serverLevel, pos, state)) {
                context.getItemInHand().consume(1, context.getPlayer());
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useOn(context);
    }

    public static boolean tryApplyToBlock(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.is(PoBlocks.POOP_FARMLAND.get())) {
            PoopFarmlandBlock.FarmMode mode = state.getValue(PoopFarmlandBlock.MODE);
            if (mode.isEnriched()) {
                return false;
            }
            level.setBlockAndUpdate(pos, state.setValue(PoopFarmlandBlock.MODE, mode.withEnriched(true)));
            level.playSound(null, pos, PoSoundEvents.ITEM_JINKELA_USE.get(), SoundSource.BLOCKS);
            return true;
        }

        if (!state.isRandomlyTicking()) {
            return false;
        }

        triggerRandomTick(level, pos, state);
        level.levelEvent(1505, pos, 15);
        if (!(state.getBlock() instanceof BonemealableBlock)) {
            level.sendParticles(
                    ParticleTypes.COMPOSTER,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    45, 0.25, 0.25, 0.25, 0.0
            );
        }
        return true;
    }

    public static void triggerRandomTick(ServerLevel level, BlockPos pos, BlockState state) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty("age");
        if (property instanceof IntegerProperty age) {
            int maxAge = getMaxAge(age);
            int targetAge = isTerminalAge(maxAge) ? Math.min(state.getValue(age) + 1, maxAge - 1) : maxAge;
            level.setBlockAndUpdate(pos, state.setValue(age, targetAge));
            level.getBlockState(pos).randomTick(level, pos, level.getRandom());
        } else {
            state.randomTick(level, pos, level.getRandom());
        }
    }

    private static boolean isTerminalAge(int maxAge) {
        return maxAge == ChorusFlowerBlock.DEAD_AGE || maxAge == GrowingPlantHeadBlock.MAX_AGE;
    }

    public static int getMaxAge(IntegerProperty age) {
        return age.getPossibleValues().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.poopsky.jinkela.info"));
    }
}
