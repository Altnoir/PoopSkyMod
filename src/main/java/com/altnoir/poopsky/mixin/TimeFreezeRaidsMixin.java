package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.PoTimeFreeze;
import net.minecraft.world.entity.raid.Raids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raids.class)
public abstract class TimeFreezeRaidsMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeRaids(CallbackInfo ci) {
        if (PoTimeFreeze.serverFrozen()) ci.cancel();
    }
}
