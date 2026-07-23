package com.altnoir.poopsky.fabric.port.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface EntityMountEvent {
    Event<EntityMountEvent> EVENT = EventFactory.createArrayBacked(EntityMountEvent.class, listeners -> (entityMounting, entityBeingMounted, level, isMounting) -> {
        for (EntityMountEvent event : listeners) {
            InteractionResult result = event.onEntityMount(entityMounting, entityBeingMounted, level, isMounting);

            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return InteractionResult.PASS;
    });

    InteractionResult onEntityMount(Entity entityMounting, Entity entityBeingMounted, Level level, boolean isMounting);
}
