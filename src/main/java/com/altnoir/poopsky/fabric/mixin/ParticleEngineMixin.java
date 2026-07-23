package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.client.ToiletClientBlockExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
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

        BlockState particleState = ToiletClientBlockExtensions.getParticleState(state, level, pos);
        if (particleState == state) return;

        ((ParticleEngine) (Object) this).destroy(pos, particleState);
        callback.cancel();
    }
}
