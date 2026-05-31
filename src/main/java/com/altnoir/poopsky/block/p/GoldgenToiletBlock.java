package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.effect.PSEffects;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticles;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GoldgenToiletBlock extends ToiletLavaBlock {
    public GoldgenToiletBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof Player player && player.isShiftKeyDown() && isEntityCentered(pos, player) && !state.getValue(LAVA)) {
            if (player.hasEffect(PSEffects.INTESTINAL_SPASM)) {
                level.setBlock(pos, state.setValue(LAVA, true), 3);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.removeEffect(PSEffects.INTESTINAL_SPASM);
                player.causeFoodExhaustion(1.0F);
            } else if (player.hasEffect(PSEffects.FECAL_INCONTINENCE)) {
                onPoopGolden(level, player);
                player.causeFoodExhaustion(0.05F);
            } else if (level.getGameTime() % 20 == 0) {
                onPoopGolden(level, player);
                player.causeFoodExhaustion(1.0F);
            }
        }
    }

    protected void onPoopGolden(Level level, Player player) {
        if (player.getFoodData().getFoodLevel() <= 0) {
            player.hurt(level.damageSources().wither(), 1.0F);

            var redStone = new ItemEntity(
                    level,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    new ItemStack(Items.REDSTONE)
            );
            redStone.setDefaultPickUpDelay();
            level.addFreshEntity(redStone);
        } else {
            var poop = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), new ItemStack(PSItems.GOLDEN_POOP.get()));

            poop.setDefaultPickUpDelay();
            level.addFreshEntity(poop);
        }
        var pitch = level.random.nextFloat() - 0.5F;
        level.playSound(null, player.getX(), player.getY() + 0.1, player.getZ(), PSSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PSParticles.POOP_PARTICLE.get(),
                player.getX(),
                player.getY() + 0.1,
                player.getZ(),
                8,
                0.0,
                -0.1,
                0.0,
                3.0
        );
    }
}
