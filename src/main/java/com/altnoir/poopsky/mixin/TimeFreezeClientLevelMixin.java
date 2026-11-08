package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.PoTimeFreeze;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class TimeFreezeClientLevelMixin {
    @Inject(method = "tickTime", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeClock(CallbackInfo ci) { if (PoTimeFreeze.frozen((ClientLevel) (Object) this)) ci.cancel(); }
    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezeEntity(Entity entity, CallbackInfo ci) { if (PoTimeFreeze.skipEntity(entity)) ci.cancel(); }
    @Inject(method = "tickPassenger", at = @At("HEAD"), cancellable = true)
    private void poopsky$freezePassenger(Entity vehicle, Entity passenger, CallbackInfo ci) { if (PoTimeFreeze.skipEntity(passenger)) ci.cancel(); }
}
