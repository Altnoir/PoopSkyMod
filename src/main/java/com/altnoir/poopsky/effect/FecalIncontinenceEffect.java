package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.sound.PSSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class FecalIncontinenceEffect extends StatusEffect {
    public FecalIncontinenceEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        int i = entity.getRandom().nextInt(Math.max(1, 20 - amplifier * 2));
        if (i == 0) {
            if (entity instanceof PlayerEntity playerEntity) {
                if (!playerEntity.isSpectator()) {
                    playerEntity.addExhaustion(0.1F * (amplifier + 1));
                    fecalIncontinence(playerEntity);
                }
            } else {
                fecalIncontinence(entity);
            }
        }

        return true;
    }

    private void fecalIncontinence(LivingEntity entity) {
        float pitch = entity.getRandom().nextFloat() + 0.5F;

        if (!entity.getWorld().isClient) {
            ItemEntity poop = new ItemEntity(
                    entity.getWorld(),
                    entity.getX(),
                    entity.getY() + 0.1,
                    entity.getZ(),
                    new ItemStack(PSItems.POOP)
            );
            poop.setPickupDelay(20);
            entity.getWorld().spawnEntity(poop);
            entity.getWorld().syncWorldEvent(2001,
                    BlockPos.ofFloored(
                            entity.getX(),
                            entity.getY() + 0.1,
                            entity.getZ()
                    ), Block.getRawIdFromState(PSBlocks.POOP_BLOCK.getDefaultState())
            );
            entity.getWorld().playSound(null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    PSSounds.FART,
                    entity.getSoundCategory(),
                    1.0F, pitch
            );
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
