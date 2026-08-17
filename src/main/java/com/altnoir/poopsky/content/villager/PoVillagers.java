package com.altnoir.poopsky.content.villager;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.function.Supplier;

public class PoVillagers {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final ResourceKey<PoiType> COMPOOPER_POI_KEY = registryPoiKey("compooper");
    public static final ResourceKey<PoiType> TOILET_POI_KEY = registryPoiKey("toilet");

    public static final Supplier<Set<BlockState>> COMPOOPER_POI = () -> ImmutableList.of(
                    PoBlocks.COMPOOPER.get(),
                    PoBlocks.WATER_COMPOOPER.get(),
                    PoBlocks.LAVA_COMPOOPER.get(),
                    PoBlocks.POWDER_SNOW_COMPOOPER.get(),
                    PoBlocks.URINE_COMPOOPER.get()
            ).stream()
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .collect(ImmutableSet.toImmutableSet());

    public static final Supplier<Set<BlockState>> TOILET_POI = () -> ImmutableList.of(
                    PoBlocks.WOODEN_TOILET.get(),
                    PoBlocks.HARD_TOILET.get()
            ).stream()
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(state -> state.getBlock() instanceof AbstractToiletBlock)
            .collect(ImmutableSet.toImmutableSet());

    public static final RegistryEntry<VillagerProfession, VillagerProfession> POOP_MAKER =
            registerProfession("poopmaker", COMPOOPER_POI_KEY, PoSoundEvents.ENTITY_VILLAGER_WORK_COMPOOPER);

    public static final RegistryEntry<VillagerProfession, VillagerProfession> GASTRONOME =
            registerProfession("gastronome", TOILET_POI_KEY, PoSoundEvents.ENTITY_VILLAGER_WORK_TOILET);

    public static void init() {
    }

    public static void register() {
        PointOfInterestHelper.register(PoopSky.loc("compooper"), 1, 1, COMPOOPER_POI.get());
        PointOfInterestHelper.register(PoopSky.loc("toilet"), 1, 1, TOILET_POI.get());
    }

    private static RegistryEntry<VillagerProfession, VillagerProfession> registerProfession(
            String name,
            ResourceKey<PoiType> poiKey,
            RegistryEntry<SoundEvent, SoundEvent> workSound) {
        return REGISTRATE.simple(name, Registries.VILLAGER_PROFESSION, () -> new VillagerProfession(
                name,
                holder -> holder.is(poiKey),
                holder -> holder.is(poiKey),
                ImmutableSet.of(),
                ImmutableSet.of(),
                workSound.get()
        ));
    }

    private static ResourceKey<PoiType> registryPoiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, PoopSky.loc(name));
    }
}