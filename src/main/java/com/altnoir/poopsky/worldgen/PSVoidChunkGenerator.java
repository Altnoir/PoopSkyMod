package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.Config;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PSVoidChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<PSVoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(generator -> generator.settings),
            TagKey.codec(Registries.STRUCTURE_SET)
                    .optionalFieldOf("allowed_structure_sets")
                    .forGetter(generator -> generator.ignoredAllowedStructureSets)
    ).apply(instance, instance.stable(PSVoidChunkGenerator::new)));

    /*
     * 给结构算法看的“假地表高度”。
     *
     * 不建议用 getMinBuildHeight() + 4，因为 1.18+ 主世界最低高度是 -64，
     * 那样结构会出现在 -60 附近，你很容易以为没生成。
     *
     * 这里放在 64，结构会在虚空中正常可见。
     */
    private static final int VIRTUAL_SURFACE_Y = 64;

    private final Holder<NoiseGeneratorSettings> settings;

    /*
     * 只为了兼容旧 JSON。
     * 不再使用这个字段过滤结构，否则标签不存在时结构会被清空。
     */
    private final Optional<TagKey<StructureSet>> ignoredAllowedStructureSets;

    private final boolean generateNormal;

    public PSVoidChunkGenerator(
            BiomeSource biomeSource,
            Holder<NoiseGeneratorSettings> settings,
            Optional<TagKey<StructureSet>> ignoredAllowedStructureSets
    ) {
        super(biomeSource, settings);
        this.settings = settings;
        this.ignoredAllowedStructureSets = ignoredAllowedStructureSets;

        this.generateNormal = settings.is(ResourceLocation.parse("minecraft:nether"))
                && !Config.voidNetherGeneration;
    }

    public PSVoidChunkGenerator(
            BiomeSource biomeSource,
            Holder<NoiseGeneratorSettings> settings
    ) {
        this(biomeSource, settings, Optional.empty());
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    /*
     * 结构状态必须走原版。
     * 不要 FilteredLookup。
     */
    @Override
    public ChunkGeneratorStructureState createState(
            HolderLookup<StructureSet> lookup,
            RandomState randomState,
            long seed
    ) {
        return super.createState(lookup, randomState, seed);
    }

    /*
     * 主世界虚空：不生成洞穴。
     */
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
        if (this.generateNormal) {
            super.applyCarvers(level, seed, randomState, biomeManager, structureManager, chunk, step);
        }
    }

    /*
     * 主世界虚空：不生成地表。
     */
    @Override
    public void buildSurface(
            WorldGenRegion level,
            StructureManager structureManager,
            RandomState randomState,
            ChunkAccess chunk
    ) {
        if (this.generateNormal) {
            super.buildSurface(level, structureManager, randomState, chunk);
        }
    }

    /*
     * 主世界虚空：不生成基础噪声地形。
     */
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(
            Blender blender,
            RandomState randomState,
            StructureManager structureManager,
            ChunkAccess chunk
    ) {
        if (this.generateNormal) {
            return super.fillFromNoise(blender, randomState, structureManager, chunk);
        }

        return CompletableFuture.completedFuture(chunk);
    }

    /*
     * 给村庄、前哨站、神殿等结构判断地表高度。
     * 这里不是实际生成方块，只是告诉结构：地表在 Y=64。
     */
    @Override
    public int getBaseHeight(
            int x,
            int z,
            Heightmap.Types type,
            LevelHeightAccessor level,
            RandomState randomState
    ) {
        if (this.generateNormal) {
            return super.getBaseHeight(x, z, type, level, randomState);
        }

        return clampVirtualSurfaceY(level);
    }

    /*
     * 给结构算法看的假方块列。
     * 不会写入区块。
     */
    @Override
    public NoiseColumn getBaseColumn(
            int x,
            int z,
            LevelHeightAccessor heightAccessor,
            RandomState randomState
    ) {
        if (this.generateNormal) {
            return super.getBaseColumn(x, z, heightAccessor, randomState);
        }

        int minY = heightAccessor.getMinBuildHeight();
        int surfaceY = clampVirtualSurfaceY(heightAccessor);

        int count = Math.max(0, surfaceY - minY);
        BlockState[] states = new BlockState[count];

        for (int i = 0; i < count; i++) {
            int y = minY + i;

            if (y == minY) {
                states[i] = Blocks.BEDROCK.defaultBlockState();
            } else if (y == surfaceY - 1) {
                states[i] = Blocks.GRASS_BLOCK.defaultBlockState();
            } else {
                states[i] = Blocks.DIRT.defaultBlockState();
            }
        }

        return new NoiseColumn(minY, states);
    }

    /*
     * 这个不能关。
     * 结构真正把方块放进区块，需要这里执行。
     */
    @Override
    public void applyBiomeDecoration(
            WorldGenLevel level,
            ChunkAccess chunk,
            StructureManager structureManager
    ) {
        if (this.generateNormal) {
            super.applyBiomeDecoration(level, chunk, structureManager);
            return;
        }

        placeStructuresOnly(level, chunk, structureManager);
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
    }

    @Override
    public void createReferences(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkAccess chunk
    ) {
        super.createReferences(level, structureManager, chunk);
    }

    /*
     * 主世界虚空不跑原版动物生成。
     */
    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        if (this.generateNormal) {
            super.spawnOriginalMobs(level);
        }
    }

    @Override
    public void addDebugScreenInfo(
            List<String> info,
            RandomState randomState,
            BlockPos pos
    ) {
        if (this.generateNormal) {
            super.addDebugScreenInfo(info, randomState, pos);
        }
    }

    private static int clampVirtualSurfaceY(LevelHeightAccessor level) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        if (VIRTUAL_SURFACE_Y <= minY) {
            return minY + 1;
        }

        if (VIRTUAL_SURFACE_Y > maxY) {
            return maxY;
        }

        return VIRTUAL_SURFACE_Y;
    }

    private void placeStructuresOnly(
            WorldGenLevel level,
            ChunkAccess chunk,
            StructureManager structureManager
    ) {
        ChunkPos chunkPos = chunk.getPos();
        if (SharedConstants.debugVoidTerrain(chunkPos)) {
            return;
        }

        SectionPos sectionPos = SectionPos.of(chunkPos, level.getMinSection());
        BlockPos origin = sectionPos.origin();
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Map<Integer, List<Structure>> structuresByStep = registry.stream()
                .collect(Collectors.groupingBy(structure -> structure.step().ordinal()));
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
        long decorationSeed = random.setDecorationSeed(level.getSeed(), origin.getX(), origin.getZ());

        try {
            for (int step = 0; step < GenerationStep.Decoration.values().length; step++) {
                int structureIndex = 0;
                if (structureManager.shouldGenerateStructures()) {
                    for (Structure structure : structuresByStep.getOrDefault(step, Collections.emptyList())) {
                        random.setFeatureSeed(decorationSeed, structureIndex, step);
                        Supplier<String> name = () -> registry.getResourceKey(structure)
                                .map(Object::toString)
                                .orElseGet(structure::toString);

                        try {
                            level.setCurrentlyGenerating(name);
                            structureManager.startsForStructure(sectionPos, structure)
                                    .forEach(start -> start.placeInChunk(
                                            level,
                                            structureManager,
                                            this,
                                            random,
                                            getWritableArea(chunk),
                                            chunkPos
                                    ));
                        } catch (Exception exception) {
                            CrashReport report = CrashReport.forThrowable(exception, "Structure placement");
                            report.addCategory("Structure").setDetail("Description", name::get);
                            throw new ReportedException(report);
                        }

                        structureIndex++;
                    }
                }
            }
        } catch (Exception exception) {
            CrashReport report = CrashReport.forThrowable(exception, "Biome decoration");
            report.addCategory("Generation")
                    .setDetail("CenterX", chunkPos.x)
                    .setDetail("CenterZ", chunkPos.z)
                    .setDetail("Decoration Seed", decorationSeed);
            throw new ReportedException(report);
        } finally {
            level.setCurrentlyGenerating(null);
        }
    }

    private static BoundingBox getWritableArea(ChunkAccess chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        LevelHeightAccessor height = chunk.getHeightAccessorForGeneration();
        int minY = height.getMinBuildHeight() + 1;
        int maxY = height.getMaxBuildHeight() - 1;

        return new BoundingBox(minX, minY, minZ, minX + 15, maxY, minZ + 15);
    }
}
