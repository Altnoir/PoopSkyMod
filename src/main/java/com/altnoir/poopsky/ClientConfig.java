package com.altnoir.poopsky;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static boolean introAnimation = true;
    public static boolean endAnimation = true;
    public static String introText = "poopsky";
    public static String introYear = "2026";

    private static ForgeConfigSpec.BooleanValue booleanOption(String path, boolean defaultValue, String... comments) {
        return BUILDER
                .comment(comments)
                .translation("poopsky.configuration." + path)
                .define(path, defaultValue);
    }

    private static ForgeConfigSpec.ConfigValue<String> stringOption(String path, String defaultValue) {
        return BUILDER
                .translation("poopsky.configuration." + path)
                .define(path, defaultValue);
    }

    private static final ForgeConfigSpec.BooleanValue INTRO_ANIMATION = booleanOption("introAnimation", true,
            "Whether to play the intro animation when entering a PoopSky world for the first time");
    private static final ForgeConfigSpec.BooleanValue END_ANIMATION = booleanOption("endAnimation", true,
            "Whether to play the Toilet End Poem before traveling through an End Toilet for the first time");
    private static final ForgeConfigSpec.ConfigValue<String> INTRO_TEXT = stringOption("introText", "poopsky");
    private static final ForgeConfigSpec.ConfigValue<String> INTRO_YEAR = stringOption("introYear", "2026");

    public static final ForgeConfigSpec CLIENT_SPEC = BUILDER.build();

    public static void onLoad(ModConfig config) {
        if (config.getSpec() == CLIENT_SPEC) {
            introAnimation = INTRO_ANIMATION.get();
            endAnimation = END_ANIMATION.get();
            introText = INTRO_TEXT.get();
            introYear = INTRO_YEAR.get();
        }
    }
}
