package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.util.ClientUtil;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.BooleanSupplier;

@Mixin(CreateWorldScreen.WorldTab.class)
public abstract class CreateWorldScreenWorldTabMixin {
    @Shadow
    @Final
    private CreateWorldScreen this$0;

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$SwitchBuilder;withIsActiveCondition(Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$SwitchBuilder;",
                    ordinal = 1
            )
    )
    private BooleanSupplier poopsky$disableBonusChestForPoopSky(BooleanSupplier original) {
        return () -> original.getAsBoolean() && !ClientUtil.isPoopSkyWorldType(this.this$0.getUiState());
    }
}
