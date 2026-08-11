package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.worldgen.structure.PoopIslandStructure;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PoVoidChunkGenerator extends NoiseBasedChunkGenerator {
    private static final int VIRTUAL_SURFACE_Y = 64;
    private static final int SPAWN_STRUCTURE_PROTECTION_RADIUS = 50;
    private static final Identifier STRONGHOLDS_STRUCTURE_SET = PoopSky.mcloc("strongholds");
    private static final Identifier STRONGHOLD_STRUCTURE = PoopSky.mcloc("stronghold");

    private static final Codec<List<ResourceKey<StructureSet>>> STRUCTURE_SET_KEYS_CODEC =
            Codec.either(Identifier.CODEC.listOf(), Identifier.CODEC).xmap(
                    either -> either.map(
                            locations -> locations.stream()
                                    .map(location -> ResourceKey.create(Registries.STRUCTURE_SET, location))
                                    .toList(),
                            location -> List.of(ResourceKey.create(Registries.STRUCTURE_SET, location))
                    ),
                    keys -> keys.size() == 1
                            ? Either.right(keys.getFirst().location())
                            : Either.left(keys.stream().map(ResourceKey::location).toList())
            );

    private static final Codec<AllowedStructureSets> ALLOWED_STRUCTURE_SETS_CODEC =
            Codec.either(
                    TagKey.hashedCodec(Registries.STRUCTURE_SET),
                    STRUCTURE_SET_KEYS_CODEC
            ).xmap(
                    either -> either.map(
                            AllowedStructureSets::ofTag,
                            AllowedStructureSets::ofKeys
                    ),
                    allowed -> allowed.tag() != null
                            ? Either.left(allowed.tag())
                            : Either.right(allowed.keys())
            );

    public static final MapCodec<PoVoidChunkGenerator> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .forGetter(generator -> generator.biomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings")
                            .forGetter(generator -> generator.settings),
                    ALLOWED_STRUCTURE_SETS_CODEC.optionalFieldOf("allowed_structure_sets")
                            .forGetter(generator -> Optional.ofNullable(generator.allowedStructureSets))
            ).apply(instance, instance.stable((biomeSource, settings, allowedStructureSets) ->
                    new PoVoidChunkGenerator(biomeSource, settings, allowedStructureSets.orElse(null)))));

    private final Holder<NoiseGeneratorSettings> settings;
    @Nullable
    private final AllowedStructureSets allowedStructureSets;
    private final boolean generateNormal;

    public PoVoidChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings, @Nullable AllowedStructureSets allowedStructureSets) {
        super(biomeSource, settings);
        this.settings = settings;
        this.allowedStructureSets = allowedStructureSets;
        this.generateNormal = settings.is(Identifier.parse("minecraft:nether")) && !Config.voidNetherGeneration;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private record AllowedStructureSets(@Nullable TagKey<StructureSet> tag, List<ResourceKey<StructureSet>> keys) {
        private static AllowedStructureSets ofTag(TagKey<StructureSet> tag) {
            return new AllowedStructureSets(tag, List.of());
        }

        private static AllowedStructureSets ofKeys(List<ResourceKey<StructureSet>> keys) {
            return new AllowedStructureSets(null, List.copyOf(keys));
        }

        private boolean contains(Holder<StructureSet> holder) {
            if (tag != null) {
                return holder.is(tag);
            }

            Set<Identifier> allowedLocations = keys.stream()
                    .map(ResourceKey::location)
                    .collect(Collectors.toUnmodifiableSet());

            return holder.unwrapKey()
                    .map(key -> allowedLocations.contains(key.location()))
                    .orElse(false)
                    || holder.value().structures().stream()
                    .map(StructureSelectionEntry::structure)
                    .map(Holder::unwrapKey)
                    .anyMatch(key -> key.map(ResourceKey::location).filter(allowedLocations::contains).isPresent());
        }
    }

    @Override
    public void applyCarvers(
            WorldGenRegion level,
            long seed,
            RandomState randomState,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunk,
            GenerationStep.Carving step
    ) {
        if (generateNormal) {
            super.applyCarvers(level, seed, randomState, biomeManager, structureManager, chunk, step);
        }
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        if (generateNormal) {
            super.buildSurface(level, structureManager, randomState, chunk);
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return generateNormal
                ? super.fillFromNoise(blender, randomState, structureManager, chunk)
                : CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return generateNormal ? super.getBaseHeight(x, z, type, level, randomState) : virtualSurfaceY(level);
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        if (generateNormal) {
            return super.getBaseColumn(x, z, level, randomState);
        }

        int minY = level.getMinBuildHeight();
        int surfaceY = virtualSurfaceY(level);
        BlockState[] states = new BlockState[Math.max(0, surfaceY - minY)];

        for (int index = 0; index < states.length; index++) {
            int y = minY + index;
            BlockState groundY = y == (surfaceY - 1) ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.DIRT.defaultBlockState();
            states[index] = y == minY ? Blocks.BEDROCK.defaultBlockState() : groundY;
        }

        return new NoiseColumn(minY, states);
    }

    @Override
    public void createStructures(
            RegistryAccess registries,
            ChunkGeneratorStructureState structureState,
            StructureManager structureManager,
            ChunkAccess chunk,
            StructureTemplateManager templateManager
    ) {
        super.createStructures(registries, structureState, structureManager, chunk, templateManager);

        if (!generateNormal) {
            filterStructureStarts(registries, structureState.getLevelSeed(), chunk);
        }

        if (!generateNormal && settings.is(Identifier.parse("minecraft:overworld")) && isStructureAllowed(registries, PoopSky.loc("poop_island"))) {
            PoopIslandStructure.addGuaranteedSpawnStart(registries, chunk, structureManager, templateManager, structureState.getLevelSeed());
        }
    }

    @Override
    @Nullable
    public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel level, HolderSet<Structure> structures, BlockPos pos, int searchRadius, boolean skipKnownStructures) {
        Pair<BlockPos, Holder<Structure>> guaranteedSpawnIsland = findGuaranteedSpawnIsland(level, structures, pos, searchRadius, skipKnownStructures);
        if (generateNormal || allowedStructureSets == null) {
            return nearestStructure(pos, super.findNearestMapStructure(level, structures, pos, searchRadius, skipKnownStructures), guaranteedSpawnIsland);
        }

        Set<Structure> allowedStructures = resolveAllowedStructures(level.registryAccess());
        List<Holder<Structure>> searchableStructures = structures.stream()
                .filter(structure -> allowedStructures.contains(structure.value()))
                .toList();

        if (searchableStructures.isEmpty()) {
            return guaranteedSpawnIsland;
        }

        return nearestStructure(pos, super.findNearestMapStructure(level, HolderSet.direct(searchableStructures), pos, searchRadius, skipKnownStructures), guaranteedSpawnIsland);
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager
    ) {
        if (generateNormal) {
            super.applyBiomeDecoration(level, chunk, structureManager);
        } else {
            placeStructuresOnly(level, chunk, structureManager);
        }
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        if (generateNormal) {
            super.spawnOriginalMobs(level);
        }
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        if (generateNormal) {
            super.addDebugScreenInfo(info, randomState, pos);
        }
    }

    private Set<Structure> resolveAllowedStructures(RegistryAccess registries) {
        if (allowedStructureSets == null) {
            return Set.of();
        }

        Registry<StructureSet> registry = registries.registryOrThrow(Registries.STRUCTURE_SET);

        return registry.holders()
                .filter(this::isStructureSetAllowed)
                .flatMap(holder -> holder.value().structures().stream())
                .map(StructureSelectionEntry::structure)
                .map(Holder::value)
                .collect(Collectors.toUnmodifiableSet());
    }

    private boolean isStructureAllowed(RegistryAccess registries, Identifier structureId) {
        if (allowedStructureSets == null) {
            return true;
        }

        Registry<Structure> structureRegistry = registries.registryOrThrow(Registries.STRUCTURE);
        Structure structure = structureRegistry.get(structureId);
        return structure != null && resolveAllowedStructures(registries).contains(structure);
    }

    private boolean isStructureSetAllowed(Holder<StructureSet> holder) {
        return allowedStructureSets != null
                && allowedStructureSets.contains(holder)
                && (Config.strongholdGeneration || !isStrongholdStructureSet(holder));
    }

    private static boolean isStrongholdStructureSet(Holder<StructureSet> holder) {
        return holder.unwrapKey()
                .map(ResourceKey::location)
                .filter(STRONGHOLDS_STRUCTURE_SET::equals)
                .isPresent()
                || holder.value().structures().stream()
                .map(StructureSelectionEntry::structure)
                .map(Holder::unwrapKey)
                .flatMap(Optional::stream)
                .map(ResourceKey::location)
                .anyMatch(STRONGHOLD_STRUCTURE::equals);
    }

    @Nullable
    private Pair<BlockPos, Holder<Structure>> findGuaranteedSpawnIsland(ServerLevel level, HolderSet<Structure> structures, BlockPos pos, int searchRadius, boolean skipKnownStructures) {
        if (skipKnownStructures || generateNormal || !settings.is(Identifier.parse("minecraft:overworld")) || !isStructureAllowed(level.registryAccess(), PoopSky.loc("poop_island"))) {
            return null;
        }

        Holder<Structure> poopIsland = structures.stream()
                .filter(structure -> structure.unwrapKey()
                        .map(key -> key.location().equals(PoopSky.loc("poop_island")))
                        .orElse(false))
                .findFirst()
                .orElse(null);
        if (poopIsland == null) {
            return null;
        }

        BlockPos center = PoopIslandStructure.getGuaranteedSpawnIslandCenter(level);
        int chunkDistance = Math.max(
                Math.abs(SectionPos.blockToSectionCoord(pos.getX()) - SectionPos.blockToSectionCoord(center.getX())),
                Math.abs(SectionPos.blockToSectionCoord(pos.getZ()) - SectionPos.blockToSectionCoord(center.getZ()))
        );
        return chunkDistance <= searchRadius ? Pair.of(center, poopIsland) : null;
    }

    @Nullable
    private static Pair<BlockPos, Holder<Structure>> nearestStructure(BlockPos pos, @Nullable Pair<BlockPos, Holder<Structure>> first, @Nullable Pair<BlockPos, Holder<Structure>> second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }

        return pos.distSqr(second.getFirst()) < pos.distSqr(first.getFirst()) ? second : first;
    }

    private void filterStructureStarts(RegistryAccess registries, long seed, ChunkAccess chunk) {
        Set<Structure> allowedStructures = resolveAllowedStructures(registries);
        boolean protectSpawn = settings.is(Identifier.parse("minecraft:overworld"));
        BlockPos spawn = protectSpawn ? defaultSpawnPosition(seed) : BlockPos.ZERO;

        for (Map.Entry<Structure, StructureStart> entry : List.copyOf(chunk.getAllStarts().entrySet())) {
            StructureStart start = entry.getValue();
            boolean disallowed = allowedStructureSets != null && !allowedStructures.contains(entry.getKey());
            boolean intersectsSpawnProtection = protectSpawn
                    && start.isValid()
                    && intersectsSpawnProtection(start.getBoundingBox(), spawn);

            if (disallowed || intersectsSpawnProtection) {
                chunk.setStartForStructure(entry.getKey(), StructureStart.INVALID_START);
            }
        }
    }

    private static boolean intersectsSpawnProtection(BoundingBox box, BlockPos spawn) {
        int nearestX = Math.clamp(spawn.getX(), box.minX(), box.maxX());
        int nearestZ = Math.clamp(spawn.getZ(), box.minZ(), box.maxZ());
        long deltaX = nearestX - spawn.getX();
        long deltaZ = nearestZ - spawn.getZ();
        return deltaX * deltaX + deltaZ * deltaZ <= (long) SPAWN_STRUCTURE_PROTECTION_RADIUS * SPAWN_STRUCTURE_PROTECTION_RADIUS;
    }

    public static BlockPos defaultSpawnPosition(long seed) {
        RandomSource random = new XoroshiroRandomSource(seed);
        return new BlockPos(
                random.nextIntBetweenInclusive(-200, 200),
                87,
                random.nextIntBetweenInclusive(-200, 200)
        );
    }

    private void placeStructuresOnly(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        ChunkPos chunkPos = chunk.getPos();

        if (SharedConstants.debugVoidTerrain(chunkPos) || !structureManager.shouldGenerateStructures()) {
            return;
        }

        SectionPos sectionPos = SectionPos.bottomOf(chunk);
        BlockPos origin = sectionPos.origin();
        RegistryAccess registries = level.registryAccess();
        Registry<Structure> structureRegistry = registries.registryOrThrow(Registries.STRUCTURE);
        Set<Structure> allowedStructures = resolveAllowedStructures(registries);

        Map<Integer, List<Structure>> structuresByStep =
                structureRegistry.stream()
                        .filter(structure -> allowedStructureSets == null || allowedStructures.contains(structure))
                        .collect(Collectors.groupingBy(structure -> structure.step().ordinal()));

        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));

        long decorationSeed = random.setDecorationSeed(level.getSeed(), origin.getX(), origin.getZ());

        try {
            for (int step = 0; step < GenerationStep.Decoration.values().length; step++) {
                List<Structure> structures = structuresByStep.getOrDefault(step, Collections.emptyList());

                for (int index = 0; index < structures.size(); index++) {
                    Structure structure = structures.get(index);
                    random.setFeatureSeed(decorationSeed, index, step);

                    Supplier<String> name = () ->
                            structureRegistry.getResourceKey(structure)
                                    .map(Object::toString)
                                    .orElseGet(structure::toString);

                    try {
                        level.setCurrentlyGenerating(name);

                        structureManager.startsForStructure(sectionPos, structure)
                                .stream()
                                .filter(StructureStart::isValid)
                                .forEach(start -> start.placeInChunk(
                                        level,
                                        structureManager,
                                        this,
                                        random,
                                        writableArea(chunk),
                                        chunkPos
                                ));
                    } catch (Exception exception) {
                        CrashReport report = CrashReport.forThrowable(exception, "Structure placement");

                        report.addCategory("Structure")
                                .setDetail("Description", name::get);

                        throw new ReportedException(report);
                    }
                }
            }
        } catch (ReportedException exception) {
            throw exception;
        } catch (Exception exception) {
            CrashReport report = CrashReport.forThrowable(exception, "Void biome decoration");

            report.addCategory("Generation")
                    .setDetail("CenterX", chunkPos.x)
                    .setDetail("CenterZ", chunkPos.z)
                    .setDetail("Decoration Seed", decorationSeed);

            throw new ReportedException(report);
        } finally {
            level.setCurrentlyGenerating(null);
        }
    }

    private static BoundingBox writableArea(ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();
        LevelHeightAccessor height = chunk.getHeightAccessorForGeneration();

        return new BoundingBox(
                pos.getMinBlockX(),
                height.getMinBuildHeight() + 1,
                pos.getMinBlockZ(),
                pos.getMaxBlockX(),
                height.getMaxBuildHeight() - 1,
                pos.getMaxBlockZ()
        );
    }

    private static int virtualSurfaceY(LevelHeightAccessor level) {
        return Math.clamp(VIRTUAL_SURFACE_Y, level.getMinBuildHeight() + 1, level.getMaxBuildHeight());
    }
}
