package com.altnoir.poopsky.villager;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PSoundEvents;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class PVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, PoopSky.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, PoopSky.MOD_ID);

    public static final ResourceKey<PoiType> COMPOOPER_POI_KEY = registryPoiKey("compooper");
    public static final ResourceKey<PoiType> TOILET_POI_KEY = registryPoiKey("toilet");

    public static final Supplier<Set<BlockState>> COMPOOPER_POI = () -> ImmutableList.of(
                    PBlocks.COMPOOPER.get(),
                    PBlocks.WATER_COMPOOPER.get(),
                    PBlocks.LAVA_COMPOOPER.get(),
                    PBlocks.POWDER_SNOW_COMPOOPER.get(),
                    PBlocks.URINE_COMPOOPER.get()
            ).stream()
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .collect(ImmutableSet.toImmutableSet());
    public static final Supplier<Set<BlockState>> TOILET_POI = () -> ImmutableList.of(
                    PBlocks.WOODEN_TOILET.get(),
                    PBlocks.HARD_TOILET.get()
            ).stream()
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(state -> state.getBlock() instanceof AbstractToiletBlock)
            .collect(ImmutableSet.toImmutableSet());

    public static final Holder<PoiType> COMPOOPER_POI_TYPE = POI_TYPES.register("compooper", () -> new PoiType(COMPOOPER_POI.get(), 1, 1));
    public static final Holder<PoiType> TOILET_POI_TYPE = POI_TYPES.register("toilet", () -> new PoiType(TOILET_POI.get(), 1, 1));

    public static final Holder<VillagerProfession> POOP_MAKER = VILLAGER_PROFESSIONS.register("poopmaker", () ->
            new VillagerProfession("poopmaker", holder -> holder.is(COMPOOPER_POI_KEY),
                    poiTypeHolder -> poiTypeHolder.is(COMPOOPER_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    PSoundEvents.ENTITY_VILLAGER_WORK_COMPOOPER.get()));

    public static final Holder<VillagerProfession> GASTRONOME = VILLAGER_PROFESSIONS.register("gastronome", () ->
            new VillagerProfession("gastronome", holder -> holder.is(TOILET_POI_KEY),
                    poiTypeHolder -> poiTypeHolder.is(TOILET_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    PSoundEvents.ENTITY_VILLAGER_WORK_TOILET.get()));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }

    private static ResourceKey<PoiType> registryPoiKey(String name) {
        return ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), PoopSky.loc(name));
    }
}