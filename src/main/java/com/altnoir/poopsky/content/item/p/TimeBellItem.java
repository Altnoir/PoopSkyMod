package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.altnoir.poopsky.impl.network.TimeBellFreezePayload;
import com.altnoir.poopsky.init.PoEffects;
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
import com.altnoir.poopsky.impl.network.PoNetworking;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TimeBellItem extends Item {
    private static final int DELAY_TICKS = 20;
    private static final int FREEZE_TICKS = 160;
    private static final Map<UUID, PendingAction> PENDING = new ConcurrentHashMap<>();
    private record SavedState(boolean mayfly, boolean flying, boolean noGravity, double speed) {}
    private static final Map<UUID, SavedState> SAVED_ABILITIES = new ConcurrentHashMap<>();
    private static UUID freezeOwner;
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
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) return;
        if (level.isClientSide) return;

        int usedTicks = this.getUseDuration(stack) - remainingUseDuration;
        if (usedTicks != DELAY_TICKS) return;

        MinecraftServer server = level.getServer();
        if (server == null) return;

        boolean unfreezing = com.altnoir.poopsky.impl.PoTimeFreeze.serverFrozen();
        if (unfreezing) {
            player.removeEffect(PoEffects.MOMENT_OF_PTYME.get());
            executeUnfreeze(server, player);
        } else {
            executeFreeze(server, player);
        }

        player.getCooldowns().addCooldown(this, FREEZE_TICKS + 1);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player)) return;

        int usedTicks = this.getUseDuration(stack) - timeLeft;

        if (level.isClientSide && usedTicks < DELAY_TICKS) {
            Minecraft.getInstance().getSoundManager().stop(PoSoundEvents.ITEM_TIME_BELL_OPEN.get().getLocation(), SoundSource.PLAYERS);
        }
    }

    private static void executeFreeze(MinecraftServer server, Player player) {
        timeBellFreeze = true;
        com.altnoir.poopsky.impl.PoTimeFreeze.setServerFrozen(true);
        broadcastFreezeState(server, true);

        UUID userId = player.getUUID();
        freezeOwner = userId;
        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            var abilities = sp.getAbilities();
            SAVED_ABILITIES.put(sp.getUUID(), new SavedState(abilities.mayfly, abilities.flying, sp.isNoGravity(), sp.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue()));

            if (sp.getUUID().equals(userId)) {
                abilities.mayfly = true;
                abilities.flying = true;
            } else {
                abilities.mayfly = false;
                abilities.flying = false;
                sp.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
                FROZEN_POS.put(sp.getUUID(), sp.position());
                sp.setNoGravity(true);
                sp.setDeltaMovement(0, 0, 0);
            }
            sp.onUpdateAbilities();
        }

        if (!Config.unlimitedFreeze) {
            player.addEffect(new MobEffectInstance(PoEffects.MOMENT_OF_PTYME.get(), FREEZE_TICKS, 0, false, false));
        }
        player.displayClientMessage(Component.translatable("message.poopsky.time_bell.frozen"), true);
    }

    static void executeUnfreeze(MinecraftServer server, Player player) {
        timeBellFreeze = false;
        com.altnoir.poopsky.impl.PoTimeFreeze.setServerFrozen(false);

        for (ServerPlayer sp : server.getPlayerList().getPlayers()) restore(sp);
        FROZEN_POS.clear();
        SAVED_ABILITIES.clear();
        freezeOwner = null;

        broadcastFreezeState(server, false);
        if (player != null) {
            player.level().playSound(null, player, PoSoundEvents.ITEM_TIME_BELL_CLOSE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            player.displayClientMessage(Component.translatable("message.poopsky.time_bell.unfrozen"), true);
        }
    }

    public static void unfreeze(MinecraftServer server, Player player) {
        executeUnfreeze(server, player);
    }

    private static void restore(ServerPlayer player) {
        SavedState saved = SAVED_ABILITIES.remove(player.getUUID());
        if (saved != null) {
            player.getAbilities().mayfly = saved.mayfly();
            player.getAbilities().flying = saved.flying();
            player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(saved.speed());
            player.setNoGravity(saved.noGravity());
            player.onUpdateAbilities();
        }
        FROZEN_POS.remove(player.getUUID());
    }

    public static void playerLeft(ServerPlayer player) {
        if (player.getUUID().equals(freezeOwner)) executeUnfreeze(player.server, player);
        else restore(player);
        PENDING.remove(player.getUUID());
    }

    public static void playerJoined(ServerPlayer player) {
        PoNetworking.sendToPlayer(player, new TimeBellFreezePayload(timeBellFreeze));
        if (!timeBellFreeze) return;
        var abilities = player.getAbilities();
        SAVED_ABILITIES.put(player.getUUID(), new SavedState(abilities.mayfly, abilities.flying, player.isNoGravity(), player.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue()));
        abilities.mayfly = false;
        abilities.flying = false;
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
        FROZEN_POS.put(player.getUUID(), player.position());
        player.setNoGravity(true);
        player.onUpdateAbilities();
    }

    public static void serverStopping(MinecraftServer server) {
        if (timeBellFreeze) executeUnfreeze(server, null);
        PENDING.clear(); SAVED_ABILITIES.clear(); FROZEN_POS.clear(); freezeOwner = null;
    }

    public static void freezeTick(MinecraftServer server) {
        if (!timeBellFreeze) return;
        ServerPlayer owner = freezeOwner == null ? null : server.getPlayerList().getPlayer(freezeOwner);
        if (owner == null || !owner.isAlive() || (!Config.unlimitedFreeze && !owner.hasEffect(PoEffects.MOMENT_OF_PTYME.get()))) {
            executeUnfreeze(server, owner);
            return;
        }
        if (FROZEN_POS.isEmpty()) return;
        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            Vec3 pos = FROZEN_POS.get(sp.getUUID());
            if (pos != null) {
                sp.teleportTo(pos.x, pos.y, pos.z);
                sp.setDeltaMovement(0, 0, 0);
                sp.setNoGravity(true);
                sp.resetFallDistance();
            }
        }
    }

    private record PendingAction(UUID playerId, boolean freeze, int remaining) {
    }

    private static void broadcastFreezeState(MinecraftServer server, boolean frozen) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            PoNetworking.sendToPlayer(serverPlayer, new TimeBellFreezePayload(frozen));
        }
    }

    public static void tickPending(MinecraftServer server) {
        Iterator<Map.Entry<UUID, PendingAction>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            var action = entry.getValue();
            int next = action.remaining() - 1;
            if (next <= 0) {
                it.remove();
                ServerPlayer sp = server.getPlayerList().getPlayer(action.playerId());
                if (sp == null) continue;
                if (action.freeze()) {
                    executeFreeze(server, sp);
                } else {
                    executeUnfreeze(server, sp);
                }
            } else {
                entry.setValue(new PendingAction(action.playerId(), action.freeze(), next));
            }
        }
    }
}
