package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.abysslib.reginth.Reginth;
import com.altnoir.poopsky.worldgen.structure.PoopIslandPiece;
import com.altnoir.poopsky.worldgen.structure.PoopIslandStructure;
import com.altnoir.abysslib.reginth.util.entry.RegistryEntry;
import com.altnoir.abysslib.reginth.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class PoStructures {
    private static final Reginth REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<StructureType<?>, StructureType<PoopIslandStructure>> POOP_ISLAND = registerStructureType(
            "poop_island", () -> () -> PoopIslandStructure.CODEC);
    public static final RegistryEntry<StructurePieceType, StructurePieceType> POOP_ISLAND_PIECE = registerStructurePiece(
            "poop_island", () -> (StructurePieceType.StructureTemplateType) PoopIslandPiece::new);

    private static <T extends StructureType<?>> RegistryEntry<StructureType<?>, T> registerStructureType(String name, NonNullSupplier<T> type) {
        return REGISTRATE.simple(name, Registries.STRUCTURE_TYPE, type);
    }

    private static RegistryEntry<StructurePieceType, StructurePieceType> registerStructurePiece(String name, NonNullSupplier<StructurePieceType> type) {
        return REGISTRATE.simple(name, Registries.STRUCTURE_PIECE, type);
    }

    public static void register() {
    }
}