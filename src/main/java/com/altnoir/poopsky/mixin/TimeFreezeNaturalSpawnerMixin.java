package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.PoTimeFreeze;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public abstract class TimeFreezeNaturalSpawnerMixin {
    @Inject(method = "spawnForChunk", at = @At("HEAD"), cancellable = true)
    private static void poopsky$freezeNaturalSpawns(CallbackInfo ci) {
        if (PoTimeFreeze.serverFrozen()) ci.cancel();
    }
}
