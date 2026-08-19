package com.altnoir.poopsky.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class PoKeyBoardInput {
    public static final KeyMapping USE_PLUG_KEY = new KeyMapping("key.poopsky.use_plug",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.category.poopsky");
    public static final KeyMapping DISMOUNT_PLUG_KEY = new KeyMapping("key.poopsky.dismount_plug",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.category.poopsky");

    public static final KeyMapping ARCADE_UP = new KeyMapping("key.poopsky.arcade.up",
            ArcadeKeyConflictContext.INSTANCE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_W,
            "key.category.poopsky");
    public static final KeyMapping ARCADE_DOWN = new KeyMapping("key.poopsky.arcade.down",
            ArcadeKeyConflictContext.INSTANCE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S,
            "key.category.poopsky");
    public static final KeyMapping ARCADE_LEFT = new KeyMapping("key.poopsky.arcade.left",
            ArcadeKeyConflictContext.INSTANCE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_A,
            "key.category.poopsky");
    public static final KeyMapping ARCADE_RIGHT = new KeyMapping("key.poopsky.arcade.right",
            ArcadeKeyConflictContext.INSTANCE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_D,
            "key.category.poopsky");
    public static final KeyMapping ARCADE_BUTTON1 = new KeyMapping("key.poopsky.arcade.button1",
            ArcadeKeyConflictContext.INSTANCE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "key.category.poopsky");
    public static final KeyMapping ARCADE_BUTTON2 = new KeyMapping("key.poopsky.arcade.button2",
            ArcadeKeyConflictContext.INSTANCE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.category.poopsky");

    public static Component getLocalizedKeyMessage(KeyMapping keyMapping) {
        return Component.translatableWithFallback(keyMapping.saveString(), keyMapping.getTranslatedKeyMessage().getString());
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(USE_PLUG_KEY);
        event.register(DISMOUNT_PLUG_KEY);

        event.register(ARCADE_UP);
        event.register(ARCADE_DOWN);
        event.register(ARCADE_LEFT);
        event.register(ARCADE_RIGHT);
        event.register(ARCADE_BUTTON1);
        event.register(ARCADE_BUTTON2);
    }

    public enum ArcadeKeyConflictContext implements IKeyConflictContext {
        INSTANCE;

        @Override
        public boolean isActive() {
            return KeyConflictContext.IN_GAME.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    }
}
