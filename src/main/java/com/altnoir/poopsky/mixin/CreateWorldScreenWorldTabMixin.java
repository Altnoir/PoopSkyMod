package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.util.ClientUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.BooleanSupplier;

@Mixin(CreateWorldScreen.WorldTab.class)
public abstract class CreateWorldScreenWorldTabMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$SwitchBuilder;withIsActiveCondition(Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$SwitchBuilder;", ordinal = 1))
    private BooleanSupplier poopsky$disableBonusChestForPoopSky(BooleanSupplier original,
            @Local(argsOnly = true) CreateWorldScreen screen) {
        return () -> original.getAsBoolean() && !ClientUtil.isPoopSkyWorldType(screen.getUiState());
    }
}
