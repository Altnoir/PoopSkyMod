package com.altnoir.poopsky.client;

import com.altnoir.poopsky.ClientConfig;
import com.altnoir.poopsky.client.screen.PoemScreen;
import com.altnoir.poopsky.impl.network.PoAnimation;
import net.minecraft.client.Minecraft;

public final class PoAnimationController {
    private static PoemScreen poemScreen;
    private static Runnable poemFinished;

    private PoAnimationController() {
    }

    public static void play(PoAnimation animation) {
        play(animation, () -> {
        });
    }

    public static void play(PoAnimation animation, Runnable finished) {
        switch (animation) {
            case INTRO -> IntroController.play();
            case POEM -> {
                if (ClientConfig.endAnimation) {
                    playPoem(finished);
                } else {
                    finished.run();
                }
            }
        }
    }

    public static void finish(PoemScreen screen) {
        if (poemScreen != screen) return;

        Runnable finished = poemFinished;
        poemScreen = null;
        poemFinished = null;
        Minecraft.getInstance().setScreen(null);
        finished.run();
    }

    public static void onScreenClosed(PoemScreen screen) {
        if (poemScreen == screen) {
            poemScreen = null;
            poemFinished = null;
        }
    }

    public static void onLoggingOut() {
        poemScreen = null;
        poemFinished = null;
    }

    private static void playPoem(Runnable finished) {
        Minecraft minecraft = Minecraft.getInstance();
        if (poemScreen != null) {
            Runnable previousFinished = poemFinished;
            poemFinished = () -> {
                previousFinished.run();
                finished.run();
            };
            return;
        }

        poemFinished = finished;
        poemScreen = new PoemScreen();
        minecraft.setScreen(poemScreen);
    }
}
