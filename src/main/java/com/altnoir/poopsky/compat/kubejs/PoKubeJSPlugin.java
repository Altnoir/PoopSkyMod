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
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.core.RecipeManagerKJS;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeMappingRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PoKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        NeoForge.EVENT_BUS.addListener(KubeJSFlyTypeReloadListener::onAddReloadListener);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(PoopSkyStartupEvents.GROUP);
        registry.register(PoopSkyServerEvents.GROUP);
    }

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
        registry.register(KubeJSRecipeComponents.SIEVE_OUTPUT_TYPE);
        registry.register(KubeJSRecipeComponents.BLOCK_OR_ITEM_TYPE);
        registry.register(KubeJSRecipeComponents.FLY_BARREL_RESULT_TYPE);
    }

    @Override
    public void registerRecipeMappings(RecipeMappingRegistry registry) {
        registry.register("sieve", PoopSky.loc("sieve"));
        registry.register("fly_barrel", PoopSky.loc("fly_barrel"));
        registry.register("breeding_chest", PoopSky.loc("breeding_chest"));
        registry.register("pop_explosion", PoopSky.loc("pop_explosion"));
        registry.register("anal_pressing", PoopSky.loc("anal_pressing"));
        registry.register("compooper", PoopSky.loc("compooper"));
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        var poopsky = registry.namespace("poopsky");

        RecipeKey<Ingredient> sieveInput = IngredientComponent.INGREDIENT.inputKey("input");
        RecipeKey<java.util.List<SieveRecipe.ChanceItemStack>> sieveOutputs =
                KubeJSRecipeComponents.SIEVE_OUTPUT.asList().outputKey("outputs");
        RecipeKey<Integer> sieveTime = NumberComponent.NON_NEGATIVE_INT
                .otherKey("processingTime")
                .optional(200);
        poopsky.register("sieve", new RecipeSchema(sieveInput, sieveOutputs, sieveTime)
                .constructor(sieveInput, sieveOutputs)
                .constructor(sieveInput, sieveOutputs, sieveTime));

        RecipeKey<FlyBarrelRecipe.Output> flyBarrelResult = KubeJSRecipeComponents.FLY_BARREL_RESULT.outputKey("result");
        RecipeKey<String> flyType = StringComponent.STRING
                .otherKey("fly_type")
                .alt("flyType");
        poopsky.register("fly_barrel", new RecipeSchema(flyType, flyBarrelResult));

        RecipeKey<String> parent1 = StringComponent.STRING.otherKey("parent1");
        RecipeKey<String> parent2 = StringComponent.STRING.otherKey("parent2");
        RecipeKey<String> breedingResult = StringComponent.STRING.otherKey("result");
        RecipeKey<Float> chance = NumberComponent.NON_NEGATIVE_FLOAT
                .otherKey("chance")
                .optional(0.2F)
                .alwaysWrite();
        poopsky.register("breeding_chest", new RecipeSchema(parent1, parent2, breedingResult, chance)
                .constructor(parent1, parent2, breedingResult)
                .constructor(parent1, parent2, breedingResult, chance));

        RecipeKey<Ingredient> popInput = IngredientComponent.INGREDIENT.inputKey("input");
        RecipeKey<POPExplosionRecipe.Output> popOutput = KubeJSRecipeComponents.BLOCK_OR_ITEM.outputKey("output");
        RecipeKey<Integer> popRadius = NumberComponent.NON_NEGATIVE_INT
                .otherKey("radius")
                .optional(0);
        poopsky.register("pop_explosion", new RecipeSchema(popInput, popOutput, popRadius)
                .constructor(popInput, popOutput)
                .constructor(popInput, popOutput, popRadius));

        RecipeKey<Ingredient> analInput = IngredientComponent.INGREDIENT.inputKey("input");
        RecipeKey<Block> analOutput = BlockComponent.BLOCK.outputKey("output");
        RecipeKey<Block> replaceTarget = BlockComponent.BLOCK
                .otherKey("replace_target")
                .alt("replaceTarget")
                .optional(Blocks.STONE);
        RecipeKey<Integer> analRadius = NumberComponent.NON_NEGATIVE_INT
                .otherKey("radius")
                .optional(1);
        poopsky.register("anal_pressing", new RecipeSchema(analInput, analOutput, replaceTarget, analRadius)
                .constructor(analInput, analOutput, replaceTarget, analRadius));

        RecipeKey<String> fluidType = StringComponent.STRING
                .otherKey("fluid_type")
                .alt("fluidType");
        RecipeKey<ItemStack> compooperInput = ItemStackComponent.ITEM_STACK.inputKey("input");
        RecipeKey<ItemStack> compooperOutput = ItemStackComponent.ITEM_STACK.outputKey("output");
        poopsky.register("compooper", new RecipeSchema(fluidType, compooperInput, compooperOutput)
                .constructor(fluidType, compooperInput, compooperOutput));
    }

    @Override
    public void afterScriptsLoaded(ScriptManager scriptManager) {
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
    public void beforeRecipeLoading(RecipesKubeEvent event, RecipeManagerKJS manager, Map<ResourceLocation, JsonElement> recipes) {
        syncKubeJsFlyTypes(true);
        int injected = 0;
        for (FlyTypeBuilder builder : PoFlyTypes.INSTANCE.builders()) {
            for (GeneratedRecipe recipe : generatedRecipes(builder)) {
                recipes.put(recipe.id(), recipe.json());
                injected++;
            }
        }
        PoopSky.LOGGER.info("KubeJS beforeRecipeLoading builders={}, injected={}", PoFlyTypes.INSTANCE.builders().size(), injected);
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
    public void generateData(KubeDataGenerator generator) {
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
    public void generateAssets(KubeAssetGenerator generator) {
        for (FlyTypeDefinition definition : PoFlyTypes.INSTANCE.storedDefinitions()) {
            ResourceLocation model = PoopSky.loc("item/fly_" + definition.id());
            generator.itemModel(model, modelGenerator -> {
                modelGenerator.parent(ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"));
                modelGenerator.texture("layer0", definition.texture().toString());
            });
        }
    }

    @Override
    public void generateLang(LangKubeEvent event) {
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
            result.addProperty("id", builder.flyBarrelResult());
            result.addProperty("count", builder.flyBarrelCount());
            recipe.add("result", result);
            recipes.add(new GeneratedRecipe(PoopSky.loc("recipe/fly_barrel/" + builder.id()), recipe));
        }
        for (FlyTypeBuilder.BreedingRecipe breeding : builder.breedingRecipes()) {
            JsonObject recipe = new JsonObject();
            recipe.addProperty("type", "poopsky:breeding_chest");
            recipe.addProperty("parent1", breeding.parent1());
            recipe.addProperty("parent2", breeding.parent2());
            recipe.addProperty("result", builder.id());
            recipe.addProperty("chance", breeding.chance());
            String path = "recipe/breeding_chest/" + builder.id() + "_from_" + breeding.parent1() + "_and_" + breeding.parent2();
            recipes.add(new GeneratedRecipe(PoopSky.loc(path), recipe));
        }
        return recipes;
    }

    private record GeneratedRecipe(ResourceLocation id, JsonObject json) {
    }
}
