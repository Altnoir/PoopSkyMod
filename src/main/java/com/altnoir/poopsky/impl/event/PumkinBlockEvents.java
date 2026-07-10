package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

import java.util.function.Predicate;

public class PumkinBlockEvents {
    public enum GolemType {
        VILLAGER,
        BLAZE,
        FLY
    }

    public record SpawnResult(GolemType type, Entity entity, BlockPattern.BlockPatternMatch match, BlockPos spawnPos) {
    }

    public static SpawnResult trySpawn(Level level, BlockPos pos, Predicate<BlockState> predicate) {
        BlockPattern.BlockPatternMatch match;

        match = getVillager(predicate).find(level, pos);
        if (match != null) {
            return createVillager(level, match);
        }
        match = getBlaze(predicate).find(level, pos);
        if (match != null) {
            return createBlaze(level, match);
        }
        match = getFly(predicate).find(level, pos);
        if (match != null) {
            return createFly(level, match);
        }
        return null;
    }

    private static SpawnResult createVillager(Level level, BlockPattern.BlockPatternMatch match) {
        Villager villager = EntityType.VILLAGER.create(level);
        if (villager != null) {
            villager.setBaby(true);
        }
        return new SpawnResult(GolemType.VILLAGER, villager, match, match.getBlock(0, 1, 0).getPos());
    }

    private static SpawnResult createBlaze(Level level, BlockPattern.BlockPatternMatch match) {
        Blaze blaze = EntityType.BLAZE.create(level);
        if (blaze != null) {
            blaze.getAttribute(Attributes.SCALE).setBaseValue(0.5);
            blaze.targetSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof NearestAttackableTargetGoal);
            blaze.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(blaze, Mob.class, 10, true, false,
                    livingEntity -> livingEntity instanceof Enemy && !(livingEntity instanceof Blaze)));
        }
        return new SpawnResult(GolemType.BLAZE, blaze, match, match.getBlock(0, 1, 0).getPos());
    }

    private static SpawnResult createFly(Level level, BlockPattern.BlockPatternMatch match) {
        FlyEntity fly = PEntityType.FLY.get().create(level);
        return new SpawnResult(GolemType.FLY, fly, match, match.getBlock(0, 1, 0).getPos());
    }

    public static boolean canSpawnCustomGolem(LevelReader level, BlockPos pos) {
        return getVillagerDispenser().find(level, pos) != null
                || getBlazeDispenser().find(level, pos) != null
                || getFlyDispenser().find(level, pos) != null;
    }


    private static BlockPattern getVillager(Predicate<BlockState> predicate) {
        return BlockPatternBuilder.start()
                .aisle("^", "#")
                .where('^', BlockInWorld.hasState(predicate))
                .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.POOP_BLOCK.get())))
                .build();
    }

    private static BlockPattern getVillagerDispenser() {
        return BlockPatternBuilder.start()
                .aisle(" ", "#")
                .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.POOP_BLOCK.get())))
                .build();
    }

    private static BlockPattern getBlaze(Predicate<BlockState> predicate) {
        return BlockPatternBuilder.start()
                .aisle("^", "#")
                .where('^', BlockInWorld.hasState(predicate))
                .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.CHILI_POOP_BLOCK.get())))
                .build();
    }

    private static BlockPattern getBlazeDispenser() {
        return BlockPatternBuilder.start()
                .aisle(" ", "#")
                .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.CHILI_POOP_BLOCK.get())))
                .build();
    }

    private static BlockPattern getFly(Predicate<BlockState> predicate) {
        return BlockPatternBuilder.start()
                .aisle("^", "#")
                .where('^', BlockInWorld.hasState(predicate))
                .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.MAGGOTS_BLOCK.get())))
                .build();
    }

    private static BlockPattern getFlyDispenser() {
        return BlockPatternBuilder.start()
                .aisle(" ", "#")
                .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.MAGGOTS_BLOCK.get())))
                .build();
    }

}
