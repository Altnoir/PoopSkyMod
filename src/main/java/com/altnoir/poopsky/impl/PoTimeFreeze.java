package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID)
public final class PoTimeFreeze {
    private static volatile boolean serverFrozen;
    private static volatile boolean clientFrozen;
    private PoTimeFreeze() {}
    public static boolean serverFrozen() { return serverFrozen; }
    public static void setServerFrozen(boolean value) { serverFrozen = value; }
    public static void setClientFrozen(boolean value) { clientFrozen = value; }
    public static boolean frozen(Level level) { return level.isClientSide ? clientFrozen : serverFrozen; }
    public static boolean skipEntity(Entity entity) {
        if (!frozen(entity.level()) || entity instanceof Player) return false;
        for (Entity passenger : entity.getIndirectPassengers()) if (passenger instanceof Player) return false;
        return true;
    }
    @SubscribeEvent public static void stopping(net.minecraftforge.event.server.ServerStoppingEvent event) {
        com.altnoir.poopsky.content.item.p.TimeBellItem.serverStopping(event.getServer());
    }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) { serverFrozen = false; }
}
