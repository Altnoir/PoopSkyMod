package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.Config;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PSVoidChunkGenerator extends NoiseBasedChunkGenerator {
    private static final int VIRTUAL_SURFACE_Y = 64;

    private static final Codec<List<ResourceKey<StructureSet>>> STRUCTURE_SET_KEYS_CODEC =
            Codec.either(ResourceLocation.CODEC.listOf(), ResourceLocation.CODEC).xmap(
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
                    allowed -> allowed.tag()
                            .<Either<TagKey<StructureSet>, List<ResourceKey<StructureSet>>>>map(Either::left)
                            .orElseGet(() -> Either.right(allowed.keys()))
            );

    public static final MapCodec<PSVoidChunkGenerator> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .forGetter(generator -> generator.biomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings")
                            .forGetter(generator -> generator.settings),
                    ALLOWED_STRUCTURE_SETS_CODEC.optionalFieldOf("allowed_structure_sets")
                            .forGetter(generator -> generator.allowedStructureSets)
            ).apply(instance, instance.stable(PSVoidChunkGenerator::new)));

    private final Holder<NoiseGeneratorSettings> settings;
    private final Optional<AllowedStructureSets> allowedStructureSets;
    private final boolean generateNormal;

    public PSVoidChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings, Optional<AllowedStructureSets> allowedStructureSets) {
        super(biomeSource, settings);
        this.settings = settings;
        this.allowedStructureSets = allowedStructureSets;
        this.generateNormal = settings.is(ResourceLocation.parse("minecraft:nether")) && !Config.voidNetherGeneration;
    }

    public PSVoidChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        this(biomeSource, settings, Optional.empty());
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private record AllowedStructureSets(Optional<TagKey<StructureSet>> tag, List<ResourceKey<StructureSet>> keys) {
        private static AllowedStructureSets ofTag(TagKey<StructureSet> tag) {
            return new AllowedStructureSets(Optional.of(tag), List.of());
        }

        private static AllowedStructureSets ofKeys(List<ResourceKey<StructureSet>> keys) {
            return new AllowedStructureSets(Optional.empty(), List.copyOf(keys));
        }

        private boolean contains(Holder<StructureSet> holder) {
            return tag.map(holder::is)
                    .orElseGet(() -> holder.unwrapKey().filter(keys::contains).isPresent());
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
            super.applyCarvers( level, seed, randomState, biomeManager, structureManager, chunk, step);
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
        return generateNormal
                ? super.getBaseHeight(x, z, type, level, randomState)
                : virtualSurfaceY(level);
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
            states[index] = y == minY
                    ? Blocks.BEDROCK.defaultBlockState()
                    : y == surfaceY - 1
                    ? Blocks.GRASS_BLOCK.defaultBlockState()
                    : Blocks.DIRT.defaultBlockState();
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

        if (allowedStructureSets.isEmpty()) {
            return;
        }

        Set<Structure> allowedStructures = resolveAllowedStructures(registries);
        Map<Structure, StructureStart> filteredStarts = new HashMap<>();

        chunk.getAllStarts().forEach((structure, start) -> {
            if (allowedStructures.contains(structure)) {
                filteredStarts.put(structure, start);
            }
        });

        chunk.setAllStarts(filteredStarts);
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
        if (allowedStructureSets.isEmpty()) {
            return Set.of();
        }

        AllowedStructureSets allowed = allowedStructureSets.get();
        Registry<StructureSet> registry = registries.registryOrThrow(Registries.STRUCTURE_SET);

        return registry.holders()
                .filter(allowed::contains)
                .flatMap(holder -> holder.value().structures().stream())
                .map(StructureSet.StructureSelectionEntry::structure)
                .map(Holder::value)
                .collect(Collectors.toUnmodifiableSet());
    }

    private void placeStructuresOnly( WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        ChunkPos chunkPos = chunk.getPos();

        if (SharedConstants.debugVoidTerrain(chunkPos) || !structureManager.shouldGenerateStructures()) {
            return;
        }

        SectionPos sectionPos = SectionPos.bottomOf(chunk);
        BlockPos origin = sectionPos.origin();
        RegistryAccess registries = level.registryAccess();
        Registry<Structure> structureRegistry = registries.registryOrThrow(Registries.STRUCTURE);

        Set<Structure> allowedStructures = allowedStructureSets.isPresent()
                ? resolveAllowedStructures(registries)
                : null;

        Map<Integer, List<Structure>> structuresByStep =
                structureRegistry.stream()
                        .filter(structure -> allowedStructures == null || allowedStructures.contains(structure))
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
            CrashReport report = CrashReport.forThrowable(exception,"Void biome decoration");

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