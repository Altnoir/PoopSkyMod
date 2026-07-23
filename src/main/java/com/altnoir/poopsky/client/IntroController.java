package com.altnoir.poopsky.client;

import com.altnoir.poopsky.client.screen.IntroScreen;
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

    public static void start() {
        if (session == null) {
            session = createSession(false);
            Minecraft.getInstance().setScreen(session.screen);
        }
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

        IntroSession activeSession = session;
        if (activeSession == null) return;

        if (event.getNewScreen() instanceof IntroReceivingScreen
                || event.getNewScreen() instanceof ProgressScreen) {
            activeSession.screen.resumePlaybackSound();
            event.setCanceled(true);
        } else if (event.getNewScreen() instanceof LevelLoadingScreen) {
            event.setCanceled(true);
        }
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        IntroSession activeSession = session;
        if (activeSession != null && activeSession.ignoreNextLogout) {
            activeSession.ignoreNextLogout = false;
            activeSession.screen.resumePlaybackSound();
            return;
        }

        if (activeSession != null) {
            activeSession.screen.abort();
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
        IntroSession activeSession = session;
        if (activeSession != null && activeSession.screen == screen) {
            session = null;
            awaitingWorldCreation = false;
        }
    }

    public static boolean isReadyToFinish() {
        IntroSession activeSession = session;
        return activeSession != null
                && (Minecraft.getInstance().level != null || activeSession.levelReady.getAsBoolean());
    }

    public static int getLoadingProgress() {
        if (isReadyToFinish()) return 100;

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
        return new IntroSession(new IntroScreen(), ignoreNextLogout);
    }

    private static final class IntroSession {
        private final IntroScreen screen;
        private BooleanSupplier levelReady = NOT_READY;
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
