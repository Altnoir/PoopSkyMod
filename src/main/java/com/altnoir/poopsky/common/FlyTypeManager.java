package com.altnoir.poopsky.common;

import com.altnoir.poopsky.PoopSky;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;

public class FlyTypeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public static final FlyTypeManager INSTANCE = new FlyTypeManager();

    private volatile List<String> flyTypes = FlyType.FLY_TYPES;

    public FlyTypeManager() {
        super(GSON, "fly_type");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (var entry : resources.entrySet()) {
            if ("fly_types".equals(entry.getKey().getPath())) {
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    builder.add(element.getAsString());
                }
                break;
            }
        }
        List<String> loaded = builder.build();
        if (loaded.isEmpty()) {
            loaded = FlyType.FLY_TYPES;
        }
        this.flyTypes = loaded;
        PoopSky.LOGGER.info("Loaded {} fly types from data pack", loaded.size());
    }

    public List<String> getFlyTypes() {
        return flyTypes;
    }

    public int size() {
        return flyTypes.size();
    }

    public int getIndex(String id) {
        int index = flyTypes.indexOf(id);
        return Math.max(index, 0);
    }

    public boolean isValid(String id) {
        return flyTypes.contains(id);
    }
}