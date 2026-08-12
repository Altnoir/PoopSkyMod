package com.altnoir.poopsky.content;

import com.altnoir.poopsky.PoopSky;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.resource.ListenerKey;

import java.util.Map;

public class ToiletTypeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public static final ToiletTypeManager INSTANCE = new ToiletTypeManager();
    public static final ListenerKey<ToiletTypeManager> LISTENER_KEY = ListenerKey.create(PoopSky.loc("toilet_types"));

    public ToiletTypeManager() {
        super(GSON, "poopsky_data/toilet_type");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        int count = 0;
        for (var entry : resources.entrySet()) {
            String id = entry.getKey().getPath();
            JsonElement json = entry.getValue();
            try {
                ToiletType.parseAndRegister(id, json);
                count++;
            } catch (Exception e) {
                PoopSky.LOGGER.error("Failed to load toilet type '{}': {}", id, e.getMessage());
            }
        }
        if (count > 0) {
            PoopSky.LOGGER.info("Loaded {} toilet type(s) from data packs", count);
        }
    }
}
