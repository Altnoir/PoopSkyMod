package com.altnoir.poopsky.fabric.port.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;

public class EntityTickEvents {

    public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, listeners -> entity -> {
        for (Pre event : listeners) {
            InteractionResult result = event.onPreTick(entity);

            if (result == InteractionResult.SUCCESS) {
                return result;
            }
        }
        return InteractionResult.PASS;
    });

    public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class, listeners -> entity -> {
        for (Post event : listeners) {
            event.onPostTick(entity);
        }
    });

    @FunctionalInterface
    public interface Pre {
        InteractionResult onPreTick(Entity entity);
    }

    @FunctionalInterface
    public interface Post {
        void onPostTick(Entity entity);
    }
}
