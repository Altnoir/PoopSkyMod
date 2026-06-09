package com.altnoir.poopsky.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class PSKeyBoardInput {
    public static final KeyMapping USE_PLUG_KEY = new KeyMapping("key.poopsky.use_plug",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.category.poopsky");

    public static final KeyMapping DISMOUNT_PLUG_KEY = new KeyMapping("key.poopsky.dismount_plug",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.category.poopsky");

    public static Component getLocalizedKeyMessage(KeyMapping keyMapping) {
        return Component.translatableWithFallback(keyMapping.saveString(), keyMapping.getTranslatedKeyMessage().getString());
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(PSKeyBoardInput::onRegisterKeyMappings);
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(USE_PLUG_KEY);
        event.register(DISMOUNT_PLUG_KEY);
    }
}
