package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.MaggotsChunkLoaderBlockEntity;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.entity.p.PoolimeEntity;
import com.altnoir.poopsky.impl.network.PoNetworking;
import com.altnoir.poopsky.init.PoEntityType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.resource.PathPackResources;

public class PoModEvents {
    public static void registerMod(IEventBus modEventBus) {
        PoNetworking.registerNetworking();
        modEventBus.addListener(PoModEvents::modLoad);
        modEventBus.addListener(PoModEvents::registerAttributes);
        modEventBus.addListener(PoModEvents::registerSpawnPlacements);
        modEventBus.addListener(PoModEvents::packSetup);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, PoBlockCapabilities::attach);
        ForgeChunkManager.setForcedChunkLoadingCallback(PoopSky.MOD_ID, MaggotsChunkLoaderBlockEntity::validateTickets);
    }
    public static void modLoad(ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Loading || event instanceof ModConfigEvent.Reloading) Config.onLoad(event.getConfig());
    }
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(PoEntityType.POOLIME.get(), PoolimeEntity.createAttributes().build());
        event.put(PoEntityType.FLY.get(), FlyEntity.createAttributes().build());
    }
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(PoEntityType.POOLIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PoolimeEntity::checkPoolimeSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(PoEntityType.FLY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                FlyEntity::checkFlySpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
    public static void packSetup(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
        addPack(event, "poopsky_official_pack", "pack.poopsky.official.name");
        addPack(event, "poopsky_community_pack", "pack.poopsky.community.name");
    }
    private static void addPack(AddPackFindersEvent event, String directory, String translation) {
        var path = ModList.get().getModFileById(PoopSky.MOD_ID).getFile().findResource("resourcepacks", directory);
        event.addRepositorySource(consumer -> {
            Pack pack = Pack.readMetaAndCreate(PoopSky.MOD_ID + ":" + directory, Component.translatable(translation), false,
                    id -> new PathPackResources(id, true, path), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
            if (pack != null) consumer.accept(pack);
        });
    }
}
