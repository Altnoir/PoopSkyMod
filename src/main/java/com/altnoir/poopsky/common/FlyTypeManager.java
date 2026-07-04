package com.altnoir.poopsky.common;

import com.altnoir.poopsky.PoopSky;
import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlyTypeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public static final FlyTypeManager INSTANCE = new FlyTypeManager();

    private volatile List<String> flyTypes = FlyType.FLY_TYPES;

    public FlyTypeManager() {
        super(GSON, "poopsky_data");
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> resultMap = new HashMap<>();

        // 查找所有数据包中 poopsky_data/ 目录下的 fly_types.json
        Map<ResourceLocation, Resource> allResources = resourceManager.listResources("poopsky_data",
                location -> location.getPath().endsWith("fly_types.json"));

        for (Map.Entry<ResourceLocation, Resource> entry : allResources.entrySet()) {
            ResourceLocation location = entry.getKey();

            // 获取同一路径下所有数据包版本的列表（从低优先级到高优先级）
            List<Resource> resourceStack = resourceManager.getResourceStack(location);

            // 按优先级顺序合并
            JsonArray mergedArray = new JsonArray();

            for (Resource resource : resourceStack) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement element = JsonParser.parseReader(reader);

                    boolean replace;
                    JsonArray values = null;

                    if (element.isJsonObject()) {
                        JsonObject obj = element.getAsJsonObject();
                        // replace 字段可选，默认为 false（追加模式）
                        replace = obj.has("replace") && obj.get("replace").getAsBoolean();
                        if (obj.has("values") && obj.get("values").isJsonArray()) {
                            values = obj.get("values").getAsJsonArray();
                        }
                    } else if (element.isJsonArray()) {
                        // 向后兼容：纯数组格式视为 replace=true（完全替换）
                        replace = true;
                        values = element.getAsJsonArray();
                    } else {
                        continue;
                    }

                    if (values != null) {
                        if (replace) {
                            mergedArray = new JsonArray();
                        }
                        for (JsonElement val : values) {
                            if (val.isJsonPrimitive()) {
                                mergedArray.add(val.getAsString());
                            }
                        }
                    }
                } catch (Exception e) {
                    PoopSky.LOGGER.error("Error loading fly_types from resource {}: {}", location, e.getMessage());
                }
            }

            // 构造结果 key：去掉目录前缀和 .json 后缀
            String path = location.getPath();
            String key = path.substring("poopsky_data/".length(), path.length() - ".json".length());
            resultMap.put(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), key), mergedArray);
        }

        return resultMap;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (var entry : resources.entrySet()) {
            if ("fly_types".equals(entry.getKey().getPath())) {
                JsonElement value = entry.getValue();
                if (value != null && value.isJsonArray()) {
                    for (JsonElement element : value.getAsJsonArray()) {
                        if (element.isJsonPrimitive()) {
                            builder.add(element.getAsString());
                        }
                    }
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