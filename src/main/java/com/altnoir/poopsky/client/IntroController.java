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

    private static IntroSession session;
    private static boolean clientRuntimeReady = !PoMods.JEI.isLoaded();
    private static boolean awaitingWorldCreation;

    private IntroController() {
    }

    public static void registerTransitionScreen(RegisterDimensionTransitionScreenEvent event) {
        event.registerConditionalEffect(null, null, IntroReceivingScreen::new);
    }

    public static void trackLevelReady(BooleanSupplier levelReady) {
        if (session != null) {
            session.levelReady = levelReady;
        }
    }

    public static void start(Runnable finishConfiguration) {
        if (session == null) {
            session = createSession(false);
            Minecraft.getInstance().setScreen(session.screen);
        }

        session.finishConfiguration = finishConfiguration;
        releaseConfigurationIfReady();
    }

    public static void onJeiRuntimeAvailable() {
        clientRuntimeReady = true;
    }

    public static void onJeiRuntimeUnavailable() {
        clientRuntimeReady = false;
    }

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (session == null) {
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

        if (session == null) return;

        if (event.getNewScreen() instanceof IntroReceivingScreen) {
            session.screen.resumePlaybackSound();
            event.setCanceled(true);
        } else if (event.getNewScreen() instanceof ProgressScreen) {
            session.screen.resumePlaybackSound();
            event.setCanceled(true);
        } else if (event.getNewScreen() instanceof LevelLoadingScreen) {
            event.setCanceled(true);
        }
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        if (session != null && session.ignoreNextLogout) {
            session.ignoreNextLogout = false;
            session.screen.resumePlaybackSound();
            return;
        }

        if (session != null) {
            session.screen.abort();
            session = null;
        }
        awaitingWorldCreation = false;
    }

    public static void onSelectMusic(SelectMusicEvent event) {
        if (session != null) {
            event.overrideMusic(null);
        }
    }

    public static void onScreenClosed(IntroScreen screen) {
        if (session != null && session.screen == screen) {
            session = null;
            awaitingWorldCreation = false;
        }
    }

    public static boolean isLevelReady() {
        return session != null && session.levelReady.getAsBoolean();
    }

    public static boolean isReadyToFinish() {
        return isLevelReady() && clientRuntimeReady;
    }

    public static void onAnimationComplete() {
        if (session != null) {
            session.animationComplete = true;
            releaseConfigurationIfReady();
        }
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
        session = createSession(true);
        event.setNewScreen(session.screen);
    }

    private static IntroSession createSession(boolean ignoreNextLogout) {
        clientRuntimeReady = !PoMods.JEI.isLoaded();
        return new IntroSession(new IntroScreen(), ignoreNextLogout);
    }

    private static void releaseConfigurationIfReady() {
        if (session == null || !session.animationComplete || session.finishConfiguration == null) return;

        Runnable finish = session.finishConfiguration;
        session.finishConfiguration = null;
        finish.run();
    }

    private static final class IntroSession {
        private final IntroScreen screen;
        private BooleanSupplier levelReady = NOT_READY;
        private Runnable finishConfiguration;
        private boolean animationComplete;
        private boolean ignoreNextLogout;

        private IntroSession(IntroScreen screen, boolean ignoreNextLogout) {
            this.screen = screen;
            this.ignoreNextLogout = ignoreNextLogout;
        }
    }

    private static final class IntroReceivingScreen extends ReceivingLevelScreen {
        private IntroReceivingScreen(BooleanSupplier levelReceived, Reason reason) {
            super(levelReceived, reason);
            IntroController.trackLevelReady(levelReceived);
        }
    }
}
