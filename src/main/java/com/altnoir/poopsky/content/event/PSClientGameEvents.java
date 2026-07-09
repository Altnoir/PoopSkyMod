package com.altnoir.poopsky.content.event;

import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import com.altnoir.poopsky.util.PHooks;
import com.altnoir.poopsky.network.PlugActionPayload;
import com.altnoir.poopsky.network.PlugDismountPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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
                    var voidWorldPreset = uiState.getSettings().worldgenLoadContext().registryOrThrow(Registries.WORLD_PRESET).getHolder(PHooks.overrideDefaultWorldPreset()).orElse(null);
                    uiState.setWorldType(new WorldCreationUiState.WorldTypeEntry(voidWorldPreset));
                }
            }
        }
    }

    public static void onClientTick(ClientTickEvent.Pre event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        boolean isRidingPlug = mc.player.getVehicle() instanceof ToiletPlugEntity;

        while (PSKeyBoardInput.USE_PLUG_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new PlugActionPayload());
        }
        if (isRidingPlug && PSKeyBoardInput.DISMOUNT_PLUG_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new PlugDismountPayload());
        }
    }
}
