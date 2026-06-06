package com.altnoir.poopsky;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class Config {
    public static boolean lavaFluid;
    public static boolean stickyCrafting;
    public static boolean desperateWorld;
    public static boolean setPoopSkyDefault;
    public static boolean voidNetherGeneration;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue LAVA_FLUID_BLOCK = BUILDER
            .comment("Whether to Disable the underground lava lake")
            .translation("poopsky.configuration.lavaFluid")
            .define("lavaFluid", true);
    private static final ModConfigSpec.BooleanValue STICK_CRAFTING = BUILDER
            .comment("Whether to Disable the consumption of liquid when sticks crafting")
            .translation("poopsky.configuration.stickyCrafting")
            .define("stickyCrafting", false);
    private static final ModConfigSpec.BooleanValue DESPERATE_WORLD = BUILDER
            .comment("Whether to Enable the Desperate World (Enabling it will cause the device to lag)")
            .translation("poopsky.configuration.desperateWorld")
            .define("desperateWorld", false);
    private static final ModConfigSpec.BooleanValue SET_POOPSKY_DEFAULT = BUILDER
            .comment("Whether the dedicated server level-type default should be set to poopsky")
            .translation("poopsky.configuration.setPoopskyDefault")
            .define("setPoopskyDefault", false);
    private static final ModConfigSpec.BooleanValue VOID_NETHER_GENERATION = BUILDER
            .comment("Whether the custom void generator should also keep the nether empty")
            .translation("poopsky.configuration.voidNetherGeneration")
            .define("voidNetherGeneration", true);
    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        lavaFluid = LAVA_FLUID_BLOCK.get();
        stickyCrafting = STICK_CRAFTING.get();
        desperateWorld = DESPERATE_WORLD.get();
        setPoopSkyDefault = SET_POOPSKY_DEFAULT.get();
        voidNetherGeneration = VOID_NETHER_GENERATION.get();
    }
}
