package com.altnoir.poopsky.client;

import com.altnoir.poopsky.client.screen.IntroScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;

public final class IntroController {
    private static IntroScreen activeScreen;
    private static Screen previousScreen;
    private static Runnable finishConfiguration;

    private IntroController() {
    }

    public static void start(Runnable finishConfiguration) {
        if (activeScreen != null) return;

        Minecraft minecraft = Minecraft.getInstance();
        IntroController.previousScreen = minecraft.screen;
        IntroController.finishConfiguration = finishConfiguration;
        activeScreen = new IntroScreen();
        minecraft.setScreen(activeScreen);
    }

    public static void finish(IntroScreen screen) {
        if (activeScreen != screen) return;

        Screen returnScreen = previousScreen;
        Runnable finish = finishConfiguration;
        activeScreen = null;
        previousScreen = null;
        finishConfiguration = null;

        finish.run();
        if (returnScreen != null) {
            Minecraft.getInstance().setScreen(returnScreen);
        }
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
    }

    public static void onSelectMusic(SelectMusicEvent event) {
        if (activeScreen != null) {
            event.overrideMusic(null);
        }
    }

    public static void onScreenClosed(IntroScreen screen) {
        if (activeScreen == screen) {
            clear();
        }
    }

    private static void clear() {
        if (activeScreen != null) {
            activeScreen.abort();
        }
        activeScreen = null;
        previousScreen = null;
        finishConfiguration = null;
    }
}
