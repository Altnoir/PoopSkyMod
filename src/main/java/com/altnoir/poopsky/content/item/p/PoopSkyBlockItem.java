package com.altnoir.poopsky.content.item.p;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class PoopSkyBlockItem extends BlockItem {
    private static final double PLACEMENT_DISTANCE = 2.5;

    public PoopSkyBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        BlockPos target = BlockPos.containing(
                eye.x + look.x * PLACEMENT_DISTANCE,
                eye.y + look.y * PLACEMENT_DISTANCE,
                eye.z + look.z * PLACEMENT_DISTANCE
        );
        BlockState existing = level.getBlockState(target);
        if (!existing.canBeReplaced()) {
            return InteractionResult.PASS;
        }

        Direction direction = Direction.getNearest((int) Math.signum(look.x), (int) Math.signum(look.y),
                (int) Math.signum(look.z), Direction.UP);
        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(target), direction, target, false);
        BlockPlaceContext context = new BlockPlaceContext(player, hand, stack, hitResult);
        InteractionResult result = place(context);
        if (result.consumesAction() && player instanceof ServerPlayer serverPlayer) {
            SoundType soundType = level.getBlockState(target).getSoundType();
            level.playSound(null, target,
                    soundType.getPlaceSound(),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F,
                    soundType.getPitch() * 0.8F
            );
        }
        return result.consumesAction() ? InteractionResult.SUCCESS_SERVER : InteractionResult.PASS;
    }
}
