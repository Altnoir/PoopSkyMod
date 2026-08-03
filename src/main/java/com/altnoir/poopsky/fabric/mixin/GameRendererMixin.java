package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.client.renderer.TimeBellOverlay;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void poopsky$applyTimeBellFov(Camera camera, float partialTick, boolean useFovSetting,
                                         CallbackInfoReturnable<Double> cir) {
        double multiplier = TimeBellOverlay.getFovMultiplier();
        if (multiplier != 1.0) {
            cir.setReturnValue(Math.min(cir.getReturnValue() * multiplier, TimeBellOverlay.MAX_FOV));
        }
    }
}
