package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.block.p.FlyNestBlock;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
    @Redirect(
            method = "releaseOccupant",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V")
    )
    private static void redirectHiveExitSound(Level level, Player player, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        if (level.getBlockState(pos).getBlock() instanceof FlyNestBlock) {
            level.playSound(player, pos, PSoundEvents.BLOCK_FLY_NEST_EXIT.get(), source, volume, pitch);
        } else {
            level.playSound(player, pos, sound, source, volume, pitch);
        }
    }

}
