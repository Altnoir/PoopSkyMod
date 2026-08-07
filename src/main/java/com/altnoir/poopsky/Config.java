package com.altnoir.poopsky;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public enum SpawnToiletMode implements TranslatableEnum {
        WOODEN_TOILET("poopsky.configuration.spawnToiletMode.wooden_toilet"),
        SKY_FLUSH_TOILET("poopsky.configuration.spawnToiletMode.sky_flush_toilet"),
        RANDOM_TOILET("poopsky.configuration.spawnToiletMode.random_toilet");

        private final String translationKey;

        SpawnToiletMode(String translationKey) {
            this.translationKey = translationKey;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable(this.translationKey);
        }
    }

    public static boolean setPoopSkyDefault;
    public static boolean voidNetherGeneration;
    public static boolean strongholdGeneration;
    public static SpawnToiletMode spawnToiletMode;
    public static boolean desperateWorld;
    public static boolean lavaFluid;

    public static boolean compooperCrafting;

    public static boolean plugTrades;
    public static boolean upgradeTemplate;

    public static boolean unlimitedFreeze;
    public static boolean freezeFilter;

    private static ModConfigSpec.BooleanValue booleanOption(String section, String path, boolean defaultValue, String... comments) {
        BUILDER.push(section);
        ModConfigSpec.BooleanValue value = BUILDER
                .comment(comments)
                .translation("poopsky.configuration." + path)
                .define(path, defaultValue);
        BUILDER.pop();
        return value;
    }

    private static <V extends Enum<V>> ModConfigSpec.EnumValue<V> enumOption(String section, String path, V defaultValue, String... comments) {
        BUILDER.push(section);
        ModConfigSpec.EnumValue<V> value = BUILDER
                .comment(comments)
                .translation("poopsky.configuration." + path)
                .defineEnum(path, defaultValue);
        BUILDER.pop();
        return value;
    }

    /**
     * 世界生成相关的配置
     */
    private static final ModConfigSpec.BooleanValue SET_POOPSKY_DEFAULT = booleanOption("world", "setPoopskyDefault", true,
            "Whether the dedicated server level-type default should be set to poopsky");
    private static final ModConfigSpec.BooleanValue VOID_NETHER_GENERATION = booleanOption("world", "voidNetherGeneration", true,
            "Whether the custom void generator should also keep the nether empty");
    private static final ModConfigSpec.BooleanValue STRONGHOLD_GENERATION = booleanOption("world", "strongholdGeneration", true,
            "Whether strongholds should generate in PoopSky worlds");
    private static final ModConfigSpec.EnumValue<SpawnToiletMode> SPAWN_TOILET_MODE = enumOption("world", "spawnToiletMode", SpawnToiletMode.WOODEN_TOILET,
            "Which toilet to generate at the PoopSky spawn point.",
            "Allowed values: WOODEN_TOILET, SKY_FLUSH_TOILET, RANDOM_TOILET");
    private static final ModConfigSpec.BooleanValue DESPERATE_WORLD = booleanOption("world", "desperateWorld", false,
            "Whether to Enable the Desperate World (Enabling it will cause the device to lag)");
    private static final ModConfigSpec.BooleanValue LAVA_FLUID_BLOCK = booleanOption("world", "lavaFluid", true,
            "Whether to Disable the underground lava lake");
    /**
     * 合成相关的配置
     */
    private static final ModConfigSpec.BooleanValue COMPOOPER_CRAFTING = booleanOption("crafting", "compooperCrafting", false,
            "Whether to Disable the consumption of liquid when sticks crafting");
    /**
     * 交易相关的配置
     */
    private static final ModConfigSpec.BooleanValue PLUG_TRADES = booleanOption("trades", "plugTrades", false,
            "Whether to Disable the plug trades");
    private static final ModConfigSpec.BooleanValue UPGRADE_TEMPLATE = booleanOption("trades", "upgradeTemplate", false,
            "Whether to Disable the upgrade template trades");
    /**
     * 时停相关的配置
     */
    private static final ModConfigSpec.BooleanValue UNLIMITED_FREEZE = booleanOption("timeStop", "unlimitedFreeze", false,
            "Whether to Enable the unlimited freeze");
    private static final ModConfigSpec.BooleanValue FREEZE_FILTER = booleanOption("timeStop", "freezeFilter", true,
            "Whether to Disable the freeze filter");

    static final ModConfigSpec SPEC = BUILDER.build();

    public static void onLoad() {
        NeoForgeModConfigEvents.loading(PoopSky.MOD_ID).register(config -> {
            if (config.getSpec() != SPEC) return;
            setPoopSkyDefault = SET_POOPSKY_DEFAULT.get();
            voidNetherGeneration = VOID_NETHER_GENERATION.get();
            strongholdGeneration = STRONGHOLD_GENERATION.get();
            spawnToiletMode = SPAWN_TOILET_MODE.get();
            desperateWorld = DESPERATE_WORLD.get();
            lavaFluid = LAVA_FLUID_BLOCK.get();
            compooperCrafting = COMPOOPER_CRAFTING.get();
            plugTrades = PLUG_TRADES.get();
            upgradeTemplate = UPGRADE_TEMPLATE.get();
            unlimitedFreeze = UNLIMITED_FREEZE.get();
            freezeFilter = FREEZE_FILTER.get();
        });
    }
}
