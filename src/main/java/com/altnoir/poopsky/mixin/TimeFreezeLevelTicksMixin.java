package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.PoTimeFreeze;
import net.minecraft.world.ticks.LevelTicks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.BlockPos;
import java.util.function.BiConsumer;

@Mixin(LevelTicks.class)
public abstract class TimeFreezeLevelTicksMixin<T> {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeScheduled(long time, int max, BiConsumer<BlockPos,T> ticker, CallbackInfo ci) {
        if (PoTimeFreeze.serverFrozen()) ci.cancel();
    }
}
