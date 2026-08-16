package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.event.PoKeyBoardInput;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = KeyBindsList.KeyEntry.class)
public class KeyBindsListKeyEntryMixin {
    @Redirect(method = "refreshEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"))
    private boolean po$ignoreArcadeKeyConflict(KeyMapping self, KeyMapping other) {
        boolean selfArcade = self.getKeyConflictContext() instanceof PoKeyBoardInput.ArcadeKeyConflictContext;
        boolean otherArcade = other.getKeyConflictContext() instanceof PoKeyBoardInput.ArcadeKeyConflictContext;
        if (selfArcade != otherArcade) {
            return false;
        }
        return self.same(other);
    }
}