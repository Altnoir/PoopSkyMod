package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PItemGroups;
import com.altnoir.poopsky.content.villager.PVillagers;
import com.altnoir.poopsky.datagen.PSAdvancementProvider;
import com.altnoir.poopsky.datagen.PSEntityLootTableProvider;
import com.altnoir.poopsky.worldgen.PSChunkGenerators;
import com.altnoir.poopsky.worldgen.PSStructures;
import com.altnoir.poopsky.worldgen.foliage.PSFoliagePlacerTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PRegistries {
    private static final List<DeferredRegister<?>> REGISTERS = new ArrayList<>();

    private PRegistries() {
    }

    public static void registerAll(IEventBus modEventBus) {
        PItems.register();
        PBlocks.register();
        PFluids.register();
        PParticles.register();
        PSAdvancementProvider.register();
        PSEntityLootTableProvider.register();

        PEffects.register(modEventBus);
        PPotions.register(modEventBus);
        PBlockEntityType.register(modEventBus);
        PEntityType.register(modEventBus);
        PSFoliagePlacerTypes.register(modEventBus);
        PSStructures.register(modEventBus);
        PSChunkGenerators.register(modEventBus);
        PItemGroups.register(modEventBus);
        PSoundEvents.register(modEventBus);
        PStats.register(modEventBus);
        PComponents.register(modEventBus);
        PLootFunctions.register(modEventBus);
        PVillagers.register(modEventBus);
        PRecipes.register(modEventBus);
        PMenuTypes.register(modEventBus);

        for (DeferredRegister<?> register : REGISTERS) {
            register.register(modEventBus);
        }
    }

    public static DeferredRegister<?> add(DeferredRegister<?> register) {
        REGISTERS.add(register);
        return register;
    }

    public static List<DeferredRegister<?>> getRegisters() {
        return Collections.unmodifiableList(REGISTERS);
    }
}
