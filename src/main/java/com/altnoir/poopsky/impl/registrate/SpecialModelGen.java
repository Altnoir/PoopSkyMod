package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoBlocks;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Generates the state-dependent models that vanilla's model helpers cannot describe. */
public class SpecialModelGen implements DataProvider {
    private static final String[] WOOD_TOILET_SUFFIXES = {"", "_n", "_ns"};
    private static final String[] HARD_TOILET_SUFFIXES = {"", "_n", "_ns", "_lava", "_lava_n", "_lava_ns"};

    private final PackOutput.PathProvider blockStates;
    private final PackOutput.PathProvider models;

    public SpecialModelGen(PackOutput output) {
        blockStates = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> writes = new ArrayList<>();

        generatePoopBlock(output, writes);
        generatePoopPiece(output, writes);
        generatePoopFarmland(output, writes);
        generatePoolimeMaggotsBlock(output, writes);
        generateMaggotsBlock(output, writes);
        generateRawPoopBlock(output, writes);
        generatePoopTnt(output, writes);
        generateBreedingChest(output, writes);
        generateFlyBarrel(output, writes);
        generatePlacer(output, writes);
        generateUrineLiquid(output, writes);
        generateMaggotsCrop(output, writes);
        generatePoopCake(output, writes);
        generateToilet(output, writes, "wooden_toilet", ToiletType.Category.WOOD, false);
        generateToilet(output, writes, "hard_toilet", ToiletType.Category.HARD, true);
        generateMissingBlockItems(output, writes);
        generateContextualShitItems(output, writes);

        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private void generatePoopBlock(CachedOutput output, List<CompletableFuture<?>> writes) {
        writeModel(output, writes, "block/poop_block1", cubeAll("poopsky:block/poop_block"));

        JsonObject maggots = cubeAll("poopsky:block/poop_block_maggots");
        maggots.getAsJsonObject("textures").addProperty("particle", "poopsky:block/poop_block");
        writeModel(output, writes, "block/poop_block2", maggots);

        JsonObject liquids = parent("minecraft:block/cube_bottom_top");
        textures(liquids)
                .addProperty("side", "poopsky:block/poop_block");
        textures(liquids)
                .addProperty("bottom", "poopsky:block/poop_block");
        textures(liquids)
                .addProperty("top", "poopsky:block/poop_block_liquids");
        textures(liquids)
                .addProperty("particle", "poopsky:block/poop_block");
        writeModel(output, writes, "block/poop_block3", liquids);

        JsonArray variants = new JsonArray();
        variants.add(variant("poopsky:block/poop_block1", 0, 0, false, 9));
        variants.add(variant("poopsky:block/poop_block2", 0, 0, false, 1));
        variants.add(variant("poopsky:block/poop_block3", 0, 0, false, 2));
        writeBlockState(output, writes, "poop_block", singleVariant(variants));
        writeItemParent(output, writes, "poop_block", "poopsky:block/poop_block1");
    }

    private void generatePoopPiece(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject variants = new JsonObject();
        for (int layers = 1; layers < 8; layers++) {
            int height = layers * 2;
            String name = "poop_height" + height;
            writeModel(output, writes, "block/" + name, layeredCube(height));
            variants.add("layers=" + layers, variant("poopsky:block/" + name));
        }
        variants.add("layers=8", variant("poopsky:block/poop_block1"));
        writeBlockState(output, writes, "poop_piece", variants(variants));
        writeItemParent(output, writes, "poop_piece", "poopsky:block/poop_height2");
    }

    private void generatePoopFarmland(CachedOutput output, List<CompletableFuture<?>> writes) {
        Map<String, String> modes = Map.of(
                "default", "poop_farmland",
                "enriched", "poop_farmland_enriched",
                "leak", "poop_farmland_leak",
                "enriched_leak", "poop_farmland_enriched_leak"
        );
        JsonObject variants = new JsonObject();
        modes.forEach((mode, texture) -> {
            JsonObject model = parent("minecraft:block/template_farmland");
            textures(model).addProperty("dirt", "poopsky:block/poop_block");
            textures(model).addProperty("top", "poopsky:block/" + texture);
            textures(model).addProperty("particle", "poopsky:block/poop_block");
            String modelName = "poop_farmland" + (mode.equals("default") ? "" : "_" + mode);
            writeModel(output, writes, "block/" + modelName, model);
            variants.add("mode=" + mode, variant("poopsky:block/" + modelName));
        });
        writeBlockState(output, writes, "poop_farmland", variants(variants));
        writeItemParent(output, writes, "poop_farmland", "poopsky:block/poop_farmland");
    }

    private void generatePoolimeMaggotsBlock(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject model = parent("minecraft:block/cube");
        JsonObject textures = textures(model);
        for (String face : List.of("north", "south", "east", "west", "down")) {
            textures.addProperty(face, "poopsky:block/poop_block");
        }
        textures.addProperty("up", "poopsky:block/poolime_maggots_block");
        textures.addProperty("particle", "poopsky:block/poop_block");
        writeModel(output, writes, "block/poolime_maggots_block", model);
        writeBlockState(output, writes, "poolime_maggots_block", singleVariant(variant("poopsky:block/poolime_maggots_block")));
        writeItemParent(output, writes, "poolime_maggots_block", "poopsky:block/poolime_maggots_block");
    }

    private void generateMaggotsBlock(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject model = bottomTop(
                "poopsky:block/maggots_block_top",
                "poopsky:block/maggots_block_side",
                "poopsky:block/maggots_block_bottom"
        );
        writeSimpleBlockWithItem(output, writes, "maggots_block", model);
    }

    private void generateRawPoopBlock(CachedOutput output, List<CompletableFuture<?>> writes) {
        writeModel(output, writes, "block/raw_poop_block0", cubeAll("poopsky:block/raw_poop_block"));
        writeModel(output, writes, "block/raw_poop_block1", cubeAll("poopsky:block/raw_poop_block1"));
        JsonArray variants = new JsonArray();
        variants.add(variant("poopsky:block/raw_poop_block0", 0, 0, false, 3));
        variants.add(variant("poopsky:block/raw_poop_block1", 0, 0, false, 1));
        writeBlockState(output, writes, "raw_poop_block", singleVariant(variants));
        writeItemParent(output, writes, "raw_poop_block", "poopsky:block/raw_poop_block0");
    }

    private void generatePoopTnt(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject model = bottomTop("poopsky:block/poop_tnt_top", "poopsky:block/poop_tnt_side", "poopsky:block/poop_tnt_bottom");
        writeSimpleBlockWithItem(output, writes, "poop_tnt", model);
    }

    private void generateBreedingChest(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject model = bottomTop("poopsky:block/cut_poop_block", "poopsky:block/breeding_chest_side", "poopsky:block/cut_poop_block");
        writeSimpleBlockWithItem(output, writes, "breeding_chest", model);
    }

    private void generateFlyBarrel(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject model = bottomTop("poopsky:block/fly_barrel_top", "poopsky:block/fly_barrel_side", "poopsky:block/fly_barrel_bottom");
        writeModel(output, writes, "block/fly_barrel", model);

        JsonObject variants = new JsonObject();
        variants.add("facing=up", variant("poopsky:block/fly_barrel"));
        variants.add("facing=down", variant("poopsky:block/fly_barrel", 180, 0, false, 0));
        variants.add("facing=north", variant("poopsky:block/fly_barrel", 90, 0, false, 0));
        variants.add("facing=south", variant("poopsky:block/fly_barrel", 90, 180, false, 0));
        variants.add("facing=west", variant("poopsky:block/fly_barrel", 90, 270, false, 0));
        variants.add("facing=east", variant("poopsky:block/fly_barrel", 90, 90, false, 0));
        writeBlockState(output, writes, "fly_barrel", variants(variants));
        writeItemParent(output, writes, "fly_barrel", "poopsky:block/fly_barrel");
    }

    private void generatePlacer(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject horizontal = parent("minecraft:block/orientable");
        textures(horizontal).addProperty("top", "poopsky:block/placer_top");
        textures(horizontal).addProperty("side", "poopsky:block/placer_side");
        textures(horizontal).addProperty("front", "poopsky:block/placer_front");
        textures(horizontal).addProperty("particle", "poopsky:block/placer_side");
        writeModel(output, writes, "block/placer", horizontal);

        JsonObject vertical = parent("minecraft:block/orientable_vertical");
        textures(vertical).addProperty("side", "poopsky:block/placer_top");
        textures(vertical).addProperty("front", "poopsky:block/placer_front_vertical");
        textures(vertical).addProperty("particle", "poopsky:block/placer_side");
        writeModel(output, writes, "block/placer_vertical", vertical);

        JsonObject variants = new JsonObject();
        variants.add("facing=up", variant("poopsky:block/placer_vertical"));
        variants.add("facing=down", variant("poopsky:block/placer_vertical", 180, 0, false, 0));
        variants.add("facing=north", variant("poopsky:block/placer"));
        variants.add("facing=south", variant("poopsky:block/placer", 0, 180, false, 0));
        variants.add("facing=west", variant("poopsky:block/placer", 0, 270, false, 0));
        variants.add("facing=east", variant("poopsky:block/placer", 0, 90, false, 0));
        writeBlockState(output, writes, "placer", variants(variants));
        writeItemParent(output, writes, "placer", "poopsky:block/placer");
    }

    private void generateUrineLiquid(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject model = parent("minecraft:block/block");
        textures(model).addProperty("particle", "poopsky:block/urine_liquid");
        textures(model).addProperty("still", "poopsky:block/urine_liquid");
        textures(model).addProperty("flow", "poopsky:block/urine_liquid_flowing");
        writeModel(output, writes, "block/urine_liquid", model);

        JsonObject variants = new JsonObject();
        for (int level = 0; level <= 15; level++) {
            variants.add("level=" + level, variant("poopsky:block/urine_liquid"));
        }
        writeBlockState(output, writes, "urine_liquid", variants(variants));
        writeFlatItem(output, writes, "urine_liquid", "poopsky:block/urine_liquid");
    }

    private void generateMaggotsCrop(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject variants = new JsonObject();
        for (int age = 0; age <= 7; age++) {
            JsonObject model = parent("minecraft:block/crop");
            textures(model).addProperty("crop", "poopsky:block/maggots_stage" + age);
            writeModel(output, writes, "block/maggots_stage" + age, model);
            variants.add("age=" + age, variant("poopsky:block/maggots_stage" + age));
        }
        writeBlockState(output, writes, "maggots", variants(variants));
    }

    private void generatePoopCake(CachedOutput output, List<CompletableFuture<?>> writes) {
        JsonObject variants = new JsonObject();
        for (int bites = 0; bites <= 6; bites++) {
            String name = bites == 0 ? "poop_cake" : "poop_cake_slice" + bites;
            String template = bites == 0 ? "cake" : "cake_slice" + bites;
            JsonObject model = parent("minecraft:block/" + template);
            addCakeTextures(model, bites > 0);
            writeModel(output, writes, "block/" + name, model);
            variants.add("bites=" + bites, variant("poopsky:block/" + name));
        }
        writeBlockState(output, writes, "poop_cake", variants(variants));

        PoBlocks.getPoopCandleCakes().forEach((candle, candleCake) -> {
            String candlePath = BuiltInRegistries.BLOCK.getKey(candle).getPath();
            String cakePath = BuiltInRegistries.BLOCK.getKey(candleCake.get()).getPath();
            for (boolean lit : List.of(false, true)) {
                String modelName = cakePath + (lit ? "_lit" : "");
                JsonObject model = parent("minecraft:block/template_cake_with_candle");
                addCakeTextures(model, false);
                textures(model).addProperty("candle", "minecraft:block/" + candlePath + (lit ? "_lit" : ""));
                writeModel(output, writes, "block/" + modelName, model);
            }
            JsonObject candleVariants = new JsonObject();
            candleVariants.add("lit=false", variant("poopsky:block/" + cakePath));
            candleVariants.add("lit=true", variant("poopsky:block/" + cakePath + "_lit"));
            writeBlockState(output, writes, cakePath, variants(candleVariants));
        });
    }

    private void addCakeTextures(JsonObject model, boolean sliced) {
        JsonObject textures = textures(model);
        textures.addProperty("particle", "poopsky:block/poop_cake_side");
        textures.addProperty("bottom", "poopsky:block/poop_cake_bottom");
        textures.addProperty("top", "poopsky:block/poop_cake_top");
        textures.addProperty("side", "poopsky:block/poop_cake_side");
        if (sliced) textures.addProperty("inside", "poopsky:block/poop_cake_inner");
    }

    private void generateToilet(
            CachedOutput output,
            List<CompletableFuture<?>> writes,
            String blockPath,
            ToiletType.Category category,
            boolean hasLava
    ) {
        List<ToiletType> types = new ArrayList<>(ToiletType.getByCategory(category).values());
        String[] suffixes = hasLava ? HARD_TOILET_SUFFIXES : WOOD_TOILET_SUFFIXES;
        String firstTexture = toiletTexture(types.getFirst());

        for (String suffix : suffixes) {
            writeToiletModel(output, writes, blockPath + suffix, suffix, firstTexture);
        }
        for (ToiletType type : types) {
            writeToiletModel(output, writes, blockPath + "_" + type.id(), "", toiletTexture(type));
        }

        JsonObject stateVariants = new JsonObject();
        for (String facing : List.of("north", "east", "south", "west")) {
            int baseRotation = switch (facing) {
                case "east" -> 90;
                case "south" -> 180;
                case "west" -> 270;
                default -> 0;
            };
            for (String connection : List.of("default", "front", "back", "both")) {
                String connectionSuffix = switch (connection) {
                    case "front", "back" -> "_n";
                    case "both" -> "_ns";
                    default -> "";
                };
                int rotation = (baseRotation + (connection.equals("back") ? 180 : 0)) % 360;
                if (hasLava) {
                    for (boolean lava : List.of(false, true)) {
                        String suffix = (lava ? "_lava" : "") + connectionSuffix;
                        String key = "connection=" + connection + ",facing=" + facing + ",lava=" + lava;
                        stateVariants.add(key, variant("poopsky:block/" + blockPath + suffix, 0, rotation, true, 0));
                    }
                } else {
                    String key = "connection=" + connection + ",facing=" + facing;
                    stateVariants.add(key, variant("poopsky:block/" + blockPath + connectionSuffix, 0, rotation, true, 0));
                }
            }
        }
        writeBlockState(output, writes, blockPath, variants(stateVariants));

        JsonObject item = parent("poopsky:block/" + blockPath);
        JsonArray overrides = new JsonArray();
        for (ToiletType type : types) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("poopsky:toilet_type", ToiletType.getCategoryModelValue(type));
            override.add("predicate", predicate);
            override.addProperty("model", "poopsky:block/" + blockPath + "_" + type.id());
            overrides.add(override);
        }
        item.add("overrides", overrides);
        writeModel(output, writes, "item/" + blockPath, item);
    }

    private void generateMissingBlockItems(CachedOutput output, List<CompletableFuture<?>> writes) {
        for (String path : List.of("saltpeter_cluster", "large_saltpeter_bud", "medium_saltpeter_bud", "small_saltpeter_bud")) {
            writeItemParent(output, writes, path, "poopsky:block/" + path);
        }
        writeFlatItem(output, writes, "maggots_seeds");
        writeFlatItem(output, writes, "roundworm");
        writeItemParent(output, writes, "fly_spawn_egg", "minecraft:item/template_spawn_egg");
        writeItemParent(output, writes, "poolime_spawn_egg", "minecraft:item/template_spawn_egg");
    }

    private void generateContextualShitItems(CachedOutput output, List<CompletableFuture<?>> writes) {
        for (String path : List.of("shit", "chili_shit", "golden_shit")) {
            JsonObject item = parent("minecraft:builtin/entity");
            item.addProperty("gui_light", "front");
            writeModel(output, writes, "item/" + path, item);
            writeFlatItem(output, writes, path + "_flat", "poopsky:item/" + path);
        }
    }

    private void writeToiletModel(
            CachedOutput output,
            List<CompletableFuture<?>> writes,
            String name,
            String suffix,
            String texture
    ) {
        String templateSuffix = suffix.startsWith("_lava") ? suffix : suffix;
        JsonObject model = parent("poopsky:block/toilet" + templateSuffix);
        textures(model).addProperty("toilet", texture);
        writeModel(output, writes, "block/" + name, model);
    }

    private String toiletTexture(ToiletType type) {
        if (type.texture() != null) {
            String namespace = type.sourceBlock() == null
                    ? PoopSky.MOD_ID
                    : BuiltInRegistries.BLOCK.getKey(type.sourceBlock()).getNamespace();
            return namespace + ":block/" + type.texture();
        }
        Block source = type.sourceBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(source);
        return id.getNamespace() + ":block/" + id.getPath();
    }

    private void writeSimpleBlockWithItem(
            CachedOutput output,
            List<CompletableFuture<?>> writes,
            String name,
            JsonObject model
    ) {
        writeModel(output, writes, "block/" + name, model);
        writeBlockState(output, writes, name, singleVariant(variant("poopsky:block/" + name)));
        writeItemParent(output, writes, name, "poopsky:block/" + name);
    }

    private void writeFlatItem(CachedOutput output, List<CompletableFuture<?>> writes, String name) {
        writeFlatItem(output, writes, name, "poopsky:item/" + name);
    }

    private void writeFlatItem(
            CachedOutput output,
            List<CompletableFuture<?>> writes,
            String name,
            String texture
    ) {
        JsonObject model = parent("minecraft:item/generated");
        textures(model).addProperty("layer0", texture);
        writeModel(output, writes, "item/" + name, model);
    }

    private void writeItemParent(
            CachedOutput output,
            List<CompletableFuture<?>> writes,
            String name,
            String parent
    ) {
        writeModel(output, writes, "item/" + name, parent(parent));
    }

    private void writeModel(
            CachedOutput output,
            List<CompletableFuture<?>> writes,
            String path,
            JsonObject model
    ) {
        ResourceLocation id = PoopSky.loc(path);
        writes.add(DataProvider.saveStable(output, model, models.json(id)));
    }

    private void writeBlockState(
            CachedOutput output,
            List<CompletableFuture<?>> writes,
            String path,
            JsonObject blockState
    ) {
        ResourceLocation id = PoopSky.loc(path);
        writes.add(DataProvider.saveStable(output, blockState, blockStates.json(id)));
    }

    private static JsonObject parent(String parent) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        return json;
    }

    private static JsonObject textures(JsonObject model) {
        if (!model.has("textures")) model.add("textures", new JsonObject());
        return model.getAsJsonObject("textures");
    }

    private static JsonObject cubeAll(String texture) {
        JsonObject model = parent("minecraft:block/cube_all");
        textures(model).addProperty("all", texture);
        return model;
    }

    private static JsonObject bottomTop(String top, String side, String bottom) {
        JsonObject model = parent("minecraft:block/cube_bottom_top");
        textures(model).addProperty("top", top);
        textures(model).addProperty("side", side);
        textures(model).addProperty("bottom", bottom);
        return model;
    }

    private static JsonObject layeredCube(int height) {
        JsonObject model = parent("minecraft:block/thin_block");
        textures(model).addProperty("particle", "poopsky:block/poop_block");
        textures(model).addProperty("texture", "poopsky:block/poop_block");

        JsonObject element = new JsonObject();
        element.add("from", vector(0, 0, 0));
        element.add("to", vector(16, height, 16));
        JsonObject faces = new JsonObject();
        for (String face : List.of("down", "up", "north", "south", "west", "east")) {
            JsonObject faceJson = new JsonObject();
            faceJson.addProperty("texture", "#texture");
            if (!face.equals("up")) {
                faceJson.addProperty("cullface", face);
            }
            faces.add(face, faceJson);
        }
        element.add("faces", faces);
        JsonArray elements = new JsonArray();
        elements.add(element);
        model.add("elements", elements);
        return model;
    }

    private static JsonArray vector(int x, int y, int z) {
        JsonArray vector = new JsonArray();
        vector.add(x);
        vector.add(y);
        vector.add(z);
        return vector;
    }

    private static JsonObject variants(JsonObject variants) {
        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    private static JsonObject singleVariant(Object variant) {
        JsonObject variants = new JsonObject();
        if (variant instanceof JsonObject object) variants.add("", object);
        if (variant instanceof JsonArray array) variants.add("", array);
        return variants(variants);
    }

    private static JsonObject variant(String model) {
        return variant(model, 0, 0, false, 0);
    }

    private static JsonObject variant(String model, int x, int y, boolean uvLock, int weight) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", model);
        if (x != 0) variant.addProperty("x", x);
        if (y != 0) variant.addProperty("y", y);
        if (uvLock) variant.addProperty("uvlock", true);
        if (weight > 0) variant.addProperty("weight", weight);
        return variant;
    }

    @Override
    public String getName() {
        return "Special Block and Item Models";
    }
}
