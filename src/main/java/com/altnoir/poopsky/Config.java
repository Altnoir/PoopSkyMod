package com.altnoir.poopsky;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class Config {
    public static boolean setPoopSkyDefault;
    public static boolean voidNetherGeneration;
    public static boolean desperateWorld;
    public static boolean stickyCrafting;
    public static boolean lavaFluid;
    public static boolean plugTrades;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SET_POOPSKY_DEFAULT = BUILDER
            .comment("Whether the dedicated server level-type default should be set to poopsky")
            .translation("poopsky.configuration.setPoopskyDefault")
            .define("setPoopskyDefault", true);
    private static final ModConfigSpec.BooleanValue VOID_NETHER_GENERATION = BUILDER
            .comment("Whether the custom void generator should also keep the nether empty")
            .translation("poopsky.configuration.voidNetherGeneration")
            .define("voidNetherGeneration", true);
    private static final ModConfigSpec.BooleanValue DESPERATE_WORLD = BUILDER
            .comment("Whether to Enable the Desperate World (Enabling it will cause the device to lag)")
            .translation("poopsky.configuration.desperateWorld")
            .define("desperateWorld", false);
    private static final ModConfigSpec.BooleanValue STICK_CRAFTING = BUILDER
            .comment("Whether to Disable the consumption of liquid when sticks crafting")
            .translation("poopsky.configuration.stickyCrafting")
            .define("stickyCrafting", false);
    private static final ModConfigSpec.BooleanValue PLUG_TRADES = BUILDER
            .comment("Whether to Disable the plug trades")
            .translation("poopsky.configuration.plugTrades")
            .define("plugTrades", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        setPoopSkyDefault = SET_POOPSKY_DEFAULT.get();
        voidNetherGeneration = VOID_NETHER_GENERATION.get();
        desperateWorld = DESPERATE_WORLD.get();
        stickyCrafting = STICK_CRAFTING.get();
        plugTrades = PLUG_TRADES.get();
    }
}
