package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.p.PortableToiletBlock;
import com.altnoir.poopsky.impl.network.ReturnTotemActivationPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class ReturnTotemItem extends Item {
    public ReturnTotemItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel targetLevel = serverPlayer.server.getLevel(serverPlayer.getRespawnDimension());
        if (targetLevel == null) {
            targetLevel = serverPlayer.server.overworld();
        }

        BlockPos respawn = serverPlayer.getRespawnPosition();
        if (respawn == null || !(targetLevel.getBlockState(respawn).getBlock() instanceof PortableToiletBlock)) {
            player.displayClientMessage(Component.translatable("message.poopsky.return_totem.not_bound"), true);
            return InteractionResultHolder.pass(stack);
        }

        Optional<ServerPlayer.RespawnPosAngle> respawnInfo = targetLevel.getBlockState(respawn).getRespawnPosition(EntityType.PLAYER, targetLevel, respawn, serverPlayer.getRespawnAngle());
        if (respawnInfo.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.poopsky.return_totem.obstructed"), true);
            return InteractionResultHolder.pass(stack);
        }

        ItemStack activationStack = stack.copy();
        stack.shrink(1);
        PacketDistributor.sendToPlayer(serverPlayer, new ReturnTotemActivationPayload(activationStack));

        ServerPlayer.RespawnPosAngle angle = respawnInfo.get();
        Vec3 destination = angle.position();
        float yaw = angle.yaw();
        Vec3 origin = serverPlayer.position();
        spawnReturnTotemParticles(serverPlayer.serverLevel(), origin);

        serverPlayer.setForcedPose(Pose.SWIMMING);
        serverPlayer.setPose(Pose.SWIMMING);
        serverPlayer.teleportTo(
                targetLevel,
                destination.x,
                destination.y - 0.6,
                destination.z,
                yaw,
                0.0F
        );
        serverPlayer.setForcedPose(null);
        serverPlayer.resetFallDistance();
        targetLevel.playSound(
                null,
                serverPlayer.getX(),
                serverPlayer.getY(),
                serverPlayer.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
        spawnReturnTotemParticles(targetLevel, serverPlayer.position());
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    private static void spawnReturnTotemParticles(ServerLevel level, Vec3 pos) {
        level.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                pos.x, pos.y + 1.0, pos.z,
                30,
                0.5, 0.5, 0.5,
                0.0
        );
    }
}
