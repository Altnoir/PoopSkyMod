package com.altnoir.poopsky;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class Config {
    public static boolean setPoopSkyDefault;
    public static boolean voidNetherGeneration;
    public static boolean strongholdGeneration;
    public static boolean desperateWorld;
    public static boolean compooperCrafting;
    public static boolean lavaFluid;
    public static boolean plugTrades;
    public static boolean upgradeTemplate;
    public static boolean unlimitedFreeze;

    public static boolean freezeFilter;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SET_POOPSKY_DEFAULT = BUILDER
            .comment("Common")
            .comment("Whether the dedicated server level-type default should be set to poopsky")
            .translation("poopsky.configuration.setPoopskyDefault")
            .define("setPoopskyDefault", true);
    private static final ModConfigSpec.BooleanValue VOID_NETHER_GENERATION = BUILDER
            .comment("Whether the custom void generator should also keep the nether empty")
            .translation("poopsky.configuration.voidNetherGeneration")
            .define("voidNetherGeneration", true);
    private static final ModConfigSpec.BooleanValue STRONGHOLD_GENERATION = BUILDER
            .comment("Whether strongholds should generate in PoopSky worlds")
            .translation("poopsky.configuration.strongholdGeneration")
            .define("strongholdGeneration", true);
    private static final ModConfigSpec.BooleanValue DESPERATE_WORLD = BUILDER
            .comment("Whether to Enable the Desperate World (Enabling it will cause the device to lag)")
            .translation("poopsky.configuration.desperateWorld")
            .define("desperateWorld", false);
    private static final ModConfigSpec.BooleanValue COMPOOPER_CRAFTING = BUILDER
            .comment("Whether to Disable the consumption of liquid when sticks crafting")
            .translation("poopsky.configuration.compooperCrafting")
            .define("compooperCrafting", false);
    private static final ModConfigSpec.BooleanValue LAVA_FLUID_BLOCK = BUILDER
            .comment("Whether to Disable the underground lava lake")
            .translation("poopsky.configuration.lavaFluid")
            .define("lavaFluid", true);
    private static final ModConfigSpec.BooleanValue PLUG_TRADES = BUILDER
            .comment("Whether to Disable the plug trades")
            .translation("poopsky.configuration.plugTrades")
            .define("plugTrades", false);
    private static final ModConfigSpec.BooleanValue UPGRADE_TEMPLATE = BUILDER
            .comment("Whether to Disable the upgrade template trades")
            .translation("poopsky.configuration.upgradeTemplate")
            .define("upgradeTemplate", false);
    private static final ModConfigSpec.BooleanValue UNLIMITED_FREEZE = BUILDER
            .comment("Whether to Enable the unlimited freeze")
            .translation("poopsky.configuration.unlimitedFreeze")
            .define("unlimitedFreeze", false);

    private static final ModConfigSpec.BooleanValue FREEZE_FILTER = BUILDER
            .comment("Client")
            .comment("Whether to Disable the freeze filter")
            .translation("poopsky.configuration.freezeFilter")
            .define("freezeFilter", true);


    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        setPoopSkyDefault = SET_POOPSKY_DEFAULT.get();
        voidNetherGeneration = VOID_NETHER_GENERATION.get();
        strongholdGeneration = STRONGHOLD_GENERATION.get();
        desperateWorld = DESPERATE_WORLD.get();
        compooperCrafting = COMPOOPER_CRAFTING.get();
        lavaFluid = LAVA_FLUID_BLOCK.get();
        plugTrades = PLUG_TRADES.get();
        upgradeTemplate = UPGRADE_TEMPLATE.get();
        unlimitedFreeze = UNLIMITED_FREEZE.get();

        freezeFilter = FREEZE_FILTER.get();
    }
}
