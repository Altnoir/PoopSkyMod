package com.altnoir.poopsky.villager;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.AllToiletBlocks;
import com.altnoir.poopsky.sound.PSSoundEvents;
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

public class PSVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, PoopSky.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, PoopSky.MOD_ID);

    public static final ResourceKey<PoiType> COMPOOPER_POI_KEY = registryPoiKey("compooper");
    public static final ResourceKey<PoiType> TOILET_POI_KEY = registryPoiKey("toilet");

    public static final Supplier<Set<BlockState>> COMPOOPER_POI = () -> ImmutableList.of(
                    PSBlocks.COMPOOPER.get(),
                    PSBlocks.WATER_COMPOOPER.get(),
                    PSBlocks.LAVA_COMPOOPER.get(),
                    PSBlocks.POWER_SNOW_COMPOOPER.get(),
                    PSBlocks.URINE_COMPOOPER.get()
            ).stream()
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .collect(ImmutableSet.toImmutableSet());
    public static final Supplier<Set<BlockState>> TOILET_POI = () -> ImmutableList.of(
                    AllToiletBlocks.OAK_TOILET.get(),
                    AllToiletBlocks.SPRUCE_TOILET.get(),
                    AllToiletBlocks.BIRCH_TOILET.get(),
                    AllToiletBlocks.JUNGLE_TOILET.get(),
                    AllToiletBlocks.ACACIA_TOILET.get(),
                    AllToiletBlocks.CHERRY_TOILET.get(),
                    AllToiletBlocks.DARK_OAK_TOILET.get(),
                    AllToiletBlocks.MANGROVE_TOILET.get(),
                    AllToiletBlocks.BAMBOO_TOILET.get(),

                    AllToiletBlocks.STONE_TOILET.get(),
                    AllToiletBlocks.COBBLESTONE_TOILET.get(),
                    AllToiletBlocks.MOSSY_COBBLESTONE_TOILET.get(),
                    AllToiletBlocks.SMOOTH_STONE_TOILET.get(),
                    AllToiletBlocks.STONE_BRICK_TOILET.get(),
                    AllToiletBlocks.MOSSY_STONE_BRICK_TOILET.get(),
                    AllToiletBlocks.TILE_TOILET.get(),

                    AllToiletBlocks.WHITE_CONCRETE_TOILET.get(),
                    AllToiletBlocks.ORANGE_CONCRETE_TOILET.get(),
                    AllToiletBlocks.MAGENTA_CONCRETE_TOILET.get(),
                    AllToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET.get(),
                    AllToiletBlocks.YELLOW_CONCRETE_TOILET.get(),
                    AllToiletBlocks.LIME_CONCRETE_TOILET.get(),
                    AllToiletBlocks.PINK_CONCRETE_TOILET.get(),
                    AllToiletBlocks.GRAY_CONCRETE_TOILET.get(),
                    AllToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET.get(),
                    AllToiletBlocks.CYAN_CONCRETE_TOILET.get(),
                    AllToiletBlocks.PURPLE_CONCRETE_TOILET.get(),
                    AllToiletBlocks.BLUE_CONCRETE_TOILET.get(),
                    AllToiletBlocks.BROWN_CONCRETE_TOILET.get(),
                    AllToiletBlocks.GREEN_CONCRETE_TOILET.get(),
                    AllToiletBlocks.RED_CONCRETE_TOILET.get(),
                    AllToiletBlocks.BLACK_CONCRETE_TOILET.get(),
                    AllToiletBlocks.RAINBOW_TOILET.get()
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
                    PSSoundEvents.ENTITY_VILLAGER_WORK_COMPOOPER.get()));

    public static final Holder<VillagerProfession> GASTRONOME = VILLAGER_PROFESSIONS.register("gastronome", () ->
            new VillagerProfession("gastronome", holder -> holder.is(TOILET_POI_KEY),
                    poiTypeHolder -> poiTypeHolder.is(TOILET_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    PSSoundEvents.ENTITY_VILLAGER_WORK_TOILET.get()));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }

    private static ResourceKey<PoiType> registryPoiKey(String name) {
        return ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), PoopSky.loc(name));
    }
}
