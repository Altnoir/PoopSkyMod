package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.client.renderer.ArcadeRenderTargetOverride;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "getMainRenderTarget", at = @At("HEAD"), cancellable = true)
    private void poopsky$overrideMainRenderTarget(CallbackInfoReturnable<RenderTarget> cir) {
        RenderTarget target = ArcadeRenderTargetOverride.get();
        if (target != null) {
            cir.setReturnValue(target);
        }
    }
}
