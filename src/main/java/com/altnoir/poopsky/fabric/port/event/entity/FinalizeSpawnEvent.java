package com.altnoir.poopsky.fabric.port.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public interface FinalizeSpawnEvent {

    Event<FinalizeSpawnEvent> EVENT = EventFactory.createArrayBacked(FinalizeSpawnEvent.class, listeners -> (entity, level, difficulty, spawnType, spawnGroupData) -> {
        for (FinalizeSpawnEvent event : listeners) {
            event.onAfterFinalizeSpawn(entity, level, difficulty, spawnType, spawnGroupData);
        }
    });

    void onAfterFinalizeSpawn(Entity entity, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData);
}
