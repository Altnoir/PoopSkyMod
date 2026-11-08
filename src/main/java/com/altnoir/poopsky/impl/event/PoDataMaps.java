package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.PoopSky;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** 回移 Hotfix2 的三种数据表，同时允许服务端数据包覆盖。 */
@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID)
public final class PoDataMaps {
    private static final String[] PATHS = {"item/compostables", "item/furnace_fuels", "block/strippables"};
    private static final Map<Item, Integer> FUELS = new HashMap<>();
    private static final Map<Block, Block> STRIPPABLES = new HashMap<>();
    private static final Map<Item, Float> OLD_COMPOST = new HashMap<>();
    private PoDataMaps() {}

    public static void bootstrap() {
        Map<String, JsonObject> tables = new HashMap<>();
        for (String path : PATHS) {
            try (var stream = PoDataMaps.class.getResourceAsStream("/data/forge/data_maps/" + path + ".json")) {
                if (stream != null) try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    tables.put(path, JsonParser.parseReader(reader).getAsJsonObject().getAsJsonObject("values"));
                }
            } catch (Exception exception) { throw new IllegalStateException("Cannot load PoopSky data map " + path, exception); }
        }
        applyTables(tables);
    }
    private static synchronized void applyTables(Map<String, JsonObject> tables) {
        OLD_COMPOST.forEach((item, old) -> {
            if (old < 0) ComposterBlock.COMPOSTABLES.removeFloat(item);
            else ComposterBlock.COMPOSTABLES.put(item, old.floatValue());
        });
        OLD_COMPOST.clear(); FUELS.clear(); STRIPPABLES.clear();
        tables.forEach((path, values) -> values.entrySet().forEach(entry -> {
            ResourceLocation id = new ResourceLocation(entry.getKey());
            JsonObject value = entry.getValue().getAsJsonObject();
            if (value.has("value")) value = value.getAsJsonObject("value");
            if (path.equals("block/strippables")) {
                ResourceLocation target = new ResourceLocation(value.get("stripped_block").getAsString());
                if (BuiltInRegistries.BLOCK.containsKey(id) && BuiltInRegistries.BLOCK.containsKey(target))
                    STRIPPABLES.put(BuiltInRegistries.BLOCK.get(id), BuiltInRegistries.BLOCK.get(target));
            } else if (BuiltInRegistries.ITEM.containsKey(id)) {
                Item item = BuiltInRegistries.ITEM.get(id);
                if (path.equals("item/furnace_fuels")) FUELS.put(item, value.get("burn_time").getAsInt());
                else {
                    OLD_COMPOST.put(item, ComposterBlock.COMPOSTABLES.getOrDefault(item, -1.0F));
                    ComposterBlock.COMPOSTABLES.put(item, value.get("chance").getAsFloat());
                }
            }
        }));
    }
    @SubscribeEvent public static void fuel(FurnaceFuelBurnTimeEvent event) {
        Integer ticks = FUELS.get(event.getItemStack().getItem());
        if (ticks != null) event.setBurnTime(ticks);
    }
    @SubscribeEvent public static void strip(BlockEvent.BlockToolModificationEvent event) {
        if (event.getToolAction() != ToolActions.AXE_STRIP) return;
        Block target = STRIPPABLES.get(event.getState().getBlock());
        if (target == null) return;
        var state = target.defaultBlockState();
        if (event.getState().hasProperty(RotatedPillarBlock.AXIS) && state.hasProperty(RotatedPillarBlock.AXIS))
            state = state.setValue(RotatedPillarBlock.AXIS, event.getState().getValue(RotatedPillarBlock.AXIS));
        event.setFinalState(state);
    }
    @SubscribeEvent public static void reload(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Map<String, JsonObject>>() {
            @Override protected Map<String, JsonObject> prepare(ResourceManager manager, ProfilerFiller profiler) {
                Map<String, JsonObject> result = new HashMap<>();
                for (String path : PATHS) {
                    JsonObject merged = new JsonObject();
                    for (var resource : manager.getResourceStack(new ResourceLocation("forge", "data_maps/" + path + ".json"))) {
                        try (var reader = resource.openAsReader()) {
                            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                            if (json.has("replace") && json.get("replace").getAsBoolean()) merged = new JsonObject();
                            if (json.has("values")) for (var entry : json.getAsJsonObject("values").entrySet()) merged.add(entry.getKey(), entry.getValue());
                            if (json.has("remove")) for (var key : json.getAsJsonArray("remove")) merged.remove(key.getAsString());
                        } catch (Exception exception) { PoopSky.LOGGER.error("Invalid data map {} from {}",path,resource.sourcePackId(),exception); }
                    }
                    result.put(path, merged);
                }
                return result;
            }
            @Override protected void apply(Map<String, JsonObject> tables, ResourceManager manager, ProfilerFiller profiler) { applyTables(tables); }
        });
    }
}
