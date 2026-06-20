package com.altnoir.poopsky.villager;

import com.altnoir.poopsky.tag.PTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;

public class PVillagerBehaviors {
    private static final double POOP_TEMPT_RANGE = 10.0;
    private static final float POOP_TEMPT_SPEED = 0.5F;
    private static final int POOP_TEMPT_CLOSE_ENOUGH = 2;
    private static final int POOP_TEMPT_MEMORY_TICKS = 20;
    private static final TargetingConditions POOP_TEMPT_TARGETING = TargetingConditions.forNonCombat()
            .range(POOP_TEMPT_RANGE)
            .ignoreLineOfSight()
            .selector(entity -> entity instanceof Player player && isHoldingPoop(player));

    public static void tickPoopTemptation(Villager villager) {
        if (!(villager.level() instanceof ServerLevel level) || !canBeTempted(villager)) return;

        Player player = level.getNearestPlayer(POOP_TEMPT_TARGETING, villager);
        if (player == null) return;

        EntityTracker tracker = new EntityTracker(player, true);
        Brain<Villager> brain = villager.getBrain();
        brain.setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET, tracker, POOP_TEMPT_MEMORY_TICKS);
        brain.setMemoryWithExpiry(MemoryModuleType.WALK_TARGET, new WalkTarget(tracker, POOP_TEMPT_SPEED, POOP_TEMPT_CLOSE_ENOUGH), POOP_TEMPT_MEMORY_TICKS);
    }

    private static boolean canBeTempted(Villager villager) {
        if (!villager.isAlive() || villager.isBaby() || villager.isNoAi() || villager.isSleeping() || villager.isTrading()) {
            return false;
        }

        VillagerProfession profession = villager.getVillagerData().getProfession();
        return profession == PVillagers.POOP_MAKER.value() || profession == PVillagers.GASTRONOME.value();
    }

    private static boolean isHoldingPoop(Player player) {
        return player.getMainHandItem().is(PTags.Items.POOPS) || player.getOffhandItem().is(PTags.Items.POOPS);
    }
}
