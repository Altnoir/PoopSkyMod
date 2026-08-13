package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.PoopSky;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class PSKeyBoardInput {
    private static final KeyMapping.Category POOPSKY_CATEGORY = KeyMapping.Category.register(PoopSky.loc("poopsky"));

    public static final KeyMapping USE_PLUG_KEY = new KeyMapping("key.poopsky.use_plug",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            POOPSKY_CATEGORY);

    public static final KeyMapping DISMOUNT_PLUG_KEY = new KeyMapping("key.poopsky.dismount_plug",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            POOPSKY_CATEGORY);

    public static Component getLocalizedKeyMessage(KeyMapping keyMapping) {
        return Component.translatableWithFallback(keyMapping.saveString(), keyMapping.getTranslatedKeyMessage().getString());
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(POOPSKY_CATEGORY);
        event.register(USE_PLUG_KEY);
        event.register(DISMOUNT_PLUG_KEY);
    }
}
