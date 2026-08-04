package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.content.block.p.PortableToiletBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private void poopsky$findPortableToiletRespawnPosition(
            boolean consumeSpawnBlock,
            DimensionTransition.PostDimensionTransition postDimensionTransition,
            CallbackInfoReturnable<DimensionTransition> cir
    ) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        BlockPos spawnPos = player.getRespawnPosition();
        if (spawnPos == null) {
            return;
        }

        ServerLevel level = player.server.getLevel(player.getRespawnDimension());
        if (level == null) {
            return;
        }

        BlockState state = level.getBlockState(spawnPos);
        if (!(state.getBlock() instanceof PortableToiletBlock)
                || state.getValue(PortableToiletBlock.HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        BlockPos respawnPos = spawnPos.below().relative(state.getValue(PortableToiletBlock.FACING));
        Vec3 position = Vec3.atBottomCenterOf(respawnPos);
        float yaw = (float) Math.toDegrees(Math.atan2(
                spawnPos.getZ() + 0.5D - position.z,
                spawnPos.getX() + 0.5D - position.x
        )) - 90.0F;
        cir.setReturnValue(new DimensionTransition(
                level,
                position,
                Vec3.ZERO,
                yaw,
                0.0F,
                postDimensionTransition
        ));
    }
}
