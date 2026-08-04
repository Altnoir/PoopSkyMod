package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.event.hook.FabricatedCommonEventHooks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapMethod(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
    private boolean poopsky_fabric$onAddEffect(MobEffectInstance mobEffectInstance, Entity entity, Operation<Boolean> original) {
        if (!FabricatedCommonEventHooks.canMobEffectBeApplied((LivingEntity) (Object) this, mobEffectInstance)) {
            return false;
        } else {
            return original.call(mobEffectInstance, entity);
        }
    }

    @WrapMethod(method = "forceAddEffect")
    private void poopsky_fabric$canAddEffect(MobEffectInstance mobEffectInstance, Entity entity, Operation<Void> original) {
        if (FabricatedCommonEventHooks.canMobEffectBeApplied((LivingEntity) (Object) this, mobEffectInstance)) {
            original.call(mobEffectInstance, entity);
        }
    }

    @Inject(
            method = "checkFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos,
                                   CallbackInfo ci, @Local(index = 19) int count) {
        if (state.getBlock().addLandingEffects(state, (ServerLevel) level(), pos, state, (LivingEntity) (Object) this, count)) {
            super.checkFallDamage(y, onGround, state, pos);
            ci.cancel();
        }
    }
}
