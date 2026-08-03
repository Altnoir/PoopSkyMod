package com.altnoir.poopsky.impl.type;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FlyTypeData implements DataProvider {
    private final Path outputPath;

    public FlyTypeData(PackOutput packOutput) {
        this.outputPath = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, PoopSky.MOD_ID + "_data")
                .json(PoopSky.loc("fly_types"));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        JsonArray values = new JsonArray();
        for (String id : FlyType.FLY_TYPES) {
            values.add(id);
        }
        json.add("values", values);
        return DataProvider.saveStable(cachedOutput, json, outputPath);
    }

    @Override
    public String getName() {
        return "Fly Types";
    }
}