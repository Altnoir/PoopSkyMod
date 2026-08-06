package com.altnoir.poopsky;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@OnlyIn(Dist.CLIENT)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static boolean introAnimation = true;
    public static boolean endAnimation = true;
    public static String introText = "poopsky";
    public static String introYear = "2026";

    private static ModConfigSpec.BooleanValue booleanOption(String path, boolean defaultValue, String... comments) {
        return BUILDER
                .comment(comments)
                .translation("poopsky.configuration." + path)
                .define(path, defaultValue);
    }

    private static ModConfigSpec.ConfigValue<String> stringOption(String path, String defaultValue) {
        return BUILDER
                .translation("poopsky.configuration." + path)
                .define(path, defaultValue);
    }

    private static final ModConfigSpec.BooleanValue INTRO_ANIMATION = booleanOption("introAnimation", true,
            "Whether to play the intro animation when entering a PoopSky world for the first time");
    private static final ModConfigSpec.BooleanValue END_ANIMATION = booleanOption("endAnimation", true,
            "Whether to play the Toilet End Poem before traveling through an End Toilet for the first time");
    private static final ModConfigSpec.ConfigValue<String> INTRO_TEXT = stringOption("introText", "poopsky");
    private static final ModConfigSpec.ConfigValue<String> INTRO_YEAR = stringOption("introYear", "2026");

    public static final ModConfigSpec CLIENT_SPEC = BUILDER.build();

    public static void onLoad(ModConfig config) {
        if (config.getSpec() == CLIENT_SPEC) {
            introAnimation = INTRO_ANIMATION.get();
            endAnimation = END_ANIMATION.get();
            introText = INTRO_TEXT.get();
            introYear = INTRO_YEAR.get();
        }
    }
}
