package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SaltpeterBlock extends AmethystBlock {
    public SaltpeterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        BlockPos pos = hit.getBlockPos();
        if (level instanceof ServerLevel serverLevel && projectile.mayInteract(serverLevel, pos)) {
            level.playSound(null, pos, PoSoundEvents.BLOCK_SALTPETER_CHIME.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
