package com.altnoir.poopsky.worldgen.structure;

import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.entity.p.PoolimeEntity;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.worldgen.PSStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

import java.util.ArrayList;
import java.util.List;

public class PoopIslandPiece extends TemplateStructurePiece {
    private static final String ROTATION_KEY = "Rotation";

    public PoopIslandPiece(StructureTemplateManager manager, ResourceLocation templateId, BlockPos pos, Rotation rotation) {
        super(PSStructures.POOP_ISLAND_PIECE.get(), 0, manager, templateId, templateId.toString(), placeSettings(rotation), pos);
    }

    public PoopIslandPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(PSStructures.POOP_ISLAND_PIECE.get(), tag, manager, id -> placeSettings(readRotation(tag)));
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
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            RandomSource random,
            BoundingBox box,
            ChunkPos chunkPos,
            BlockPos pos
    ) {
        super.postProcess(level, structureManager, generator, random, box, chunkPos, pos);
        RandomSource islandRandom = RandomSource.create(BlockPos.asLong(this.templatePosition.getX(), this.templatePosition.getY(), this.templatePosition.getZ()));
        placeRandomPoopTree(level, islandRandom, this.template, this.templatePosition, this.placeSettings, box);
        spawnRandomPoolimes(level, islandRandom, this.template, this.templatePosition, this.placeSettings, box);
    }

    @Override
    protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
    }

    public static void placeRandomPoopTree(WorldGenLevel level, RandomSource random, StructureTemplate template, BlockPos origin, StructurePlaceSettings settings, BoundingBox box) {
        List<StructureTemplate.StructureBlockInfo> treeMarkers = template.filterBlocks(origin, settings, Blocks.STRUCTURE_BLOCK)
                .stream()
                .filter(PoopIslandPiece::isTreeMarker)
                .toList();

        if (treeMarkers.isEmpty() || random.nextFloat() >= 0.7F) {
            return;
        }

        BlockPos treePos = treeMarkers.get(random.nextInt(treeMarkers.size())).pos();
        placePoopTree(level, random, treePos);
    }

    public static void spawnRandomPoolimes(WorldGenLevel level, RandomSource random, StructureTemplate template, BlockPos origin, StructurePlaceSettings settings, BoundingBox box) {
        List<StructureTemplate.StructureBlockInfo> poolimeBlocks = new ArrayList<>(template.filterBlocks(origin, settings, PBlocks.POOLIME_BLOCK.get())
                .stream()
                .filter(blockInfo -> box.isInside(blockInfo.pos().above()))
                .filter(blockInfo -> level.getBlockState(blockInfo.pos().above()).canBeReplaced())
                .toList());

        if (poolimeBlocks.isEmpty()) {
            return;
        }

        int spawnCount = Math.min(poolimeBlocks.size(), random.nextIntBetweenInclusive(1, 3));
        for (int index = 0; index < spawnCount; index++) {
            BlockPos pos = poolimeBlocks.remove(random.nextInt(poolimeBlocks.size())).pos().above();
            PoolimeEntity poolime = PEntityType.POOLIME.get().create(level.getLevel());
            if (poolime == null) {
                continue;
            }

            poolime.setSize(random.nextInt(3) + 1, true);
            poolime.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            if (level.noCollision(poolime, poolime.getBoundingBox())) {
                level.addFreshEntity(poolime);
            }
        }
    }

    private static void placePoopTree(WorldGenLevel level, RandomSource random, BlockPos basePos) {
        int trunkHeight = random.nextIntBetweenInclusive(4, 5);
        for (int y = 0; y < trunkHeight; y++) {
            placeTreeBlock(level, basePos.above(y), PBlocks.POOP_LOG.get().defaultBlockState());
        }

        BlockPos crown = basePos.above(trunkHeight);
        for (int y = -2; y <= 1; y++) {
            int radius = y == 1 ? 1 : 2;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius && Math.abs(z) == radius && random.nextBoolean()) {
                        continue;
                    }

                    placeTreeBlock(level, crown.offset(x, y, z), PBlocks.POOP_LEAVES.get().defaultBlockState());
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

    private static boolean isTreeMarker(StructureTemplate.StructureBlockInfo blockInfo) {
        CompoundTag tag = blockInfo.nbt();
        return tag != null && PoopIslandStructure.POOP_TREE_MARKER.equals(tag.getString("metadata"));
    }

    private static Rotation readRotation(CompoundTag tag) {
        if (!tag.contains(ROTATION_KEY)) {
            return Rotation.NONE;
        }

        try {
            return Rotation.valueOf(tag.getString(ROTATION_KEY));
        } catch (IllegalArgumentException exception) {
            return Rotation.NONE;
        }
    }
}
