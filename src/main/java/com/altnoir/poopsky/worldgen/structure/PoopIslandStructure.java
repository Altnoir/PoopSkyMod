package com.altnoir.poopsky.worldgen.structure;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.worldgen.PoStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PoopIslandStructure extends Structure {
    public static final MapCodec<PoopIslandStructure> CODEC = simpleCodec(PoopIslandStructure::new);
    public static final List<Identifier> DIRT_ISLAND_TEMPLATES = List.of(
            PoopSky.loc("islands/dirt/0x1x0"),
            PoopSky.loc("islands/dirt/11x1x11"),
            PoopSky.loc("islands/dirt/2x2x4"),
            PoopSky.loc("islands/dirt/4x1x6"),
            PoopSky.loc("islands/dirt/8x1x11")
    );
    public static final String POOP_TREE_MARKER = "poopsky:poop_tree";
    private static final int OVERWORLD_HEIGHT_OFFSET = 64;
    private static final int VOID_ISLAND_Y = 128;
    private static final int SPAWN_ISLAND_MIN_DISTANCE = 100;
    private static final int SPAWN_ISLAND_MAX_DISTANCE = 200;
    private static final Map<Long, BlockPos> GUARANTEED_SPAWNS = new ConcurrentHashMap<>();

    public PoopIslandStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Identifier templateId = randomTemplate(context.random());
        Optional<StructureTemplate> template = context.structureTemplateManager().get(templateId);
        if (template.isEmpty()) {
            PoopSky.LOGGER.warn("Missing poop island template {}", templateId);
            return Optional.empty();
        }

        Rotation rotation = Rotation.getRandom(context.random());
        ChunkPos chunkPos = context.chunkPos();
        int centerX = chunkPos.getMiddleBlockX();
        int centerZ = chunkPos.getMiddleBlockZ();
        int y = clampIslandY(context.heightAccessor(),
                context.chunkGenerator().getFirstOccupiedHeight(centerX, centerZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState())
                        + OVERWORLD_HEIGHT_OFFSET);
        Vec3i size = template.get().getSize(rotation);
        BlockPos origin = new BlockPos(centerX - size.getX() / 2, y, centerZ - size.getZ() / 2);

        return Optional.of(new GenerationStub(origin, builder ->
                builder.addPiece(new PoopIslandPiece(context.structureTemplateManager(), templateId, origin, rotation))));
    }

    @Override
    public StructureType<?> type() {
        return PoStructures.POOP_ISLAND.get();
    }

    public static void addGuaranteedSpawnStart(
            RegistryAccess registries,
            ChunkAccess chunk,
            StructureManager structureManager,
            StructureTemplateManager templateManager,
            long seed
    ) {
        BlockPos registeredSpawn = GUARANTEED_SPAWNS.get(seed);
        BlockPos center = registeredSpawn != null ? getGuaranteedSpawnIslandCenter(seed, registeredSpawn) : getGuaranteedSpawnIslandCenter(seed);
        if (!chunk.getPos().equals(new ChunkPos(center))) {
            return;
        }

        Registry<Structure> structureRegistry = registries.registryOrThrow(Registries.STRUCTURE);
        Structure structure = structureRegistry.get(PoopSky.loc("poop_island"));
        if (!(structure instanceof PoopIslandStructure)) {
            PoopSky.LOGGER.warn("Missing poop island structure");
            return;
        }

        SectionPos sectionPos = SectionPos.bottomOf(chunk);
        StructureStart existingStart = structureManager.getStartForStructure(sectionPos, structure, chunk);
        if (existingStart != null && existingStart.isValid()) {
            return;
        }

        int islandY = clampIslandY(chunk.getHeightAccessorForGeneration(), VOID_ISLAND_Y);
        RandomSource random = RandomSource.create(seed ^ BlockPos.asLong(center.getX(), center.getY(), center.getZ()));
        Identifier templateId = randomTemplate(random);
        StructureTemplate template = templateManager.get(templateId).orElse(null);
        if (template == null) {
            PoopSky.LOGGER.warn("Missing poop island template {}", templateId);
            return;
        }

        Rotation rotation = Rotation.getRandom(random);
        Vec3i size = template.getSize(rotation);
        BlockPos origin = new BlockPos(center.getX() - size.getX() / 2, islandY, center.getZ() - size.getZ() / 2);
        StructurePiecesBuilder builder = new StructurePiecesBuilder();
        builder.addPiece(new PoopIslandPiece(templateManager, templateId, origin, rotation));
        structureManager.setStartForStructure(sectionPos, structure, new StructureStart(structure, chunk.getPos(), 0, builder.build()), chunk);
    }

    public static void registerGuaranteedSpawn(long seed, BlockPos spawn) {
        GUARANTEED_SPAWNS.put(seed, spawn.immutable());
    }

    public static BlockPos getGuaranteedSpawnIslandCenter(long seed, BlockPos spawn) {
        RandomSource random = RandomSource.create(seed ^ BlockPos.asLong(spawn.getX(), spawn.getY(), spawn.getZ()));
        int distance = random.nextIntBetweenInclusive(SPAWN_ISLAND_MIN_DISTANCE, SPAWN_ISLAND_MAX_DISTANCE);
        double angle = random.nextDouble() * Math.TAU;

        return new BlockPos(
                spawn.getX() + (int) Math.round(Math.cos(angle) * distance),
                0,
                spawn.getZ() + (int) Math.round(Math.sin(angle) * distance)
        );
    }

    private static BlockPos getGuaranteedSpawnIslandCenter(long seed) {
        RandomSource spawnRandom = new XoroshiroRandomSource(seed);
        return getGuaranteedSpawnIslandCenter(seed, new BlockPos(
                spawnRandom.nextIntBetweenInclusive(-200, 200),
                87,
                spawnRandom.nextIntBetweenInclusive(-200, 200)
        ));
    }

    public static BlockPos getGuaranteedSpawnIslandCenter(ServerLevel level) {
        BlockPos registeredSpawn = GUARANTEED_SPAWNS.get(level.getSeed());
        return getGuaranteedSpawnIslandCenter(level.getSeed(), Objects.requireNonNullElseGet(registeredSpawn, level::getSharedSpawnPos));
    }

    private static Identifier randomTemplate(RandomSource random) {
        return DIRT_ISLAND_TEMPLATES.get(random.nextInt(DIRT_ISLAND_TEMPLATES.size()));
    }

    private static int clampIslandY(LevelHeightAccessor level, int y) {
        return Math.clamp(y, level.getMinBuildHeight() + 8, level.getMaxBuildHeight() - 32);
    }
}
