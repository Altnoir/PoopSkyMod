package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootTableRandomSequenceGen implements DataProvider {
    private final Path lootTableRoot;

    public LootTableRandomSequenceGen(PackOutput output) {
        this.lootTableRoot = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(PoopSky.MOD_ID)
                .resolve("loot_table");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        if (!Files.isDirectory(lootTableRoot)) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletableFuture<?>> writes = new ArrayList<>();
        try (var paths = Files.walk(lootTableRoot)) {
            paths.filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> writes.add(writeWithRandomSequence(output, path)));
        } catch (IOException exception) {
            return CompletableFuture.failedFuture(exception);
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> writeWithRandomSequence(CachedOutput output, Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String relativePath = lootTableRoot.relativize(path).toString().replace('\\', '/');
            String idPath = relativePath.substring(0, relativePath.length() - ".json".length());
            json.addProperty("random_sequence", PoopSky.MOD_ID + ":" + idPath);
            return DataProvider.saveStable(output, json, path);
        } catch (IOException exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    @Override
    public String getName() {
        return "PoopSky Loot Table Random Sequences";
    }
}
