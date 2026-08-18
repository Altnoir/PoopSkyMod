package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.data.ArcadeLootGen;
import com.altnoir.poopsky.init.PoBlocks;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ArcadeLootRecipes {
    private record ParsedOutput(ItemStack item, @Nullable TagKey<Item> tag, int weight) {
    }

    private ArcadeLootRecipes() {
    }

    public static List<ArcadeLootRecipe> all() {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager resources = minecraft.getSingleplayerServer() != null
                ? minecraft.getSingleplayerServer().getResourceManager()
                : minecraft.getResourceManager();

        List<ArcadeLootRecipe> recipes = new ArrayList<>();
        for (BlockEntry<ArcadeBlock> entry : PoBlocks.getArcadeBlocks()) {
            var lootKey = ArcadeLootGen.lootTableKey(entry.get());
            ResourceLocation file = ResourceLocation.fromNamespaceAndPath(
                    lootKey.location().getNamespace(),
                    "loot_table/" + lootKey.location().getPath() + ".json");
            resources.getResource(file).ifPresent(resource -> loadArcade(entry, resource, recipes));
        }
        return recipes;
    }

    private static void loadArcade(BlockEntry<ArcadeBlock> entry, Resource resource, List<ArcadeLootRecipe> recipes) {
        try (InputStream stream = resource.open()) {
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            List<ParsedOutput> parsedOutputs = new ArrayList<>();

            if (root.has("pools")) {
                for (JsonElement poolElement : root.getAsJsonArray("pools")) {
                    JsonObject pool = poolElement.getAsJsonObject();
                    if (!pool.has("entries")) {
                        continue;
                    }
                    for (JsonElement entryElement : pool.getAsJsonArray("entries")) {
                        parseEntry(entryElement.getAsJsonObject(), parsedOutputs);
                    }
                }
            }

            ItemStack input = new ItemStack(entry.get());
            String blockPath = PoopSky.getBlockPath(entry.get());
            List<ArcadeLootRecipe.Output> outputs = calculateOutputs(parsedOutputs);
            int pageCount = (outputs.size() + ArcadeLootRecipe.OUTPUT_CAPACITY - 1) / ArcadeLootRecipe.OUTPUT_CAPACITY;
            for (int page = 0; page < pageCount; page++) {
                int start = page * ArcadeLootRecipe.OUTPUT_CAPACITY;
                int end = Math.min(start + ArcadeLootRecipe.OUTPUT_CAPACITY, outputs.size());
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "arcade/" + blockPath + "_" + (page + 1));
                recipes.add(new ArcadeLootRecipe(id, input, List.copyOf(outputs.subList(start, end))));
            }
        } catch (Exception e) {
            PoopSky.LOGGER.error("Failed to load arcade loot for JEI from {}", resource.sourcePackId(), e);
        }
    }

    private static void parseEntry(JsonObject entry, List<ParsedOutput> outputs) {
        if (!entry.has("type") || !entry.has("name")) {
            return;
        }

        String type = entry.get("type").getAsString();
        int weight = entry.has("weight") ? entry.get("weight").getAsInt() : 1;
        ResourceLocation id = ResourceLocation.parse(entry.get("name").getAsString());

        switch (type) {
            case "minecraft:item" -> {
                Item item = BuiltInRegistries.ITEM.get(id);
                if (item != Items.AIR) {
                    outputs.add(new ParsedOutput(item.getDefaultInstance(), null, weight));
                }
            }
            case "minecraft:tag" -> {
                TagKey<Item> tag = TagKey.create(Registries.ITEM, id);
                outputs.add(new ParsedOutput(ItemStack.EMPTY, tag, weight));
            }
            default -> {
            }
        }
    }

    private static List<ArcadeLootRecipe.Output> calculateOutputs(List<ParsedOutput> parsedOutputs) {
        float totalWeight = 0.0F;
        for (ParsedOutput output : parsedOutputs) {
            totalWeight += effectiveWeight(output);
        }

        List<ArcadeLootRecipe.Output> outputs = new ArrayList<>();
        if (totalWeight <= 0.0) {
            return outputs;
        }

        for (ParsedOutput output : parsedOutputs) {
            float chance = effectiveWeight(output) / totalWeight * 100;
            if (output.tag() != null) {
                outputs.add(ArcadeLootRecipe.Output.tag(output.tag(), chance));
            } else {
                outputs.add(ArcadeLootRecipe.Output.item(output.item(), chance));
            }
        }
        return outputs;
    }

    private static float effectiveWeight(ParsedOutput output) {
        return output.tag() != null ? output.weight() * tagMemberCount(output.tag()) : output.weight();
    }

    private static int tagMemberCount(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.getTag(tag).map(HolderSet::size).orElse(0);
    }
}