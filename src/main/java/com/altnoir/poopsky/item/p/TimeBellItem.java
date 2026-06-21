package com.altnoir.poopsky.item.p;

import com.altnoir.poopsky.network.TimeBellFreezePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimeBellItem extends Item {
    private static final int FREEZE_SECONDS = 8;
    private static volatile boolean timeBellFreeze = false;
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });
    private static volatile ScheduledFuture<?> pendingUnfreeze = null;

    public static boolean isTimeBellFreeze() {
        return timeBellFreeze;
    }

    public TimeBellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && level.getServer() != null) {
            var server = level.getServer();
            if (server.tickRateManager().isFrozen()) {
                unfreeze(server, player);
            } else {
                freeze(server, player);
            }

            player.getCooldowns().addCooldown(this, 20 * FREEZE_SECONDS + 1);
            return InteractionResultHolder.success(new ItemStack(this));
        }
        return super.use(level, player, usedHand);
    }

    private void freeze(MinecraftServer server, Player player) {
        server.tickRateManager().setFrozen(true);
        timeBellFreeze = true;

        broadcastFreezeState(server, true);

        pendingUnfreeze = SCHEDULER.schedule(() -> server.execute(() -> {
            if (server.tickRateManager().isFrozen()) {
                unfreeze(server, player);
            }
        }), FREEZE_SECONDS, TimeUnit.SECONDS);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0F, 0.5F);
        player.displayClientMessage(Component.translatable("message.poopsky.time_bell.frozen"), true);
    }

    private void unfreeze(MinecraftServer server, Player player) {
        if (pendingUnfreeze != null) {
            pendingUnfreeze.cancel(false);
            pendingUnfreeze = null;
        }
        timeBellFreeze = false;
        server.tickRateManager().setFrozen(false);

        broadcastFreezeState(server, false);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.0F, 1.5F);
        player.displayClientMessage(Component.translatable("message.poopsky.time_bell.unfrozen"), true);
    }

    private void broadcastFreezeState(MinecraftServer server, boolean frozen) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            PacketDistributor.sendToPlayer(serverPlayer, new TimeBellFreezePayload(frozen));
        }
    }
}