package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.content.FlyTypeManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class KubeJSFlyTypeReloadListener implements PreparableReloadListener {
    public static final KubeJSFlyTypeReloadListener INSTANCE = new KubeJSFlyTypeReloadListener();

    private KubeJSFlyTypeReloadListener() {
    }

    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier preparationBarrier,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor) {
        return preparationBarrier.wait(null).thenRunAsync(
                () -> FlyTypeManager.INSTANCE.replaceKubeJsDefinitions(PoFlyTypes.INSTANCE.definitions()),
                gameExecutor
        );
    }

    @Override
    public String getName() {
        return "PoopSky KubeJS Fly Types";
    }
}