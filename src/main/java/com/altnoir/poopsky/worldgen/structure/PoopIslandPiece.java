package com.altnoir.poopsky.worldgen.structure;

import com.altnoir.poopsky.content.entity.p.PoolimeEntity;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.worldgen.PoStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PoopIslandPiece extends TemplateStructurePiece {
    private static final String ROTATION_KEY = "Rotation";
    private static final int MAX_TREE_COUNT = 3;
    private static final long TREE_SELECTION_SALT = 0x6B9D5F21A7C403L;
    private static final long TREE_SALT = 0x1D2F7A4B9C3E5D6L;
    private static final long TREE_VARIANT_SALT = 0x2F9A6C4D187B35EL;
    private static final long POOLIME_SELECTION_SALT = 0x5A71C0FFEEBABEL;
    private static final long POOLIME_SPAWN_SALT = 0x37A11D5EED12345L;
    private static final double POOLIME_DUPLICATE_RADIUS = 2.0D;

    public PoopIslandPiece(StructureTemplateManager manager, Identifier templateId, BlockPos pos, Rotation rotation) {
        super(PoStructures.POOP_ISLAND_PIECE.get(), 0, manager, templateId, templateId.toString(), placeSettings(rotation), pos);
    }

    public PoopIslandPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(PoStructures.POOP_ISLAND_PIECE.get(), tag, manager, id -> placeSettings(readRotation(tag)));
    }

    public static StructurePlaceSettings placeSettings(Rotation rotation) {
        return new StructurePlaceSettings()
                .setRotation(rotation)
                .setIgnoreEntities(false)
                .setFinalizeEntities(true)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION_KEY, this.placeSettings.getRotation().name());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos
    ) {
        super.postProcess(level, structureManager, generator, random, box, chunkPos, pos);
        placeRandomPoopTree(level, this.template, this.templatePosition, this.placeSettings);
        spawnRandomPoolimes(level, this.template, this.templatePosition, this.placeSettings, box);
    }

    @Override
    protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
    }

    public static void placeRandomPoopTree(WorldGenLevel level, StructureTemplate template, BlockPos origin, StructurePlaceSettings settings) {
        List<StructureTemplate.StructureBlockInfo> treeMarkers = template.filterBlocks(origin, settings, Blocks.STRUCTURE_BLOCK)
                .stream()
                .filter(PoopIslandPiece::isTreeMarker)
                .sorted(Comparator.comparingLong(blockInfo -> blockInfo.pos().asLong()))
                .toList();

        if (treeMarkers.isEmpty()) {
            return;
        }

        List<StructureTemplate.StructureBlockInfo> candidates = new ArrayList<>(treeMarkers);
        RandomSource selectionRandom = RandomSource.create(featureSeed(level.getSeed(), origin, origin, TREE_SELECTION_SALT));
        int treeCount = selectionRandom.nextIntBetweenInclusive(0, Math.min(MAX_TREE_COUNT, candidates.size()));
        for (int index = 0; index < treeCount; index++) {
            StructureTemplate.StructureBlockInfo treeMarker = candidates.remove(selectionRandom.nextInt(candidates.size()));
            BlockPos treePos = treeMarker.pos();
            RandomSource treeRandom = RandomSource.create(featureSeed(level.getSeed(), origin, treePos, TREE_SALT));
            RandomSource variantRandom = RandomSource.create(featureSeed(level.getSeed(), origin, treePos, TREE_VARIANT_SALT));
            placePoopTree(level, treeRandom, variantRandom, treePos);
        }
    }

    public static void spawnRandomPoolimes(WorldGenLevel level, StructureTemplate template, BlockPos origin, StructurePlaceSettings settings, BoundingBox box) {
        List<StructureTemplate.StructureBlockInfo> poolimeBlocks = new ArrayList<>(template.filterBlocks(origin, settings, PoBlocks.POOLIME_BLOCK.get())
                .stream()
                .sorted(Comparator.comparingLong(blockInfo -> blockInfo.pos().asLong()))
                .toList());

        if (poolimeBlocks.isEmpty()) {
            return;
        }

        RandomSource selectionRandom = RandomSource.create(featureSeed(level.getSeed(), origin, origin, POOLIME_SELECTION_SALT));
        int spawnCount = Math.min(poolimeBlocks.size(), selectionRandom.nextIntBetweenInclusive(1, 3));
        for (int index = 0; index < spawnCount; index++) {
            BlockPos pos = poolimeBlocks.remove(selectionRandom.nextInt(poolimeBlocks.size())).pos().above();
            if (!box.isInside(pos) || !level.getBlockState(pos).canBeReplaced()) {
                continue;
            }

            PoolimeEntity poolime = PoEntityType.POOLIME.get().create(level.getLevel(), EntitySpawnReason.STRUCTURE);
            if (poolime == null) {
                continue;
            }

            RandomSource spawnRandom = RandomSource.create(featureSeed(level.getSeed(), origin, pos, POOLIME_SPAWN_SALT));
            poolime.setSize(spawnRandom.nextInt(3) + 1, true);
            poolime.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, spawnRandom.nextFloat() * 360.0F, 0.0F);
            if (level.noCollision(poolime, poolime.getBoundingBox()) && !hasNearbyPoolime(level, poolime)) {
                level.addFreshEntity(poolime);
            }
        }
    }

    private static void placePoopTree(WorldGenLevel level, RandomSource shapeRandom, RandomSource variantRandom, BlockPos basePos) {
        int trunkHeight = shapeRandom.nextIntBetweenInclusive(4, 5);
        for (int y = 0; y < trunkHeight; y++) {
            placeTreeBlock(level, basePos.above(y), PoBlocks.POOP_LOG.get().defaultBlockState());
        }

        BlockPos crown = basePos.above(trunkHeight);
        for (int y = -2; y <= 1; y++) {
            int radius = y == 1 ? 1 : 2;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius && Math.abs(z) == radius && shapeRandom.nextBoolean()) {
                        continue;
                    }

                    placeTreeBlock(level, crown.offset(x, y, z), PoBlocks.POOP_LEAVES.get().defaultBlockState());
                }
            }
        }
    }

    private static void placeTreeBlock(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (level.isOutsideBuildHeight(pos)) {
            return;
        }

        if (level.getBlockState(pos).canBeReplaced()) {
            level.setBlock(pos, state, 2);
        }
    }

    private static boolean hasNearbyPoolime(WorldGenLevel level, PoolimeEntity poolime) {
        AABB duplicateCheckArea = poolime.getBoundingBox().inflate(POOLIME_DUPLICATE_RADIUS);
        return !level.getEntitiesOfClass(PoolimeEntity.class, duplicateCheckArea).isEmpty();
    }

    private static long featureSeed(long worldSeed, BlockPos origin, BlockPos featurePos, long salt) {
        long seed = worldSeed ^ salt;
        seed ^= BlockPos.asLong(origin.getX(), origin.getY(), origin.getZ());
        seed = Long.rotateLeft(seed, 21) ^ BlockPos.asLong(featurePos.getX(), featurePos.getY(), featurePos.getZ());
        return seed;
    }

    private static boolean isTreeMarker(StructureTemplate.StructureBlockInfo blockInfo) {
        CompoundTag tag = blockInfo.nbt();
        return tag != null && tag.getString("metadata")
                .filter(PoopIslandStructure.POOP_TREE_MARKER::equals)
                .isPresent();
    }

    private static Rotation readRotation(CompoundTag tag) {
        if (!tag.contains(ROTATION_KEY)) {
            return Rotation.NONE;
        }

        try {
            return Rotation.valueOf(tag.getString(ROTATION_KEY).orElse(Rotation.NONE.name()));
        } catch (IllegalArgumentException exception) {
            return Rotation.NONE;
        }
    }
}
