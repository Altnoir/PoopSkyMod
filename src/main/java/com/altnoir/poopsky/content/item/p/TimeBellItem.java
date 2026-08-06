package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.altnoir.poopsky.impl.network.TimeBellFreezePayload;
import com.altnoir.poopsky.init.PoEffects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TimeBellItem extends Item {
    private static final int DELAY_TICKS = 20;
    private static final int FREEZE_TICKS = 160;
    private static final Map<UUID, PendingAction> PENDING = new ConcurrentHashMap<>();
    private static final Map<UUID, boolean[]> SAVED_ABILITIES = new ConcurrentHashMap<>();
    private static final Map<UUID, Vec3> FROZEN_POS = new ConcurrentHashMap<>();
    private static volatile boolean timeBellFreeze = false;

    public static boolean isTimeBellFreeze() {
        return timeBellFreeze;
    }

    public TimeBellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        player.level().playSound(null, player, PoSoundEvents.ITEM_TIME_BELL_OPEN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player) || level.isClientSide) return;

        int usedTicks = this.getUseDuration(stack, livingEntity) - remainingUseDuration;
        if (usedTicks != DELAY_TICKS) return;

        MinecraftServer server = level.getServer();
        if (server == null) return;

        if (server.tickRateManager().isFrozen()) {
            player.removeEffect(PoEffects.holder(PoEffects.MOMENT_OF_PTYME));
            executeUnfreeze(server, player);
        } else {
            executeFreeze(server, player);
        }
        player.getCooldowns().addCooldown(this, FREEZE_TICKS + 1);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player) || !level.isClientSide) return;

        int usedTicks = this.getUseDuration(stack, livingEntity) - timeLeft;
        if (usedTicks < DELAY_TICKS) {
            Minecraft.getInstance().getSoundManager().stop(
                    PoSoundEvents.ITEM_TIME_BELL_OPEN.get().getLocation(), SoundSource.PLAYERS);
        }
    }

    private static void executeFreeze(MinecraftServer server, Player player) {
        timeBellFreeze = true;
        server.tickRateManager().setFrozen(true);
        broadcastFreezeState(server, true);

        UUID userId = player.getUUID();
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            var abilities = serverPlayer.getAbilities();
            SAVED_ABILITIES.put(serverPlayer.getUUID(), new boolean[]{abilities.mayfly, abilities.flying});

            if (serverPlayer.getUUID().equals(userId)) {
                abilities.mayfly = true;
                abilities.flying = true;
            } else {
                abilities.mayfly = false;
                abilities.flying = false;
                serverPlayer.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
                FROZEN_POS.put(serverPlayer.getUUID(), serverPlayer.position());
                serverPlayer.setNoGravity(true);
                serverPlayer.setDeltaMovement(0, 0, 0);
            }
            serverPlayer.onUpdateAbilities();
        }

        if (!Config.unlimitedFreeze) {
            player.addEffect(new MobEffectInstance(
                    PoEffects.holder(PoEffects.MOMENT_OF_PTYME), FREEZE_TICKS, 0, false, false));
        }
        player.displayClientMessage(Component.translatable("message.poopsky.time_bell.frozen"), true);
    }

    static void executeUnfreeze(MinecraftServer server, Player player) {
        timeBellFreeze = false;
        server.tickRateManager().setFrozen(false);

        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            boolean[] saved = SAVED_ABILITIES.remove(serverPlayer.getUUID());
            if (saved != null) {
                serverPlayer.getAbilities().mayfly = saved[0];
                serverPlayer.getAbilities().flying = saved[1];
                serverPlayer.onUpdateAbilities();
            }
            serverPlayer.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
            serverPlayer.setNoGravity(false);
        }
        FROZEN_POS.clear();

        broadcastFreezeState(server, false);
        player.level().playSound(null, player, PoSoundEvents.ITEM_TIME_BELL_CLOSE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.displayClientMessage(Component.translatable("message.poopsky.time_bell.unfrozen"), true);
    }

    public static void unfreeze(MinecraftServer server, Player player) {
        executeUnfreeze(server, player);
    }

    public static void freezeTick(MinecraftServer server) {
        if (!timeBellFreeze || FROZEN_POS.isEmpty()) return;
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            Vec3 pos = FROZEN_POS.get(serverPlayer.getUUID());
            if (pos != null) {
                serverPlayer.teleportTo(pos.x, pos.y, pos.z);
                serverPlayer.setDeltaMovement(0, 0, 0);
                serverPlayer.setNoGravity(true);
                serverPlayer.resetFallDistance();
            }
        }
    }

    private record PendingAction(UUID playerId, boolean freeze, int remaining) {
    }

    private static void broadcastFreezeState(MinecraftServer server, boolean frozen) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(serverPlayer, new TimeBellFreezePayload(frozen));
        }
    }

    public static void tickPending(MinecraftServer server) {
        Iterator<Map.Entry<UUID, PendingAction>> iterator = PENDING.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var action = entry.getValue();
            int next = action.remaining() - 1;
            if (next <= 0) {
                iterator.remove();
                ServerPlayer serverPlayer = server.getPlayerList().getPlayer(action.playerId());
                if (serverPlayer == null) continue;
                if (action.freeze()) {
                    executeFreeze(server, serverPlayer);
                } else {
                    executeUnfreeze(server, serverPlayer);
                }
            } else {
                entry.setValue(new PendingAction(action.playerId(), action.freeze(), next));
            }
        }
    }
}
