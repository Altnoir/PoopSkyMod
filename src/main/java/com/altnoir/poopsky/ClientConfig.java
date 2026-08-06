package com.altnoir.poopsky;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static boolean introAnimation = true;
    public static String introText = "poopsky";
    public static String introYear = "2026";

    private static ModConfigSpec.BooleanValue booleanOption(String path, boolean defaultValue, String... comments) {
        ModConfigSpec.BooleanValue value = BUILDER
                .comment(comments)
                .translation("poopsky.configuration." + path)
                .define(path, defaultValue);
        return value;
    }

    private static ModConfigSpec.ConfigValue<String> stringOption(String path, String defaultValue) {
        ModConfigSpec.ConfigValue<String> value = BUILDER
                .translation("poopsky.configuration." + path)
                .define(path, defaultValue);
        return value;
    }

    private static final ModConfigSpec.BooleanValue INTRO_ANIMATION = booleanOption("introAnimation", true,
            "Whether to play the intro animation when entering a PoopSky world for the first time");
    private static final ModConfigSpec.ConfigValue<String> INTRO_TEXT = stringOption("introText", "poopsky");
    private static final ModConfigSpec.ConfigValue<String> INTRO_YEAR = stringOption("introYear", "2026");

    public static final ModConfigSpec CLIENT_SPEC = BUILDER.build();

    public static void onLoad() {
        NeoForgeModConfigEvents.loading(PoopSky.MOD_ID).register(config -> {
            if (config.getSpec() != CLIENT_SPEC) return;
            introAnimation = INTRO_ANIMATION.get();
            introText = INTRO_TEXT.get();
            introYear = INTRO_YEAR.get();
        });
    }
}
