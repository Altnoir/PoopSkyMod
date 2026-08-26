package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.client.ToiletClientBlockExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
    @Inject(method = "destroy", at = @At("HEAD"), cancellable = true)
    private void poopsky$replaceToiletDestroyState(BlockPos pos, BlockState state, CallbackInfo callback) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        ToiletClientBlockExtensions.beginDestroyParticles(state, level, pos);
        if (ToiletClientBlockExtensions.getDestroyParticleTexture() != null) return;

        BlockState particleState = ToiletClientBlockExtensions.getParticleState(state, level, pos);
        if (particleState == state) return;

        ((ParticleEngine) (Object) this).destroy(pos, particleState);
        callback.cancel();
    }

    @Inject(method = "destroy", at = @At("RETURN"))
    private void poopsky$clearToiletDestroyTexture(BlockPos pos, BlockState state, CallbackInfo callback) {
        ToiletClientBlockExtensions.endDestroyParticles();
    }

    @Inject(method = "crack", at = @At("HEAD"))
    private void poopsky$setToiletCrackTexture(BlockPos pos, Direction direction, CallbackInfo callback) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            ToiletClientBlockExtensions.beginDestroyParticles(level.getBlockState(pos), level, pos);
        }
    }

    @Inject(method = "crack", at = @At("RETURN"))
    private void poopsky$clearToiletCrackTexture(BlockPos pos, Direction direction, CallbackInfo callback) {
        ToiletClientBlockExtensions.endDestroyParticles();
    }
}
