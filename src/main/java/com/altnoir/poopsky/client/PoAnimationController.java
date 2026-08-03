package com.altnoir.poopsky.client;

import com.altnoir.poopsky.client.screen.PoemScreen;
import com.altnoir.poopsky.impl.network.PoAnimation;
import net.minecraft.client.Minecraft;

public final class PoAnimationController {
    private static PoemScreen poemScreen;

    private PoAnimationController() {
    }

    public static void play(PoAnimation animation) {
        switch (animation) {
            case INTRO -> IntroController.play();
            case POEM -> playPoem();
        }
    }

    public static void finish(PoemScreen screen) {
        if (poemScreen != screen) return;

        poemScreen = null;
        Minecraft.getInstance().setScreen(null);
    }

    public static void onScreenClosed(PoemScreen screen) {
        if (poemScreen == screen) {
            poemScreen = null;
        }
    }

    public static void onLoggingOut() {
        poemScreen = null;
    }

    private static void playPoem() {
        Minecraft minecraft = Minecraft.getInstance();
        if (poemScreen != null) return;

        poemScreen = new PoemScreen();
        minecraft.setScreen(poemScreen);
    }
}
