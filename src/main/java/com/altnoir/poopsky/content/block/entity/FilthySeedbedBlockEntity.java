package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Optional;

public class FilthySeedbedBlockEntity extends BlockEntity {
    private static final int SPAWN_INTERVAL = 200;
    private static final int SPAWN_ATTEMPTS = 4;
    private static final int SPAWN_RANGE = 4;

    private int cooldown;

    public FilthySeedbedBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.FILTHY_SEEDBED.get(), pos, state);
        this.cooldown = SPAWN_INTERVAL;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FilthySeedbedBlockEntity seedbed) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }

        if (seedbed.cooldown-- > 0) {
            return;
        }
        seedbed.cooldown = SPAWN_INTERVAL;

        MobCategory category = MobCategory.MONSTER;
        if (countCategory(serverLevel, category) >= category.getMaxInstancesPerChunk()) {
            return;
        }

        RandomSource random = serverLevel.getRandom();
        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
            BlockPos spawnPos = pos.offset(
                    random.nextInt(SPAWN_RANGE * 2 + 1) - SPAWN_RANGE,
                    random.nextInt(3) - 1,
                    random.nextInt(SPAWN_RANGE * 2 + 1) - SPAWN_RANGE);

            Optional<MobSpawnSettings.SpawnerData> spawnData = getRandomSpawnData(serverLevel, spawnPos, category, random);
            if (spawnData.isEmpty()) {
                continue;
            }

            EntityType<?> entityType = spawnData.get().type;
            if (!entityType.canSummon()
                    || !SpawnPlacements.isSpawnPositionOk(entityType, serverLevel, spawnPos)
                    || !SpawnPlacements.checkSpawnRules(entityType, serverLevel, MobSpawnType.NATURAL, spawnPos, random)
                    || !serverLevel.noCollision(entityType.getSpawnAABB(
                    spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5))) {
                continue;
            }

            Entity entity = entityType.create(serverLevel);
            if (!(entity instanceof Mob mob)) {
                continue;
            }

            mob.moveTo(Vec3.atBottomCenterOf(spawnPos), random.nextFloat() * 360.0F, 0.0F);
            if (!EventHooks.checkSpawnPosition(mob, serverLevel, MobSpawnType.NATURAL)) {
                continue;
            }

            mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.NATURAL, null);
            serverLevel.addFreshEntityWithPassengers(mob);
            break;
        }
    }

    private static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnData(
            ServerLevel level, BlockPos pos, MobCategory category, RandomSource random) {
        WeightedRandomList<MobSpawnSettings.SpawnerData> spawns = level.getChunkSource()
                .getGenerator()
                .getMobsAt(level.getBiome(pos), level.structureManager(), category, pos);
        return EventHooks.getPotentialSpawns(level, category, pos, spawns).getRandom(random);
    }

    private static int countCategory(ServerLevel level, MobCategory category) {
        int count = 0;
        for (Entity entity : level.getAllEntities()) {
            if (entity.getType().getCategory() == category) {
                count++;
            }
        }
        return count;
    }
}
