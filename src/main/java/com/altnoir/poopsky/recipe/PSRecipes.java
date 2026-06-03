package com.altnoir.poopsky.recipe;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PSRecipes {
    public static final String SIEVE_RECIPE_FOLDER = "sieve";

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SieveRecipe.Serializer> SIEVE_SERIALIZER = SERIALIZERS
            .register("sieve", SieveRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<SieveRecipe>> SIEVE_TYPE = TYPES
            .register("sieve", () ->
                    RecipeType.simple(PoopSky.loc("sieve")));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}