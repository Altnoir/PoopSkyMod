package com.altnoir.poopsky.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

abstract class LegacyBlockStateGenerator {
    protected final RegistrateBlockModelGenerator prov;
    private final ModelManager blockModels = new ModelManager("block");
    private final ItemModelManager itemModels = new ItemModelManager();
    private final Map<Block, VariantBuilder> variants = new IdentityHashMap<>();

    protected LegacyBlockStateGenerator(RegistrateBlockModelGenerator prov) {
        this.prov = prov;
    }

    private static String path(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private static JsonArray vector(float... values) {
        JsonArray a = new JsonArray();
        for (float value : values) {
            if (value == Math.rint(value)) {
                a.add((int) value);
            } else {
                a.add(value);
            }
        }
        return a;
    }

    private static JsonElement configured(ConfiguredModel[] models) {
        if (models.length == 1) return models[0].json();
        JsonArray a = new JsonArray();
        for (ConfiguredModel m : models) a.add(m.json());
        return a;
    }

    protected Identifier modLoc(String path) {
        return prov.modLoc(path);
    }

    protected Identifier mcLoc(String path) {
        return prov.mcLoc(path);
    }

    protected Identifier blockTexture(Block block) {
        return prov.blockTexture(block).sprite();
    }

    protected ModelManager models() {
        return blockModels;
    }

    protected ItemModelManager itemModels() {
        return itemModels;
    }

    protected VariantBuilder getVariantBuilder(Block block) {
        return variants.computeIfAbsent(block, VariantBuilder::new);
    }

    protected void flushVariantBuilders() {
        variants.forEach((block, builder) ->
            prov.blockStateOutput.accept(new net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator() {
                @Override
                public Block block() {
                    return block;
                }

                @Override
                public BlockStateModelDispatcher create() {
                    return BlockStateModelDispatcher.CODEC.parse(JsonOps.INSTANCE, builder.json()).getOrThrow();
                }
            }));
    }

    protected void existingBlockstate(Block block) {
        String resource = "assets/poopsky/blockstates/" + path(block) + ".json";
        var stream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resource), resource);
        JsonElement json;
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            json = JsonParser.parseReader(reader);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Failed to read " + resource, exception);
        }
        BlockStateModelDispatcher dispatcher = BlockStateModelDispatcher.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        prov.blockStateOutput.accept(new net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator() {
            @Override
            public Block block() {
                return block;
            }

            @Override
            public BlockStateModelDispatcher create() {
                return dispatcher;
            }
        });
    }

    protected void simpleBlock(Block block, ModelFile... models) {
        ConfiguredModel[] configured = Arrays.stream(models).map(ConfiguredModel::new).toArray(ConfiguredModel[]::new);
        simpleBlock(block, configured);
    }

    protected void simpleBlock(Block block, ConfiguredModel... models) {
        getVariantBuilder(block).partialState().addModels(models);
    }

    protected void simpleBlockItem(Block block, ModelFile model) {
        itemModels.withExistingParent(BuiltInRegistries.ITEM.getKey(block.asItem()).getPath(), model.location);
    }

    protected void simpleBlockWithItem(Block block, ModelFile model) {
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    protected void separateTransformsItem(Block block, Identifier texture, Identifier blockModel) {
        Identifier itemModel = modLoc("item/" + BuiltInRegistries.ITEM.getKey(block.asItem()).getPath());
        prov.modelOutput.accept(itemModel, () -> {
            JsonObject root = new JsonObject();
            JsonObject base = new JsonObject();
            base.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", texture.toString());
            base.add("textures", textures);
            root.add("base", base);
            root.addProperty("gui_light", "front");
            root.addProperty("loader", "neoforge:separate_transforms");
            JsonObject perspectives = new JsonObject();
            for (String context : List.of("ground", "head")) {
                JsonObject model = new JsonObject();
                model.addProperty("parent", blockModel.toString());
                perspectives.add(context, model);
            }
            root.add("perspectives", perspectives);
            return root;
        });
    }

    protected ModelFile cubeAll(Block block) {
        return models().cubeAll(path(block), blockTexture(block));
    }

    protected void logBlock(RotatedPillarBlock block) {
        prov.generateLogBlock(block);
    }

    protected void axisBlock(RotatedPillarBlock block, Identifier side, Identifier end) {
        prov.generateAxisBlock(block, new Material(side), new Material(end));
    }

    protected void stairsBlock(StairBlock block, Identifier texture) {
        prov.generateStairsBlock(block, new Material(texture));
    }

    protected void slabBlock(SlabBlock block, Identifier doubleModel, Identifier texture) {
        prov.generateSlabBlock(block, BlockModelGenerators.plainVariant(doubleModel), new Material(texture));
    }

    protected void wallBlock(WallBlock block, Identifier texture) {
        String name = path(block);
        models().withExistingParent(name + "_post", mcLoc("block/template_wall_post")).texture("wall", texture);
        models().withExistingParent(name + "_side", mcLoc("block/template_wall_side")).texture("wall", texture);
        models().withExistingParent(name + "_side_tall", mcLoc("block/template_wall_side_tall")).texture("wall", texture);

        JsonArray multipart = new JsonArray();
        multipart.add(wallPart(name + "_post", "up", "true", 0));
        multipart.add(wallPart(name + "_side", "east", "low", 90));
        multipart.add(wallPart(name + "_side_tall", "east", "tall", 90));
        multipart.add(wallPart(name + "_side", "north", "low", 0));
        multipart.add(wallPart(name + "_side_tall", "north", "tall", 0));
        multipart.add(wallPart(name + "_side", "south", "low", 180));
        multipart.add(wallPart(name + "_side_tall", "south", "tall", 180));
        multipart.add(wallPart(name + "_side", "west", "low", 270));
        multipart.add(wallPart(name + "_side_tall", "west", "tall", 270));
        JsonObject root = new JsonObject();
        root.add("multipart", multipart);
        emitBlockstate(block, root);
    }

    protected void buttonBlock(ButtonBlock block, Identifier texture) {
        prov.generateButtonBlock(block, new Material(texture));
    }

    protected void pressurePlateBlock(PressurePlateBlock block, Identifier texture) {
        prov.generatePressurePlateBlock(block, new Material(texture));
    }

    protected void fenceBlock(FenceBlock block, Identifier texture) {
        prov.generateFenceBlock(block, new Material(texture));
    }

    protected void fenceGateBlock(FenceGateBlock block, Identifier texture) {
        prov.generateFenceGateBlock(block, new Material(texture));
    }

    protected void doorBlockWithRenderType(DoorBlock block, Identifier bottom, Identifier top, String renderType) {
        String name = path(block);
        String[] suffixes = {"_bottom_left", "_bottom_left_open", "_bottom_right", "_bottom_right_open",
                "_top_left", "_top_left_open", "_top_right", "_top_right_open"};
        ModelFile[] files = new ModelFile[suffixes.length];
        for (int index = 0; index < suffixes.length; index++) {
            files[index] = models().withExistingParent(name + suffixes[index], mcLoc("block/door" + suffixes[index]))
                    .texture("bottom", bottom)
                    .texture("top", top)
                    .renderType(renderType);
        }
        prov.generateDoorBlock(block,
                BlockModelGenerators.plainVariant(files[0].location), BlockModelGenerators.plainVariant(files[1].location),
                BlockModelGenerators.plainVariant(files[2].location), BlockModelGenerators.plainVariant(files[3].location),
                BlockModelGenerators.plainVariant(files[4].location), BlockModelGenerators.plainVariant(files[5].location),
                BlockModelGenerators.plainVariant(files[6].location), BlockModelGenerators.plainVariant(files[7].location));
    }

    protected void trapdoorBlockWithRenderType(TrapDoorBlock block, Identifier texture, boolean orientable, String renderType) {
        String name = path(block);
        ModelFile bottom = models().withExistingParent(name + "_bottom", mcLoc("block/template_orientable_trapdoor_bottom"))
                .texture("texture", texture).renderType(renderType);
        ModelFile top = models().withExistingParent(name + "_top", mcLoc("block/template_orientable_trapdoor_top"))
                .texture("texture", texture).renderType(renderType);
        ModelFile open = models().withExistingParent(name + "_open", mcLoc("block/template_orientable_trapdoor_open"))
                .texture("texture", texture).renderType(renderType);
        prov.generateTrapdoorBlock(block, BlockModelGenerators.plainVariant(bottom.location),
                BlockModelGenerators.plainVariant(top.location), BlockModelGenerators.plainVariant(open.location), orientable);
    }

    private JsonObject wallPart(String model, String property, String value, int rotation) {
        JsonObject apply = new JsonObject();
        apply.addProperty("model", modLoc("block/" + model).toString());
        if (!property.equals("up")) apply.addProperty("uvlock", true);
        if (rotation != 0) apply.addProperty("y", rotation);
        JsonObject when = new JsonObject();
        when.addProperty(property, value);
        JsonObject part = new JsonObject();
        part.add("apply", apply);
        part.add("when", when);
        return part;
    }

    private void emitBlockstate(Block block, JsonElement json) {
        BlockStateModelDispatcher dispatcher = BlockStateModelDispatcher.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        prov.blockStateOutput.accept(new net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator() {
            @Override
            public Block block() {
                return block;
            }

            @Override
            public BlockStateModelDispatcher create() {
                return dispatcher;
            }
        });
    }

    protected static class ModelFile {
        final Identifier location;

        ModelFile(Identifier location) {
            this.location = location;
        }

        public static class UncheckedModelFile extends ModelFile {
            public UncheckedModelFile(String id) {
                super(Identifier.parse(id));
            }

            public UncheckedModelFile(Identifier id) {
                super(id);
            }
        }
    }

    protected static class FaceBuilder {
        final JsonObject json = new JsonObject();
        private final Runnable endAction;

        FaceBuilder() {
            this(() -> {
            });
        }

        FaceBuilder(Runnable endAction) {
            this.endAction = endAction;
        }

        FaceBuilder texture(String texture) {
            json.addProperty("texture", texture);
            return this;
        }

        FaceBuilder uvs(float a, float b, float c, float d) {
            json.add("uv", vector(a, b, c, d));
            return this;
        }

        FaceBuilder cullface(Direction direction) {
            json.addProperty("cullface", direction.getSerializedName());
            return this;
        }

        ModelBuilder end() {
            endAction.run();
            return null;
        }
    }

    protected static class ConfiguredModel {
        final ModelFile model;
        final int x, y;
        final boolean uv;
        final int weight;

        ConfiguredModel(ModelFile model) {
            this(model, 0, 0, false, 1);
        }

        ConfiguredModel(ModelFile model, int x, int y, boolean uv, int weight) {
            this.model = model;
            this.x = x;
            this.y = y;
            this.uv = uv;
            this.weight = weight;
        }

        static Builder builder() {
            return new Builder();
        }

        JsonObject json() {
            JsonObject j = new JsonObject();
            j.addProperty("model", model.location.toString());
            if (x != 0) j.addProperty("x", x);
            if (y != 0) j.addProperty("y", y);
            if (uv) j.addProperty("uvlock", true);
            if (weight != 1) j.addProperty("weight", weight);
            return j;
        }

        static class Builder {
            ModelFile model;
            int x, y;
            boolean uv;

            Builder modelFile(ModelFile model) {
                this.model = model;
                return this;
            }

            Builder rotationX(int x) {
                this.x = x;
                return this;
            }

            Builder rotationY(int y) {
                this.y = y;
                return this;
            }

            Builder uvLock(boolean uv) {
                this.uv = uv;
                return this;
            }

            ConfiguredModel[] build() {
                return new ConfiguredModel[]{new ConfiguredModel(model, x, y, uv, 1)};
            }
        }
    }

    protected class ModelManager {
        private final String folder;
        protected final Map<Identifier, ModelBuilder> builders = new LinkedHashMap<>();

        ModelManager(String folder) {
            this.folder = folder;
        }

        protected Identifier location(String name) {
            if (name.indexOf(':') >= 0) return Identifier.parse(name);
            return modLoc(folder + "/" + name);
        }

        ModelBuilder withExistingParent(String name, Identifier parent) {
            Identifier location = location(name);
            return builders.computeIfAbsent(location, ModelBuilder::new).parent(parent);
        }

        ModelBuilder cubeAll(String name, Identifier texture) {
            return withExistingParent(name, mcLoc("block/cube_all")).texture("all", texture);
        }

        ModelBuilder cubeBottomTop(String name, Identifier side, Identifier bottom, Identifier top) {
            return withExistingParent(name, mcLoc("block/cube_bottom_top")).texture("side", side).texture("bottom", bottom).texture("top", top);
        }

        ModelBuilder cube(String name, Identifier down, Identifier up, Identifier north, Identifier south, Identifier east, Identifier west) {
            return withExistingParent(name, mcLoc("block/cube")).texture("down", down).texture("up", up).texture("north", north)
                    .texture("south", south).texture("east", east).texture("west", west);
        }

        ModelBuilder cross(String name, Identifier texture) {
            return withExistingParent(name, mcLoc("block/cross")).texture("cross", texture);
        }

        ModelBuilder crop(String name, Identifier texture) {
            return withExistingParent(name, mcLoc("block/crop")).texture("crop", texture);
        }

        ModelBuilder slab(String name, Identifier side, Identifier bottom, Identifier top) {
            return withExistingParent(name, mcLoc("block/slab")).texture("side", side).texture("bottom", bottom).texture("top", top);
        }

        ModelFile getExistingFile(Identifier id) {
            return new ModelFile(id);
        }
    }

    protected final class ItemModelManager extends ModelManager {
        ItemModelManager() {
            super("item");
        }

        ItemModelBuilder withExistingParent(String name, Identifier parent) {
            Identifier location = location(name);
            return ((ItemModelBuilder) builders.computeIfAbsent(location, ItemModelBuilder::new)).parent(parent);
        }

        ItemModelBuilder getBuilder(String name) {
            Identifier location = location(name);
            return (ItemModelBuilder) builders.computeIfAbsent(location, ItemModelBuilder::new);
        }

        ItemModelBuilder nested() {
            return new ItemModelBuilder(null, false);
        }
    }

    protected class ModelBuilder extends ModelFile {
        final JsonObject json = new JsonObject();
        private final boolean emit;

        ModelBuilder(Identifier location) {
            this(location, true);
        }

        ModelBuilder(Identifier location, boolean emit) {
            super(location);
            this.emit = emit;
            if (emit) prov.modelOutput.accept(location, (ModelInstance) () -> json);
        }

        ModelBuilder parent(Identifier parent) {
            json.addProperty("parent", parent.toString());
            return this;
        }

        ModelBuilder parent(ModelFile parent) {
            return parent(parent.location);
        }

        ModelBuilder texture(String key, Identifier texture) {
            JsonObject textures = json.has("textures") ? json.getAsJsonObject("textures") : new JsonObject();
            textures.addProperty(key, texture.toString());
            json.add("textures", textures);
            return this;
        }

        ModelBuilder texture(String key, String texture) {
            JsonObject textures = json.has("textures") ? json.getAsJsonObject("textures") : new JsonObject();
            textures.addProperty(key, texture);
            json.add("textures", textures);
            return this;
        }

        ModelBuilder renderType(String type) {
            json.addProperty("render_type", type.indexOf(':') >= 0 ? type : "minecraft:" + type);
            return this;
        }

        ElementBuilder element() {
            return new ElementBuilder(this);
        }
    }

    protected final class ElementBuilder {
        private final ModelBuilder owner;
        private final JsonObject element = new JsonObject();
        private final JsonObject faces = new JsonObject();
        private final FaceBuilder[] fbHolder = new FaceBuilder[1];

        ElementBuilder(ModelBuilder owner) {
            this.owner = owner;
        }

        ElementBuilder from(float x, float y, float z) {
            element.add("from", vector(x, y, z));
            return this;
        }

        ElementBuilder to(float x, float y, float z) {
            element.add("to", vector(x, y, z));
            return this;
        }

        ElementBuilder allFaces(BiConsumer<Direction, FaceBuilder> action) {
            for (Direction d : Direction.values()) {
                FaceBuilder fb = new FaceBuilder();
                action.accept(d, fb);
                faces.add(d.getSerializedName(), fb.json);
            }
            finish();
            return this;
        }

        ModelBuilder end() {
            finish();
            return owner;
        }

        FaceBuilder face(Direction direction) {
            FaceBuilder fb = new FaceBuilder(() -> {
                faces.add(direction.getSerializedName(), fbHolder[0].json);
                finish();
            });
            fbHolder[0] = fb;
            return fb;
        }

        private void finish() {
            element.add("faces", faces);
            JsonArray elements = owner.json.has("elements") ? owner.json.getAsJsonArray("elements") : new JsonArray();
            if (!elements.contains(element)) elements.add(element);
            owner.json.add("elements", elements);
        }
    }

    protected class ItemModelBuilder extends ModelBuilder {
        ItemModelBuilder(Identifier location) {
            super(location);
        }

        ItemModelBuilder(Identifier location, boolean emit) {
            super(location, emit);
        }

        @Override
        ItemModelBuilder parent(Identifier parent) {
            super.parent(parent);
            return this;
        }

        @Override
        ItemModelBuilder parent(ModelFile parent) {
            super.parent(parent);
            return this;
        }

        @Override
        ItemModelBuilder texture(String key, Identifier texture) {
            super.texture(key, texture);
            return this;
        }

        OverrideBuilder override() {
            return new OverrideBuilder(this);
        }
    }

    protected final class OverrideBuilder {
        private final ItemModelBuilder owner;
        private final JsonObject value = new JsonObject();

        OverrideBuilder(ItemModelBuilder owner) {
            this.owner = owner;
            value.add("predicate", new JsonObject());
        }

        OverrideBuilder predicate(Identifier id, float value) {
            this.value.getAsJsonObject("predicate").addProperty(id.toString(), value);
            return this;
        }

        OverrideBuilder model(ModelFile model) {
            value.addProperty("model", model.location.toString());
            return this;
        }

        ItemModelBuilder end() {
            JsonArray a = owner.json.has("overrides") ? owner.json.getAsJsonArray("overrides") : new JsonArray();
            a.add(value);
            owner.json.add("overrides", a);
            return owner;
        }
    }

    protected final class VariantBuilder {
        private final Block block;
        private final LinkedHashMap<String, ConfiguredModel[]> entries = new LinkedHashMap<>();

        VariantBuilder(Block block) {
            this.block = block;
        }

        PartialBlockstate partialState() {
            return new PartialBlockstate(this);
        }

        VariantBuilder forAllStates(Function<BlockState, ConfiguredModel[]> fn) {
            for (BlockState s : block.getStateDefinition().getPossibleStates()) entries.put(stateKey(s), fn.apply(s));
            return this;
        }

        JsonObject json() {
            JsonObject root = new JsonObject(), vars = new JsonObject();
            entries.forEach((k, v) -> vars.add(k, configured(v)));
            root.add("variants", vars);
            return root;
        }

        private String stateKey(BlockState state) {
            return state.getValues().map(Object::toString).sorted().reduce((a, b) -> a + "," + b).orElse("");
        }
    }

    protected final class PartialBlockstate {
        private final VariantBuilder owner;
        private final LinkedHashMap<Property<?>, Comparable<?>> values = new LinkedHashMap<>();

        PartialBlockstate(VariantBuilder owner) {
            this.owner = owner;
        }

        <T extends Comparable<T>> PartialBlockstate with(Property<T> property, T value) {
            values.put(property, value);
            return this;
        }

        VariantBuilder addModels(ConfiguredModel... models) {
            owner.entries.put(key(), models);
            return owner;
        }

        ConfiguredModelBuilder modelForState() {
            return new ConfiguredModelBuilder(this);
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private String key() {
            return values.entrySet().stream().map(e -> ((Property) e.getKey()).getName() + "=" + ((Property) e.getKey()).getName(e.getValue())).sorted().reduce((a, b) -> a + "," + b).orElse("");
        }
    }

    protected final class ConfiguredModelBuilder {
        private final PartialBlockstate state;
        private ModelFile model;
        private int x, y;
        private boolean uv;

        ConfiguredModelBuilder(PartialBlockstate state) {
            this.state = state;
        }

        ConfiguredModelBuilder modelFile(ModelFile model) {
            this.model = model;
            return this;
        }

        ConfiguredModelBuilder rotationX(int x) {
            this.x = x;
            return this;
        }

        ConfiguredModelBuilder rotationY(int y) {
            this.y = y;
            return this;
        }

        ConfiguredModelBuilder uvLock(boolean uv) {
            this.uv = uv;
            return this;
        }

        VariantBuilder addModel() {
            return state.addModels(new ConfiguredModel(model, x, y, uv, 1));
        }
    }
}
