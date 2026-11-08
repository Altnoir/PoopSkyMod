package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.PoTimeFreeze;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class TimeFreezeServerLevelMixin {
    @Inject(method = {"tickTime", "advanceWeatherCycle", "runBlockEvents"}, at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeWorld(CallbackInfo ci) { if (PoTimeFreeze.serverFrozen()) ci.cancel(); }
    @Inject(method = "tickChunk", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeChunk(LevelChunk chunk, int speed, CallbackInfo ci) { if (PoTimeFreeze.serverFrozen()) ci.cancel(); }
    @Inject(method = "tickCustomSpawners", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeSpawner(boolean hostile, boolean peaceful, CallbackInfo ci) { if (PoTimeFreeze.serverFrozen()) ci.cancel(); }
    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeEntity(Entity entity, CallbackInfo ci) { if (PoTimeFreeze.skipEntity(entity)) ci.cancel(); }
    @Inject(method = "tickPassenger", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezePassenger(Entity vehicle, Entity passenger, CallbackInfo ci) { if (PoTimeFreeze.skipEntity(passenger)) ci.cancel(); }
}
