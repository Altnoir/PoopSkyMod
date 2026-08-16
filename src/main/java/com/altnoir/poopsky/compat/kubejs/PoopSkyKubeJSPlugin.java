package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.recipe.FlyBarrelRecipe;
import com.altnoir.poopsky.content.recipe.POPExplosionRecipe;
import com.altnoir.poopsky.content.recipe.SieveRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PoopSkyKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        NeoForge.EVENT_BUS.addListener(KubeJSFlyTypeReloadListener::onAddReloadListener);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(PoopSkyEvents.GROUP);
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
        registry.register("fly_barrel", PoopSky.loc("flyBarrel"));
        registry.register("breeding_chest", PoopSky.loc("breedingChest"));
        registry.register("pop_explosion", PoopSky.loc("popExplosion"));
        registry.register("anal_pressing", PoopSky.loc("analPressing"));
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
        poopsky.register("flyBarrel", new RecipeSchema(flyBarrelResult, flyType)
                .constructor(flyType, flyBarrelResult));

        RecipeKey<String> parent1 = StringComponent.STRING.otherKey("parent1");
        RecipeKey<String> parent2 = StringComponent.STRING.otherKey("parent2");
        RecipeKey<String> breedingResult = StringComponent.STRING.otherKey("result");
        RecipeKey<Float> chance = NumberComponent.NON_NEGATIVE_FLOAT
                .otherKey("chance")
                .optional(0.2F)
                .alwaysWrite();
        poopsky.register("breedingChest", new RecipeSchema(parent1, parent2, breedingResult, chance)
                .constructor(parent1, parent2, breedingResult)
                .constructor(parent1, parent2, breedingResult, chance));

        RecipeKey<Ingredient> popInput = IngredientComponent.INGREDIENT.inputKey("input");
        RecipeKey<POPExplosionRecipe.Output> popOutput = KubeJSRecipeComponents.BLOCK_OR_ITEM.outputKey("output");
        RecipeKey<Integer> popRadius = NumberComponent.NON_NEGATIVE_INT
                .otherKey("radius")
                .optional(0);
        poopsky.register("popExplosion", new RecipeSchema(popInput, popOutput, popRadius)
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
        poopsky.register("analPressing", new RecipeSchema(analInput, analOutput, replaceTarget, analRadius)
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
        if (!scriptManager.scriptType.isServer()) {
            return;
        }
        PoopSkyFlyTypes.INSTANCE.clear();
        PoopSkyEvents.FLY_TYPE.post(new FlyTypeKubeEvent());
        FlyTypeManager.INSTANCE.replaceKubeJsDefinitions(PoopSkyFlyTypes.INSTANCE.definitions());
    }

    @Override
    public void beforeRecipeLoading(RecipesKubeEvent event, RecipeManagerKJS manager, Map<ResourceLocation, JsonElement> recipes) {
        for (FlyTypeBuilder builder : PoopSkyFlyTypes.INSTANCE.builders()) {
            for (GeneratedRecipe recipe : generatedRecipes(builder)) {
                recipes.put(recipe.id(), recipe.json());
            }
        }
    }

    @Override
    public void generateData(KubeDataGenerator generator) {
        JsonArray values = new JsonArray();
        for (FlyTypeBuilder builder : PoopSkyFlyTypes.INSTANCE.builders()) {
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
        for (FlyTypeBuilder builder : PoopSkyFlyTypes.INSTANCE.builders()) {
            ResourceLocation model = PoopSky.loc("item/fly_" + builder.id());
            generator.itemModel(model, modelGenerator -> {
                modelGenerator.parent(ResourceLocation.fromNamespaceAndPath("minecraft", "item/generated"));
                modelGenerator.texture("layer0", builder.texture().toString());
            });
        }
    }

    @Override
    public void generateLang(LangKubeEvent event) {
        for (FlyTypeBuilder builder : PoopSkyFlyTypes.INSTANCE.builders()) {
            event.add("fly_type.poopsky." + builder.id(), builder.toDefinition().displayName());
        }
    }

    @Override
    public void clearCaches() {
        PoopSkyFlyTypes.INSTANCE.clear();
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
