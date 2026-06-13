package com.altnoir.poopsky.worldgen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.worldgen.structure.PoopIslandPiece;
import com.altnoir.poopsky.worldgen.structure.PoopIslandStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PSStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, PoopSky.MOD_ID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, PoopSky.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<PoopIslandStructure>> POOP_ISLAND =
            STRUCTURE_TYPES.register("poop_island", () -> () -> PoopIslandStructure.CODEC);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> POOP_ISLAND_PIECE =
            STRUCTURE_PIECES.register("poop_island", () -> (StructurePieceType.StructureTemplateType) PoopIslandPiece::new);

    private PSStructures() {
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
        STRUCTURE_PIECES.register(eventBus);
    }
}
