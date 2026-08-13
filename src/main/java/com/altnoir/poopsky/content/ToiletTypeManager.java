package com.altnoir.poopsky.content;

import com.altnoir.poopsky.PoopSky;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.resource.ListenerKey;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ToiletTypeManager extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {

    public static final ToiletTypeManager INSTANCE = new ToiletTypeManager();
    public static final ListenerKey<ToiletTypeManager> LISTENER_KEY = ListenerKey.create(PoopSky.loc("toilet_types"));

    public ToiletTypeManager() {
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> result = new HashMap<>();
        for (var entry : resourceManager.listResources("poopsky_data/toilet_type", path -> path.getPath().endsWith(".json")).entrySet()) {
            Identifier location = entry.getKey();
            String path = location.getPath();
            String typePath = path.substring("poopsky_data/toilet_type/".length(), path.length() - ".json".length());
            try (Reader reader = entry.getValue().openAsReader()) {
                result.put(Identifier.fromNamespaceAndPath(location.getNamespace(), typePath), JsonParser.parseReader(reader));
            } catch (Exception e) {
                PoopSky.LOGGER.error("Failed to parse toilet type resource '{}': {}", location, e.getMessage());
            }
        }
        return result;
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
