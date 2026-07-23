package com.altnoir.poopsky.impl.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
//import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
//import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class PSKeyBoardInput {
    public static final KeyMapping USE_PLUG_KEY = new KeyMapping(
            "key.poopsky.use_plug",
            //KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.category.poopsky");

    public static final KeyMapping DISMOUNT_PLUG_KEY = new KeyMapping(
            "key.poopsky.dismount_plug",
            //KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.category.poopsky");

    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        KeyBindingHelper.registerKeyBinding(USE_PLUG_KEY);
        KeyBindingHelper.registerKeyBinding(DISMOUNT_PLUG_KEY);
    }

    public static Component getLocalizedKeyMessage(KeyMapping keyMapping) {
        return Component.translatableWithFallback(keyMapping.saveString(), keyMapping.getTranslatedKeyMessage().getString());
    }

}
