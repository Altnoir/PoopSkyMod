package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoKeyBoardInput;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyBindsList.KeyEntry.class)
public class KeyBindsListKeyEntryMixin {
    @WrapOperation(method = "refreshEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"))
    private boolean poopsky$ignoreArcadeKeyConflict(KeyMapping self, KeyMapping other, Operation<Boolean> original) {
        boolean selfArcade = self.getKeyConflictContext() instanceof PoKeyBoardInput.ArcadeKeyConflictContext;
        boolean otherArcade = other.getKeyConflictContext() instanceof PoKeyBoardInput.ArcadeKeyConflictContext;
        if (selfArcade != otherArcade) {
            return false;
        }
        return original.call(self, other);
    }
}
