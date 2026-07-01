package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.FlyType;
import com.google.gson.JsonArray;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class PFlyTypeProvider implements DataProvider {
    private final Path outputPath;

    public PFlyTypeProvider(PackOutput packOutput) {
        this.outputPath = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, PoopSky.MOD_ID)
                .json(PoopSky.loc("fly_types"));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        JsonArray json = new JsonArray();
        for (String id : FlyType.FLY_TYPES) {
            json.add(id);
        }
        return DataProvider.saveStable(cachedOutput, json, outputPath);
    }

    @Override
    public String getName() {
        return "Fly Types";
    }
}