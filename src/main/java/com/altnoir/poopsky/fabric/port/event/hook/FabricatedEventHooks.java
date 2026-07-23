package com.altnoir.poopsky.fabric.port.event.hook;

import com.altnoir.poopsky.fabric.port.event.LevelEvents;
import com.altnoir.poopsky.fabric.port.event.entity.EntityMountEvent;
import com.altnoir.poopsky.fabric.port.event.entity.EntityTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;

public class FabricatedEventHooks {
    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting) {
        InteractionResult result = EntityMountEvent.EVENT.invoker().onEntityMount(entityMounting, entityBeingMounted, entityMounting.level(), isMounting);
        if (result == InteractionResult.PASS) {
            entityMounting.absMoveTo(entityMounting.getX(), entityMounting.getY(), entityMounting.getZ(), entityMounting.yRotO, entityMounting.xRotO);
            return false;
        } else {
            return true;
        }
    }

    public static boolean fireEntityTickPre(Entity entity) {
        InteractionResult result = EntityTickEvents.PRE.invoker().onPreTick(entity);

        if (result == InteractionResult.PASS) {
            return false;
        } else {
            return true;
        }
    }

    public static void fireEntityTickPost(Entity entity) {
        EntityTickEvents.POST.invoker().onPostTick(entity);
    }

    public static boolean onCreateWorldSpawn(Level level, ServerLevelData settings) {
        return LevelEvents.CREATE_SPAWN_POSITION.invoker().onCreateSpawnPosition(level, settings);
    }
}
