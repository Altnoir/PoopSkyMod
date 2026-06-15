package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.recipe.BreedingBoxRecipe;
import com.altnoir.poopsky.recipe.FlyNestRecipe;
import com.altnoir.poopsky.recipe.SieveRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PRecipes {
    public static final String SIEVE_RECIPE_FOLDER = "sieve";
    public static final String FLY_NEST_FOLDER = "fly_nest";
    public static final String BREEDING_BOX_FOLDER = "breeding_box";

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SieveRecipe.Serializer> SIEVE_SERIALIZER = SERIALIZERS
            .register("sieve", SieveRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SieveRecipe>> SIEVE_TYPE = TYPES
            .register("sieve", () ->
                    RecipeType.simple(PoopSky.loc("sieve")));

    // ——— 苍蝇窝配方 ———
    public static final DeferredHolder<RecipeSerializer<?>, FlyNestRecipe.Serializer> FLY_NEST_SERIALIZER = SERIALIZERS
            .register("fly_nest", FlyNestRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FlyNestRecipe>> FLY_NEST_TYPE = TYPES
            .register("fly_nest", () ->
                    RecipeType.simple(PoopSky.loc("fly_nest")));

    // ——— 繁育箱配方 ———
    public static final DeferredHolder<RecipeSerializer<?>, BreedingBoxRecipe.Serializer> BREEDING_BOX_SERIALIZER = SERIALIZERS
            .register("breeding_box", BreedingBoxRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BreedingBoxRecipe>> BREEDING_BOX_TYPE = TYPES
            .register("breeding_box", () ->
                    RecipeType.simple(PoopSky.loc("breeding_box")));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
