package com.altnoir.poopsky.event.asm;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.worldgen.PSVoidChunkGenerator;
import com.altnoir.poopsky.worldgen.PSWorldPresets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.fml.ModList;

import java.util.Properties;

@SuppressWarnings("unused")
public final class ASMHooks {
    /**
     * Called in {@link net.minecraft.world.level.levelgen.structure.structures.EndCityStructure#findGenerationPoint(Structure.GenerationContext)}
     * to fix End Cities not generating in void worlds.
     */
    public static BlockPos adjustPos(BlockPos pos, Structure.GenerationContext context) {
        if (context.chunkGenerator().getClass() == PSVoidChunkGenerator.class) {
            return new BlockPos(pos.getX(), 64, pos.getZ());
        } else {
            return pos;
        }
    }

    /**
     * Called in {@link net.minecraft.world.level.dimension.end.EndDragonFight#spawnExitPortal(boolean)}
     * right before {@code EndPodiumFeature.place} is called to fix End Portal not spawning fully,
     * with part of it being generated outside the world in the void.
     */
    public static BlockPos prePlaceEndPodium(BlockPos pos) {
        if (pos.getY() < 4) {
            return pos.above(32);
        } else {
            return pos.immutable();
        }
    }

    /**
     * Called in {@link net.minecraft.server.dedicated.DedicatedServerProperties#DedicatedServerProperties(Properties)}
     * where {@code WorldPresets.NORMAL} is used in the line that looks like {@code WorldPresets.NORMAL.location().toString()}
     */
    public static ResourceKey<WorldPreset> overrideDefaultWorldPreset() {
        if (ModList.get().isLoaded("skyblockbuilder")) {
            return ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath("skyblockbuilder", "skyblock"));
        }
        return Config.setPoopSkyDefault ? PSWorldPresets.POOPSKY : WorldPresets.NORMAL;
    }
}