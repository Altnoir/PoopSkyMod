package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.util.ClientUtil;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
    @Shadow
    private boolean bonusChest;

    @Inject(method = "setBonusChest", at = @At("HEAD"))
    private void poopsky$preventBonusChestForPoopSky(boolean bonusChest, CallbackInfo ci) {
        if (bonusChest) {
            ClientUtil.isPoopSkyWorldType((WorldCreationUiState) (Object) this);
        }
    }

    @Inject(method = "setWorldType", at = @At("HEAD"))
    private void poopsky$clearBonusChestForPoopSky(WorldCreationUiState.WorldTypeEntry worldType, CallbackInfo ci) {
        if (ClientUtil.isPoopSkyWorldType(worldType)) {
            this.bonusChest = false;
        }
    }

    @Inject(method = "isBonusChest", at = @At("HEAD"), cancellable = true)
    private void poopsky$hideBonusChestForPoopSky(CallbackInfoReturnable<Boolean> cir) {
        if (ClientUtil.isPoopSkyWorldType((WorldCreationUiState)(Object)this)) {
            cir.setReturnValue(false);
        }
    }
}