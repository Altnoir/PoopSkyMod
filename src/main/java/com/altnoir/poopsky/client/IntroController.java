package com.altnoir.poopsky.client;

import com.altnoir.poopsky.ClientConfig;
import com.altnoir.poopsky.client.screen.IntroScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class IntroController {
    private static IntroScreen activeScreen;
    private static Screen previousScreen;
    private static Runnable finishConfiguration;

    private IntroController() {
    }

    public static void start(Runnable finishConfiguration) {
        start(finishConfiguration, true);
    }

    public static void play() {
        start(() -> {
        }, false);
    }

    private static void start(Runnable finishConfiguration, boolean restorePreviousScreen) {
        if (!ClientConfig.introAnimation) {
            finishConfiguration.run();
            return;
        }
        if (activeScreen != null) return;

        Minecraft minecraft = Minecraft.getInstance();
        IntroController.previousScreen = restorePreviousScreen ? minecraft.screen : null;
        IntroController.finishConfiguration = finishConfiguration;
        activeScreen = new IntroScreen(ClientConfig.introText, ClientConfig.introYear);
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
        Minecraft.getInstance().setScreen(returnScreen);
    }

    public static void onLoggingOut() {
        clear();
    }

    public static void tick() {
        if (activeScreen != null) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
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
