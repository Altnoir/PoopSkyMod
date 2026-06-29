package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.recipe.*;
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
    public static final String POP_EXPLOSION_RECIPE_FOLDER = "pop_explosion";
    public static final String ANAL_PRESSING_FOLDER = "anal_pressing";

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SieveRecipe.Serializer> SIEVE_SERIALIZER = SERIALIZERS
            .register("sieve", SieveRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SieveRecipe>> SIEVE_TYPE = TYPES
            .register("sieve", () ->
                    RecipeType.simple(PoopSky.loc("sieve")));

    public static final DeferredHolder<RecipeSerializer<?>, FlyNestRecipe.Serializer> FLY_NEST_SERIALIZER = SERIALIZERS
            .register("fly_nest", FlyNestRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FlyNestRecipe>> FLY_NEST_TYPE = TYPES
            .register("fly_nest", () ->
                    RecipeType.simple(PoopSky.loc("fly_nest")));

    public static final DeferredHolder<RecipeSerializer<?>, BreedingBoxRecipe.Serializer> BREEDING_BOX_SERIALIZER = SERIALIZERS
            .register("breeding_box", BreedingBoxRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BreedingBoxRecipe>> BREEDING_BOX_TYPE = TYPES
            .register("breeding_box", () ->
                    RecipeType.simple(PoopSky.loc("breeding_box")));
    public static final DeferredHolder<RecipeSerializer<?>, POPExplosionRecipe.Serializer> EXPLOSION_TRANSFORM_SERIALIZER = SERIALIZERS
            .register("pop_explosion", POPExplosionRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<POPExplosionRecipe>> EXPLOSION_TRANSFORM_TYPE = TYPES
            .register("pop_explosion", () ->
                    RecipeType.simple(PoopSky.loc("pop_explosion")));

    public static final DeferredHolder<RecipeSerializer<?>, AnalPressingRecipe.Serializer> ANAL_PRESSING_SERIALIZER = SERIALIZERS
            .register("anal_pressing", AnalPressingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AnalPressingRecipe>> ANAL_PRESSING_TYPE = TYPES
            .register("anal_pressing", () ->
                    RecipeType.simple(PoopSky.loc("anal_pressing")));

    public static final DeferredHolder<RecipeSerializer<?>, ToiletShapedRecipe.Serializer> TOILET_SHAPED_SERIALIZER = SERIALIZERS
            .register("toilet_shaped", ToiletShapedRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}