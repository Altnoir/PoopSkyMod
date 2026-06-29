package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.util.toiletUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GoldgenBaseToiletBlock extends BaseToiletLavaBlock {
    public GoldgenBaseToiletBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof Player player && player.isShiftKeyDown() && isEntityCentered(pos, player) && !state.getValue(LAVA)) {
            if (player.hasEffect(PEffects.INTESTINAL_SPASM)) {
                level.setBlock(pos, state.setValue(LAVA, true), 3);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.removeEffect(PEffects.INTESTINAL_SPASM);
                player.causeFoodExhaustion(1.0F);
            } else if (player.hasEffect(PEffects.FECAL_INCONTINENCE)) {
                toiletUtil.onPoop(level, player, false, true, 0.1F, -0.5F);
                player.causeFoodExhaustion(0.05F);
            } else {
                var playerData = player.getPersistentData();
                long lastPoopTime = playerData.getLong("poopTime");
                long gameTime = level.getGameTime();
                if (lastPoopTime == 0 || gameTime - lastPoopTime >= 20) {
                    toiletUtil.onPoop(level, player, false, true, 0.1F, -0.5F);
                    player.causeFoodExhaustion(1.0F);
                    playerData.putLong("poopTime", gameTime);
                }
            }
        }
    }
}