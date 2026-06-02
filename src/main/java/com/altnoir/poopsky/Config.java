package com.altnoir.poopsky;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Set;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class Config {
    public static boolean lavaFluid;
    public static boolean stickyCrafting;
    public static boolean desperateWorld;
    public static boolean setPoopSkyDefault;
    public static boolean voidNetherGeneration;
    public static boolean voidEndGeneration;

    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue LAVA_FLUID_BLOCK = BUILDER
            .comment("Whether to Disable the underground lava lake")
            .define("lavaFluid", true);
    private static final ModConfigSpec.BooleanValue STICK_CREAFTING = BUILDER
            .comment("Whether to Disable the consumption of liquid when sticks crafting")
            .define("stickyCrafting", false);
    private static final ModConfigSpec.BooleanValue DESPERATE_WORLD = BUILDER
            .comment("Whether to Enable the Desperate World (Enabling it will cause the device to lag)")
            .define("desperateWorld", false);
    private static final ModConfigSpec.BooleanValue SET_POOPSKY_DEFAULT = BUILDER
            .comment("Whether the dedicated server level-type default should be set to poopsky")
            .define("setPoopskyDefault", true);
    private static final ModConfigSpec.BooleanValue VOID_NETHER_GENERATION = BUILDER
            .comment("Whether the custom void generator should also keep the nether empty")
            .define("voidNetherGeneration", true);
    private static final ModConfigSpec.BooleanValue VOID_END_GENERATION = BUILDER
            .comment("Whether the custom void generator should also keep the end empty")
            .define("voidEndGeneration", true);

//    private static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
//            .comment("A magic number")
//            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);
//
//    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
//            .comment("What you want the introduction message to be for the magic number")
//            .define("magicNumberIntroduction", "The magic number is... ");
//
//    // a list of strings that are treated as resource locations for items
//    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
//            .comment("A list of items to log on common setup.")
//            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        lavaFluid = LAVA_FLUID_BLOCK.get();
        stickyCrafting = STICK_CREAFTING.get();
        desperateWorld = DESPERATE_WORLD.get();
        setPoopSkyDefault = SET_POOPSKY_DEFAULT.get();
        voidNetherGeneration = VOID_NETHER_GENERATION.get();
        voidEndGeneration = VOID_END_GENERATION.get();

//        magicNumber = MAGIC_NUMBER.get();
//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream()
//                .map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)))
//                .collect(Collectors.toSet());
    }
}
