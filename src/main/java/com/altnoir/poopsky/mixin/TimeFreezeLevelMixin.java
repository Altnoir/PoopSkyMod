package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.PoTimeFreeze;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class TimeFreezeLevelMixin {
    @Inject(method = "tickBlockEntities", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeBlockEntities(CallbackInfo ci) { if (PoTimeFreeze.frozen((Level) (Object) this)) ci.cancel(); }
}
