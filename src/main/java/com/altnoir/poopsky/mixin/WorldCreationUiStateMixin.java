package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.util.ClientUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
    @Shadow
    private boolean bonusChest;

    @ModifyVariable(method = "setBonusChest", at = @At("HEAD"), argsOnly = true)
    private boolean poopsky$preventBonusChestForPoopSky(boolean bonusChest) {
        return bonusChest && !ClientUtil.isPoopSkyWorldType((WorldCreationUiState) (Object) this);
    }

    @Inject(method = "setWorldType", at = @At("HEAD"))
    private void poopsky$clearBonusChestForPoopSky(WorldCreationUiState.WorldTypeEntry worldType, CallbackInfo ci) {
        if (ClientUtil.isPoopSkyWorldType(worldType)) {
            this.bonusChest = false;
        }
    }

    @ModifyReturnValue(method = "isBonusChest", at = @At("RETURN"))
    private boolean poopsky$hideBonusChestForPoopSky(boolean original) {
        return original && !ClientUtil.isPoopSkyWorldType((WorldCreationUiState) (Object) this);
    }
}
