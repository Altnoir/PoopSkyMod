package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.event.hook.FabricatedEventHooks;
import com.altnoir.poopsky.fabric.port.extension.IEntityExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements IEntityExtension {
    @Shadow
    @Nullable
    private Entity vehicle;

    @Inject(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;canRide(Lnet/minecraft/world/entity/Entity;)Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    public void poopsky_fabric$startRiding(Entity entity, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!FabricatedEventHooks.canMountEntity((Entity) (Object) this, entity, true))
            cir.setReturnValue(false);
    }

    @Inject(method = "removeVehicle", at = @At(value = "CONSTANT", args = "nullValue=true"), cancellable = true)
    public void poopsky_fabric$removeRidingEntity(CallbackInfo ci) {
        if (!FabricatedEventHooks.canMountEntity((Entity) (Object) this, this.vehicle, false))
            ci.cancel();
    }

    @WrapOperation(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void poopsky_fabric$preEntityTick(Entity instance, Operation<Void> original) {
        if (!FabricatedEventHooks.fireEntityTickPre(instance)) {
            original.call(instance);
            FabricatedEventHooks.fireEntityTickPost(instance);
        }
    }

    // custom data

    @Unique
    private CompoundTag customData;

    @Inject(
            method = "saveWithoutId",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
            )
    )
    private void saveCustomData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (customData != null && !customData.isEmpty()) {
            tag.put("PofabData", customData);
        }
    }

    @Inject(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
            )
    )
    private void loadCustomData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("PofabData")) {
            customData = tag.getCompound("PofabData");
        }
    }

    @Override
    public CompoundTag getCustomData() {
        if (customData == null)
            customData = new CompoundTag();
        return customData;
    }
}
