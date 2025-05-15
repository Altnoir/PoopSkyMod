package com.altnoir.poopsky.villager;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.sound.PSSoundEvents;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class PSVillagers {
    public static final RegistryKey<PointOfInterestType> TOILET_POI = registryPoiKey("toilet");
    public static final PointOfInterestType TOILET_POI_OAK = registerPoi("toilet", ToiletBlocks.OAK_TOILET);
    public static final PointOfInterestType TOILET_POI_SPRUCE = registerPoi("toilet", ToiletBlocks.SPRUCE_TOILET);
    public static final PointOfInterestType TOILET_POI_BIRCH = registerPoi("toilet", ToiletBlocks.BIRCH_TOILET);
    public static final PointOfInterestType TOILET_POI_JUNGLE = registerPoi("toilet", ToiletBlocks.JUNGLE_TOILET);
    public static final PointOfInterestType TOILET_POI_ACACIA = registerPoi("toilet", ToiletBlocks.ACACIA_TOILET);
    public static final PointOfInterestType TOILET_POI_DARK_OAK = registerPoi("toilet", ToiletBlocks.DARK_OAK_TOILET);
    public static final PointOfInterestType TOILET_POI_MANGROVE = registerPoi("toilet", ToiletBlocks.MANGROVE_TOILET);
    public static final PointOfInterestType TOILET_POI_CHERRY = registerPoi("toilet", ToiletBlocks.CHERRY_TOILET);
    public static final PointOfInterestType TOILET_POI_BAMBOO = registerPoi("toilet", ToiletBlocks.BAMBOO_TOILET);
    public static final PointOfInterestType TOILET_POI_CRIMSON = registerPoi("toilet", ToiletBlocks.CRIMSON_TOILET);
    public static final PointOfInterestType TOILET_POI_WARPED = registerPoi("toilet", ToiletBlocks.WARPED_TOILET);

    public static final PointOfInterestType TOILET_POI_STONE = registerPoi("toilet", ToiletBlocks.STONE_TOILET);
    public static final PointOfInterestType TOILET_POI_COBBLESTONE = registerPoi("toilet", ToiletBlocks.COBBLESTONE_TOILET);
    public static final PointOfInterestType TOILET_POI_MOSSY_COBBLESTONE = registerPoi("toilet", ToiletBlocks.MOSSY_COBBLESTONE_TOILET);
    public static final PointOfInterestType TOILET_POI_SMOOTH_STONE = registerPoi("toilet", ToiletBlocks.SMOOTH_STONE_TOILET);
    public static final PointOfInterestType TOILET_POI_STONE_BRICK = registerPoi("toilet", ToiletBlocks.STONE_BRICK_TOILET);
    public static final PointOfInterestType TOILET_POI_MOSSY_STONE_BRICK = registerPoi("toilet", ToiletBlocks.MOSSY_STONE_BRICK_TOILET);

    public static final PointOfInterestType TOILET_POI_WHITE = registerPoi("toilet", ToiletBlocks.WHITE_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_LIGHT_GRAY = registerPoi("toilet", ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_GRAY = registerPoi("toilet", ToiletBlocks.GRAY_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_BLACK = registerPoi("toilet", ToiletBlocks.BLACK_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_BROWN = registerPoi("toilet", ToiletBlocks.BROWN_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_RED = registerPoi("toilet", ToiletBlocks.RED_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_ORANGE = registerPoi("toilet", ToiletBlocks.ORANGE_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_YELLOW = registerPoi("toilet", ToiletBlocks.YELLOW_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_LIME = registerPoi("toilet", ToiletBlocks.LIME_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_GREEN = registerPoi("toilet", ToiletBlocks.GREEN_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_CYAN = registerPoi("toilet", ToiletBlocks.CYAN_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_LIGHT_BLUE = registerPoi("toilet", ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_BLUE = registerPoi("toilet", ToiletBlocks.BLUE_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_PURPLE = registerPoi("toilet", ToiletBlocks.PURPLE_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_MAGENTA = registerPoi("toilet", ToiletBlocks.MAGENTA_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_PINK = registerPoi("toilet", ToiletBlocks.PINK_CONCRETE_TOILET);
    public static final PointOfInterestType TOILET_POI_RAINBOW = registerPoi("toilet", ToiletBlocks.RAINBOW_TOILET);

    public static final VillagerProfession POOP_MAKER = registerProfession("poopmaker", TOILET_POI);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(PoopSky.MOD_ID, name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), PSSoundEvents.ENTITY_VILLAGER_WORK_TOILET));
    }
    private static PointOfInterestType registerPoi(String name, Block block) {
        return PointOfInterestHelper.register(Identifier.of(PoopSky.MOD_ID, name), 1, 1, block);
    }
    private static RegistryKey<PointOfInterestType> registryPoiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(PoopSky.MOD_ID, name));
    }
    public static void registerVillagers() {
        PoopSky.LOGGER.info("Registering villagers for " + PoopSky.MOD_ID);
    }
}
