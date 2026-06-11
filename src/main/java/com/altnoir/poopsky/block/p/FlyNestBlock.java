package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.entity.FlyNestBlockEntity;
import com.altnoir.poopsky.init.PSoundEvents;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.List;

public class FlyNestBlock extends BeehiveBlock {
    public FlyNestBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static void dropFlyEggs(Level level, BlockPos pos) {
        popResource(level, pos, new ItemStack(PSItems.FLY_EGG.get(), 3));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlyNestBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        if (state.getValue(HONEY_LEVEL) < MAX_HONEY_LEVELS
                || !stack.canPerformAction(ItemAbilities.SHEARS_HARVEST)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        Item item = stack.getItem();
        level.playSound(player, player.getX(), player.getY(), player.getZ(), PSoundEvents.BLOCK_FLY_NEST_SHEAR.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        dropFlyEggs(level, pos);
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        level.gameEvent(player, GameEvent.SHEAR, pos);

        if (!level.isClientSide()) {
            player.awardStat(Stats.ITEM_USED.get(item));
        }

        if (!CampfireBlock.isSmokeyPos(level, pos)) {
            if (hiveContainsBees(level, pos)) {
                angerNearbyBees(level, pos);
            }

            this.releaseBeesAndResetHoneyLevel(level, state, pos, player, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
        } else {
            this.resetHoneyLevel(level, state, pos);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static boolean hiveContainsBees(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BeehiveBlockEntity beehive && !beehive.isEmpty();
    }

    private static void angerNearbyBees(Level level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(8.0, 6.0, 8.0);
        List<Bee> bees = level.getEntitiesOfClass(Bee.class, area);
        if (bees.isEmpty()) {
            return;
        }

        List<Player> players = level.getEntitiesOfClass(Player.class, area);
        if (players.isEmpty()) {
            return;
        }

        for (Bee bee : bees) {
            if (bee.getTarget() == null) {
                bee.setTarget(Util.getRandom(players, level.random));
            }
        }
    }
}
