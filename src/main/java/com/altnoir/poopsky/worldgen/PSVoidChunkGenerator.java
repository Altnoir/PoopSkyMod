package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.Config;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class PSVoidChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<PSVoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(generator -> generator.settings),
            TagKey.codec(Registries.STRUCTURE_SET).fieldOf("allowed_structure_sets").forGetter(generator -> generator.allowedStructureSets)
    ).apply(instance, instance.stable(PSVoidChunkGenerator::new)));

    private final Holder<NoiseGeneratorSettings> settings;
    private final TagKey<StructureSet> allowedStructureSets;
    private final boolean generateNormal;
    private final boolean allowBiomeDecoration;

    public PSVoidChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings, TagKey<StructureSet> allowedStructureSets) {
        super(biomeSource, settings);
        this.settings = settings;
        this.allowedStructureSets = allowedStructureSets;
        this.generateNormal = (settings.is(ResourceLocation.parse("minecraft:end")) && !Config.voidEndGeneration)
                || (settings.is(ResourceLocation.parse("minecraft:nether")) && !Config.voidNetherGeneration);
        this.allowBiomeDecoration = !settings.is(ResourceLocation.parse("minecraft:overworld"));
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {
        if (this.generateNormal) {
            super.applyCarvers(level, seed, randomState, biomeManager, structureManager, chunk, step);
        }
    }

    @Override
    public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> lookup, RandomState randomState, long seed) {
        return this.generateNormal
                ? super.createState(lookup, randomState, seed)
                : super.createState(new FilteredLookup(lookup, this.allowedStructureSets), randomState, seed);
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
        if (this.generateNormal) {
            super.buildSurface(level, structureManager, randomState, chunk);
        }
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        if (this.generateNormal) {
            super.spawnOriginalMobs(level);
        }
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return this.generateNormal
                ? super.fillFromNoise(blender, randomState, structureManager, chunk)
                : CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return this.generateNormal ? super.getBaseHeight(x, z, type, level, randomState) : getMinY();
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        return this.generateNormal ? super.getBaseColumn(x, z, heightAccessor, randomState) : new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        if (this.generateNormal) {
            super.addDebugScreenInfo(info, randomState, pos);
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (this.generateNormal || this.allowBiomeDecoration) {
            super.applyBiomeDecoration(level, chunk, structureManager);
        }
    }

    @Override
    public void createReferences(WorldGenLevel level, StructureManager structureManager, ChunkAccess chunk) {
        if (this.generateNormal || hasStructures(level.registryAccess())) {
            super.createReferences(level, structureManager, chunk);
        }
    }

    @Override
    public void createStructures(RegistryAccess registries, ChunkGeneratorStructureState structureState, StructureManager structureManager, ChunkAccess chunk, StructureTemplateManager templateManager) {
        if (this.generateNormal || hasStructures(registries)) {
            super.createStructures(registries, structureState, structureManager, chunk, templateManager);
        }
    }

    private boolean hasStructures(RegistryAccess registries) {
        return registries.registryOrThrow(Registries.STRUCTURE_SET).getTagOrEmpty(this.allowedStructureSets).iterator().hasNext();
    }

    private record FilteredLookup(HolderLookup<StructureSet> parent,
                                  TagKey<StructureSet> allowedValues) implements HolderLookup<StructureSet> {
        @Override
        public Optional<Holder.Reference<StructureSet>> get(ResourceKey<StructureSet> key) {
            return this.parent.get(key).filter(holder -> holder.is(this.allowedValues));
        }

        @Override
        public Optional<HolderSet.Named<StructureSet>> get(TagKey<StructureSet> tagKey) {
            return this.parent.get(tagKey);
        }

        @Override
        public Stream<Holder.Reference<StructureSet>> listElements() {
            return this.parent.listElements().filter(holder -> holder.is(this.allowedValues));
        }

        @Override
        public Stream<HolderSet.Named<StructureSet>> listTags() {
            return this.parent.listTags();
        }
    }
}
