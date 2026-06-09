package com.altnoir.poopsky.event;

import com.altnoir.poopsky.event.asm.ASMHooks;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class PSClientGameEvents {
    public static Holder<WorldPreset> originalDefaultWorldPreset;

    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof CreateWorldScreen screen) {
            var uiState = screen.getUiState();
            var originalPreset = uiState.getWorldType().preset();

            if (originalPreset != null) {
                if (originalDefaultWorldPreset == null) {
                    originalDefaultWorldPreset = originalPreset;
                }
                if (originalDefaultWorldPreset.unwrapKey().equals(originalPreset.unwrapKey())) {
                    var voidWorldPreset = uiState.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder(ASMHooks.overrideDefaultWorldPreset()).orElse(null);
                    uiState.setWorldType(new WorldCreationUiState.WorldTypeEntry(voidWorldPreset));
                }
            }
        }
    }
}
