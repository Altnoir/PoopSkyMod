package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.PoItemGroups;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyTypeDefinition;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.recipe.FlyBarrelRecipe;
import com.altnoir.poopsky.content.recipe.POPExplosionRecipe;
import com.altnoir.poopsky.content.recipe.SieveRecipe;
import com.altnoir.poopsky.impl.creative.PoSectionedCreativeModeTab;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.common.MinecraftForge;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PoKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.addListener(KubeJSFlyTypeReloadListener::onAddReloadListener);
    }

    @Override
    public void registerEvents() {
        PoopSkyStartupEvents.GROUP.register();
        PoopSkyServerEvents.GROUP.register();
    }

    @Override
    public void registerRecipeComponents(dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistryEvent registry) {
        registry.register("poopsky:sieve_output", KubeJSRecipeComponents.SIEVE_OUTPUT);
        registry.register("poopsky:block_or_item", KubeJSRecipeComponents.BLOCK_OR_ITEM);
        registry.register("poopsky:fly_barrel_result", KubeJSRecipeComponents.FLY_BARREL_RESULT);
    }


    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent registry) {
        var poopsky = registry.namespace("poopsky");

        RecipeKey<dev.latvian.mods.kubejs.item.InputItem> sieveInput = ItemComponents.INPUT.key("input");
        RecipeKey<SieveRecipe.ChanceItemStack[]> sieveOutputs =
                KubeJSRecipeComponents.SIEVE_OUTPUT.asArray().key("outputs");
        RecipeKey<Integer> sieveTime = NumberComponent.intRange(0, Integer.MAX_VALUE)
                .key("processingTime")
                .optional(200);
        poopsky.register("sieve", new RecipeSchema(sieveInput, sieveOutputs, sieveTime)
                .constructor(sieveInput, sieveOutputs)
                .constructor(sieveInput, sieveOutputs, sieveTime));

        RecipeKey<FlyBarrelRecipe.Output> flyBarrelResult = KubeJSRecipeComponents.FLY_BARREL_RESULT.key("result");
        RecipeKey<String> flyType = StringComponent.ANY
                .key("fly_type")
                .alt("flyType");
        poopsky.register("fly_barrel", new RecipeSchema(flyType, flyBarrelResult));

        RecipeKey<String> parent1 = StringComponent.ANY.key("parent1");
        RecipeKey<String> parent2 = StringComponent.ANY.key("parent2");
        RecipeKey<String> breedingResult = StringComponent.ANY.key("result");
        RecipeKey<Float> chance = NumberComponent.floatRange(0.0F, Float.MAX_VALUE)
                .key("chance")
                .optional(0.2F)
                .alwaysWrite();
        poopsky.register("breeding_chest", new RecipeSchema(parent1, parent2, breedingResult, chance)
                .constructor(parent1, parent2, breedingResult)
                .constructor(parent1, parent2, breedingResult, chance));

        RecipeKey<dev.latvian.mods.kubejs.item.InputItem> popInput = ItemComponents.INPUT.key("input");
        RecipeKey<POPExplosionRecipe.Output> popOutput = KubeJSRecipeComponents.BLOCK_OR_ITEM.key("output");
        RecipeKey<Integer> popRadius = NumberComponent.intRange(0, Integer.MAX_VALUE)
                .key("radius")
                .optional(0);
        poopsky.register("pop_explosion", new RecipeSchema(popInput, popOutput, popRadius)
                .constructor(popInput, popOutput)
                .constructor(popInput, popOutput, popRadius));

        RecipeKey<dev.latvian.mods.kubejs.item.InputItem> analInput = ItemComponents.INPUT.key("input");
        RecipeKey<Block> analOutput = BlockComponent.BLOCK.key("output");
        RecipeKey<Block> replaceTarget = BlockComponent.BLOCK
                .key("replace_target")
                .alt("replaceTarget")
                .optional(Blocks.STONE);
        RecipeKey<Integer> analRadius = NumberComponent.intRange(0, Integer.MAX_VALUE)
                .key("radius")
                .optional(1);
        poopsky.register("anal_pressing", new RecipeSchema(analInput, analOutput, replaceTarget, analRadius)
                .constructor(analInput, analOutput, replaceTarget, analRadius));

        RecipeKey<String> fluidType = StringComponent.ANY
                .key("fluid_type")
                .alt("fluidType");
        var compooperInput = KubeJSRecipeComponents.STACK_INPUT.key("input");
        var compooperOutput = KubeJSRecipeComponents.STACK_OUTPUT.key("output");
        poopsky.register("compooper", new RecipeSchema(fluidType, compooperInput, compooperOutput)
                .constructor(fluidType, compooperInput, compooperOutput));
    }

    public static void afterScriptsLoaded(ScriptManager scriptManager) {
        if (scriptManager.scriptType.isStartup()) {
            PoopSkyStartupEvents.FLY_TYPE.post(ScriptType.STARTUP, new FlyTypeKubeEvent());
            syncKubeJsFlyTypes(false);
            return;
        }
        if (!scriptManager.scriptType.isServer()) {
            return;
        }
        PoFlyTypes.INSTANCE.clear();
        PoCustomBosses.INSTANCE.clear();
        PoopSkyServerEvents.FLY_TYPE.post(ScriptType.SERVER, new ServerFlyTypeKubeEvent());
        PoopSkyServerEvents.CUSTOM_BOSS.post(ScriptType.SERVER, new CustomBossKubeEvent());
        syncKubeJsFlyTypes(true);
        PoCustomBosses.INSTANCE.store();
        PoopSky.LOGGER.info("Registered {} KubeJS fly types: {}", PoFlyTypes.INSTANCE.builders().size(), PoFlyTypes.INSTANCE.builders().stream().map(FlyTypeBuilder::id).toList());
        PoopSky.LOGGER.info("Registered {} KubeJS custom bosses: {}", PoCustomBosses.INSTANCE.definitions().size(), PoCustomBosses.INSTANCE.definitions().stream().map(CustomBossDefinition::id).toList());
    }

    @Override
    public void injectRuntimeRecipes(RecipesEventJS event, net.minecraft.world.item.crafting.RecipeManager manager,
            Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>> recipes) {
        syncKubeJsFlyTypes(true);
        for (FlyTypeBuilder builder : PoFlyTypes.INSTANCE.builders()) {
            for (GeneratedRecipe recipe : generatedRecipes(builder)) {
                String path = recipe.id().getPath();
                ResourceLocation id = PoopSky.loc(path.startsWith("recipes/") ? path.substring(8) : path);
                recipes.put(id, net.minecraft.world.item.crafting.RecipeManager.fromJson(id, recipe.json()));
            }
        }
    }

    private static void syncKubeJsFlyTypes(boolean rebuildCreativeTab) {
        if (PoFlyTypes.INSTANCE.builders().isEmpty()) {
            PoopSkyServerEvents.FLY_TYPE.post(ScriptType.SERVER, new ServerFlyTypeKubeEvent());
        }
        PoFlyTypes.INSTANCE.store(PoFlyTypes.INSTANCE.definitions());
        FlyTypeManager.INSTANCE.replaceKubeJsDefinitions(PoFlyTypes.INSTANCE.storedDefinitions());
        if (rebuildCreativeTab
                && FMLLoader.getDist().isClient()
                && PoItemGroups.POOPSKY_TAB.get() instanceof PoSectionedCreativeModeTab tab) {
            tab.rebuild();
        }
        if (FMLLoader.getDist().isClient()) {
            writeKubeJsModels();
        }
    }

    private static void writeKubeJsModels() {
        try {
            Path modelDir = KubeJSPaths.ASSETS.resolve("poopsky/models/item");
            Files.createDirectories(modelDir);
            for (FlyTypeDefinition definition : PoFlyTypes.INSTANCE.storedDefinitions()) {
                JsonObject model = new JsonObject();
                model.addProperty("parent", "minecraft:item/generated");
                JsonObject textures = new JsonObject();
                textures.addProperty("layer0", definition.texture().toString());
                model.add("textures", textures);
                Path modelFile = modelDir.resolve("fly_" + definition.id() + ".json");
                Files.writeString(modelFile, model.toString(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            PoopSky.LOGGER.error("Failed to write KubeJS fly model assets", e);
        }
    }

    @Override
    public void generateDataJsons(DataJsonGenerator generator) {
        JsonArray values = new JsonArray();
        for (FlyTypeBuilder builder : PoFlyTypes.INSTANCE.builders()) {
            values.add(builder.id());
            for (GeneratedRecipe recipe : generatedRecipes(builder)) {
                generator.json(recipe.id(), recipe.json());
            }
        }
        if (!values.isEmpty()) {
            JsonObject flyTypes = new JsonObject();
            flyTypes.addProperty("replace", false);
            flyTypes.add("values", values);
            generator.json(PoopSky.loc("poopsky_data/fly_types"), flyTypes);
        }
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        for (FlyTypeDefinition definition : PoFlyTypes.INSTANCE.storedDefinitions()) {
            ResourceLocation model = PoopSky.loc("item/fly_" + definition.id());
            generator.itemModel(model, modelGenerator -> {
                modelGenerator.parent("minecraft:item/generated");
                modelGenerator.texture("layer0", definition.texture().toString());
            });
        }
    }

    @Override
    public void generateLang(LangEventJS event) {
        for (FlyTypeDefinition definition : PoFlyTypes.INSTANCE.storedDefinitions()) {
            event.add("fly_type.poopsky." + definition.id(), definition.displayName());
        }
    }

    @Override
    public void clearCaches() {
        PoFlyTypes.INSTANCE.clear();
    }

    private static List<GeneratedRecipe> generatedRecipes(FlyTypeBuilder builder) {
        List<GeneratedRecipe> recipes = new ArrayList<>();
        if (builder.hasFlyBarrel()) {
            JsonObject recipe = new JsonObject();
            recipe.addProperty("type", "poopsky:fly_barrel");
            recipe.addProperty("fly_type", builder.id());
            JsonObject result = new JsonObject();
            result.addProperty("item", builder.flyBarrelResult());
            result.addProperty("count", builder.flyBarrelCount());
            recipe.add("result", result);
            recipes.add(new GeneratedRecipe(PoopSky.loc("recipes/fly_barrel/" + builder.id()), recipe));
        }
        for (FlyTypeBuilder.BreedingRecipe breeding : builder.breedingRecipes()) {
            JsonObject recipe = new JsonObject();
            recipe.addProperty("type", "poopsky:breeding_chest");
            recipe.addProperty("parent1", breeding.parent1());
            recipe.addProperty("parent2", breeding.parent2());
            recipe.addProperty("result", builder.id());
            recipe.addProperty("chance", breeding.chance());
            String path = "recipes/breeding_chest/" + builder.id() + "_from_" + breeding.parent1() + "_and_" + breeding.parent2();
            recipes.add(new GeneratedRecipe(PoopSky.loc(path), recipe));
        }
        return recipes;
    }

    private record GeneratedRecipe(ResourceLocation id, JsonObject json) {
    }
}
