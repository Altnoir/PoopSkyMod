package com.altnoir.poopsky;

import com.altnoir.abysslib.AbyssLib;
import com.altnoir.abysslib.reginth.Reginth;
import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.compat.create.CreatePlugin;
import com.altnoir.poopsky.compat.maid.MaidPlugin;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletTypeManager;
import com.altnoir.poopsky.content.block.p.CompooperBlock;
import com.altnoir.poopsky.content.villager.PoVillagers;
import com.altnoir.poopsky.data.*;
import com.altnoir.poopsky.data.lang.LangGen;
import com.altnoir.poopsky.impl.event.PoGameEvents;
import com.altnoir.poopsky.impl.event.PoModEvents;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PoChunkGenerators;
import com.altnoir.poopsky.worldgen.PoFeatures;
import com.altnoir.poopsky.worldgen.PoStructures;
import com.altnoir.poopsky.worldgen.foliage.PoFoliagePlacerTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

@Mod(PoopSky.MOD_ID)
public class PoopSky {
    public static final String MOD_ID = "poopsky";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Reginth REGINTH = Reginth.create(MOD_ID);

    static {
        REGINTH.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public PoopSky(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        PoItems.register();
        PoBlocks.register();
        PoFluids.register();
        PoItemGroups.register();
        PoMenuTypes.register();

        PoEffects.register();
        PoPotions.register();
        PoRecipes.register(modEventBus);
        PoComponents.register(modEventBus);
        PoAttachments.register(modEventBus);

        PoEntityType.register();
        PoBlockEntityType.register();
        EntityLootTableGen.register();
        FishingLootGen.register();
        ArcadeLootGen.register();
        GlobalLootModifierGen.register();
        PoLootFunctions.register();
        PoVillagers.register();

        PoParticles.register();
        ParticleGen.register();
        AdvancementGen.register();
        PoSoundEvents.register();
        PoStats.register();

        ItemTagGen.register();
        BlockTagGen.register();
        EntityTypeTagsGen.register();
        FluidTagsGen.register();
        DataMapGen.register();
        LangGen.register();

        PoFoliagePlacerTypes.register();
        PoFeatures.register();
        PoStructures.register();
        PoChunkGenerators.register();

        var gameEventBus = NeoForge.EVENT_BUS;
        PoModEvents.registerMod(modEventBus);
        PoGameEvents.registerGame(gameEventBus);
        gameEventBus.addListener(this::reload);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (ModList.get().isLoaded(PoMods.TOUHOU_LITTLE_MAID.id())) {
            MaidPlugin.registry(modEventBus);
        }
        if (ModList.get().isLoaded(PoMods.CREATE.id())) {
            CreatePlugin.register(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CompooperBlock.bootStrap();
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                    PoBlocks.GINKGO_SAPLING.getId(), PoBlocks.POTTED_GINKGO_SAPLING);
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(
                    PoBlocks.FOLIUM_SENNAE_PLANT.getId(), PoBlocks.POTTED_FOLIUM_SENNAE_PLANT);
            PoDispenserBehaviors.register();
            PoFluidInteractions.register();
        });
        PoStats.init();
    }

    private void reload(final AddReloadListenerEvent event) {
        event.addListener(FlyTypeManager.INSTANCE);
        event.addListener(ToiletTypeManager.INSTANCE);
    }

    public static ResourceLocation loc(String path) {
        return AbyssLib.modloc(MOD_ID, path);
    }

    public static Reginth registrate() {
        return REGINTH;
    }
}
