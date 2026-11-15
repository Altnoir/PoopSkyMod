package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.client.SnailShellCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class SnailShellCameraMixin {
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void poopsky$observeShell(double dx, double dy, CallbackInfo ci) {
        if ((Object) this == Minecraft.getInstance().player && SnailShellCamera.turn(dx, dy)) {
            ci.cancel();
        }
    }
}
