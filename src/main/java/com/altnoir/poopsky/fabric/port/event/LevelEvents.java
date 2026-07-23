package com.altnoir.poopsky.fabric.port.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ServerLevelData;

public class LevelEvents {

    public static final Event<CreateSpawnPosition> CREATE_SPAWN_POSITION = EventFactory.createArrayBacked(CreateSpawnPosition.class, listeners -> (level, settings) -> {
        for (CreateSpawnPosition event : listeners) {
            return event.onCreateSpawnPosition(level, settings);
        }
        return false;
    });

    @FunctionalInterface
    public interface CreateSpawnPosition {
        boolean onCreateSpawnPosition(LevelAccessor level, ServerLevelData settings);
    }
}
