package com.altnoir.poopsky.client;

import com.altnoir.poopsky.client.screen.IntroScreen;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.impl.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionTransitionScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;

import java.util.function.BooleanSupplier;

public final class IntroController {
    private static final BooleanSupplier NOT_READY = () -> false;

    private static IntroScreen activeScreen;
    private static BooleanSupplier levelReady = NOT_READY;
    private static Runnable finishConfiguration;
    private static boolean clientRuntimeReady = !PoMods.JEI.isLoaded();
    private static boolean animationComplete;
    private static boolean configurationReleased;
    private static boolean awaitingWorldCreation;
    private static boolean ignoreNextLogout;

    private IntroController() {
    }

    public static void registerTransitionScreen(RegisterDimensionTransitionScreenEvent event) {
        event.registerConditionalEffect(null, null, IntroReceivingScreen::new);
    }

    public static void trackLevelReady(BooleanSupplier levelReady) {
        IntroController.levelReady = levelReady;
    }

    public static void start(Runnable finishConfiguration) {
        if (activeScreen == null) {
            prepareIntro();
            activeScreen = new IntroScreen();
            Minecraft.getInstance().setScreen(activeScreen);
        }

        IntroController.finishConfiguration = finishConfiguration;
        releaseConfigurationIfReady();
    }

    public static void onJeiRuntimeAvailable() {
        clientRuntimeReady = true;
    }

    public static void onJeiRuntimeUnavailable() {
        clientRuntimeReady = false;
    }

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (activeScreen == null) {
            if (event.getCurrentScreen() instanceof CreateWorldScreen createWorldScreen
                    && ClientUtil.isPoopSkyWorldType(createWorldScreen.getUiState())) {
                if (event.getNewScreen() instanceof GenericMessageScreen) {
                    startForWorldCreation(event);
                    return;
                }
                awaitingWorldCreation = event.getNewScreen() instanceof ConfirmScreen;
            } else if (awaitingWorldCreation && event.getNewScreen() instanceof GenericMessageScreen) {
                startForWorldCreation(event);
                return;
            } else if (event.getNewScreen() instanceof CreateWorldScreen) {
                awaitingWorldCreation = false;
            }
        }

        if (activeScreen == null) return;

        if (event.getNewScreen() instanceof IntroReceivingScreen) {
            activeScreen.resumePlaybackSound();
            event.setCanceled(true);
        } else if (event.getNewScreen() instanceof ProgressScreen) {
            activeScreen.resumePlaybackSound();
            event.setCanceled(true);
        } else if (event.getNewScreen() instanceof LevelLoadingScreen) {
            event.setCanceled(true);
        }
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        if (ignoreNextLogout) {
            ignoreNextLogout = false;
            if (activeScreen != null) {
                activeScreen.resumePlaybackSound();
            }
            return;
        }

        if (activeScreen != null) {
            activeScreen.abort();
            activeScreen = null;
        }
        awaitingWorldCreation = false;
        levelReady = NOT_READY;
        finishConfiguration = null;
    }

    public static void onSelectMusic(SelectMusicEvent event) {
        if (activeScreen != null) {
            event.overrideMusic(null);
        }
    }

    public static void onScreenClosed(IntroScreen screen) {
        if (activeScreen == screen) {
            activeScreen = null;
            awaitingWorldCreation = false;
            ignoreNextLogout = false;
            levelReady = NOT_READY;
            finishConfiguration = null;
            animationComplete = false;
            configurationReleased = false;
        }
    }

    public static boolean isLevelReady() {
        return activeScreen != null && levelReady.getAsBoolean();
    }

    public static boolean isReadyToFinish() {
        return isLevelReady() && clientRuntimeReady;
    }

    public static void onAnimationComplete() {
        animationComplete = true;
        releaseConfigurationIfReady();
    }

    public static int getLoadingProgress() {
        if (isLevelReady()) return clientRuntimeReady ? 100 : 99;

        var progressListener = Minecraft.getInstance().getProgressListener();
        if (progressListener == null) {
            return 0;
        }
        return Math.min(progressListener.getProgress(), 99);
    }

    private static void startForWorldCreation(ScreenEvent.Opening event) {
        awaitingWorldCreation = false;
        ignoreNextLogout = true;
        prepareIntro();
        activeScreen = new IntroScreen();
        event.setNewScreen(activeScreen);
    }

    private static void prepareIntro() {
        levelReady = NOT_READY;
        finishConfiguration = null;
        animationComplete = false;
        configurationReleased = false;
        clientRuntimeReady = !PoMods.JEI.isLoaded();
    }

    private static void releaseConfigurationIfReady() {
        if (!animationComplete || configurationReleased || finishConfiguration == null) return;

        configurationReleased = true;
        Runnable finish = finishConfiguration;
        finishConfiguration = null;
        finish.run();
    }

    private static final class IntroReceivingScreen extends ReceivingLevelScreen {
        private IntroReceivingScreen(BooleanSupplier levelReceived, Reason reason) {
            super(levelReceived, reason);
            IntroController.trackLevelReady(levelReceived);
        }
    }
}
