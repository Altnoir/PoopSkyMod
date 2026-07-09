package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.network.TimeBellFreezePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class TimeBellItem extends Item {
    private static final int FREEZE_TICKS = 160;
    private static volatile boolean timeBellFreeze = false;

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
                player.removeEffect(PEffects.MOMENT_OF_PTYME);
                unfreeze(server, player);
            } else {
                freeze(server, player);
            }

            player.getCooldowns().addCooldown(this, FREEZE_TICKS + 1);
            return InteractionResultHolder.success(new ItemStack(this));
        }
        return super.use(level, player, usedHand);
    }

    private void freeze(MinecraftServer server, Player player) {
        timeBellFreeze = true;
        server.tickRateManager().setFrozen(true);
        broadcastFreezeState(server, true);

        if (!Config.unlimitedFreeze) {
            player.addEffect(new MobEffectInstance(PEffects.MOMENT_OF_PTYME, FREEZE_TICKS, 0, false, false));
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0F, 0.5F);
        player.displayClientMessage(Component.translatable("message.poopsky.time_bell.frozen"), true);
    }

    public static void unfreeze(MinecraftServer server, Player player) {
        timeBellFreeze = false;
        server.tickRateManager().setFrozen(false);
        broadcastFreezeState(server, false);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.0F, 1.5F);
        player.displayClientMessage(Component.translatable("message.poopsky.time_bell.unfrozen"), true);
    }

    private static void broadcastFreezeState(MinecraftServer server, boolean frozen) {
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            PacketDistributor.sendToPlayer(serverPlayer, new TimeBellFreezePayload(frozen));
        }
    }
}