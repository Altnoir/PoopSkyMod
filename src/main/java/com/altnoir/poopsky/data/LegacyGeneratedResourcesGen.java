package com.altnoir.poopsky.data;

import com.google.common.hash.Hashing;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LegacyGeneratedResourcesGen implements DataProvider {
    private static final List<String> LEGACY_DATA_FILES = List.of(
            "data/poopsky/advancement/chili.json",
            "data/poopsky/advancement/recipes/anal_pressing/wither_skeleton_skull.json",
            "data/poopsky/advancement/recipes/pop_explosion/calcite_to_diorite.json",
            "data/poopsky/advancement/recipes/pop_explosion/dripstone_block_to_granite.json",
            "data/poopsky/advancement/recipes/pop_explosion/poop_sand_to_dried_poop_block.json",
            "data/poopsky/advancement/recipes/pop_explosion/skeleton_skull_to_bone_block.json",
            "data/poopsky/advancement/recipes/pop_explosion/tuff_to_andesite.json",
            "data/poopsky/loot_modifiers/seenae.json",
            "data/poopsky/recipe/haunting/end_stone.json",
            "data/poopsky/recipe/haunting/netherrack.json"
    );

    private final Map<Path, byte[]> resources = new LinkedHashMap<>();

    public LegacyGeneratedResourcesGen(PackOutput output) {
        Path root = output.getOutputFolder();
        captureTree(root.resolve("assets/poopsky/blockstates"));
        captureTree(root.resolve("assets/poopsky/lang"));
        captureTree(root.resolve("assets/poopsky/models/block"));
        captureTree(root.resolve("assets/poopsky/models/item"));
        LEGACY_DATA_FILES.forEach(relative -> capture(root.resolve(relative)));
    }

    private void captureTree(Path root) {
        if (!Files.isDirectory(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            paths.filter(path -> path.toString().endsWith(".json")).forEach(this::capture);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read existing generated resources from " + root, exception);
        }
    }

    private void capture(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        try {
            resources.put(path, Files.readAllBytes(path));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read existing generated resource " + path, exception);
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        try {
            for (Map.Entry<Path, byte[]> resource : resources.entrySet()) {
                byte[] bytes = resource.getValue();
                output.writeIfNeeded(resource.getKey(), bytes, Hashing.sha256().hashBytes(bytes));
            }
            return CompletableFuture.completedFuture(null);
        } catch (IOException exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    @Override
    public String getName() {
        return "PoopSky Legacy Generated Resources";
    }
}
