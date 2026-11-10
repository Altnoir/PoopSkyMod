package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.p.PigstyBlock;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class PigstyBlockItem extends BlockItem {
    public PigstyBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (stack.has(PoComponents.PIGSTY_PIG) || !(target instanceof Pig pig) || !pig.isAlive()
                || pig.isBaby() || pig.isPassenger() || pig.isVehicle() || PigstyBlock.isConfined(pig)) {
            return InteractionResult.PASS;
        }
        if (!player.level().isClientSide) {
            CompoundTag data = new CompoundTag();
            if (!pig.saveAsPassenger(data)) return InteractionResult.FAIL;
            ItemStack captured = stack.copyWithCount(1);
            captured.set(PoComponents.PIGSTY_PIG, CustomData.of(data));
            if (stack.getCount() == 1) {
                player.setItemInHand(hand, captured);
            } else {
                player.getItemInHand(hand).shrink(1);
                if (!player.getInventory().add(captured)) {
                    player.drop(captured, false);
                }
            }
            player.level().playSound(null, pig.getX(), pig.getY(), pig.getZ(),
                    PoSoundEvents.ENTITY_FLY_CAPTURE.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF, pig.getX(), pig.getY(0.5), pig.getZ(),
                        20, 0.3, 0.3, 0.3, 0.02);
            }
            pig.discard();
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (stack.has(PoComponents.PIGSTY_PIG)) {
            tooltip.add(Component.translatable("tooltip.poopsky.pigsty.captured").withStyle(ChatFormatting.GRAY));
        }
    }
}
