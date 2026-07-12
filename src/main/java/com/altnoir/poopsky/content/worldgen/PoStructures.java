package com.altnoir.poopsky.content.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.content.worldgen.structure.PoopIslandPiece;
import com.altnoir.poopsky.content.worldgen.structure.PoopIslandStructure;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class PoStructures {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

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