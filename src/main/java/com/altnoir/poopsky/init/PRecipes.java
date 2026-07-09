package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final RecipeEntry<SieveRecipe.Serializer, SieveRecipe> SIEVE = register("sieve", SieveRecipe.Serializer::new);
    public static final RecipeEntry<FlyBarrelRecipe.Serializer, FlyBarrelRecipe> FLY_BARREL = register("fly_barrel", FlyBarrelRecipe.Serializer::new);
    public static final RecipeEntry<BreedingChestRecipe.Serializer, BreedingChestRecipe> BREEDING_CHEST = register("breeding_chest", BreedingChestRecipe.Serializer::new);
    public static final RecipeEntry<POPExplosionRecipe.Serializer, POPExplosionRecipe> POP_EXPLOSION = register("pop_explosion", POPExplosionRecipe.Serializer::new);
    public static final RecipeEntry<AnalPressingRecipe.Serializer, AnalPressingRecipe> ANAL_PRESSING = register("anal_pressing", AnalPressingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, ToiletShapedRecipe.Serializer> TOILET_SHAPED_SERIALIZER = SERIALIZERS.register("toilet_shaped", ToiletShapedRecipe.Serializer::new);

    @SuppressWarnings("unchecked")
    private static <S extends RecipeSerializer<?>, R extends Recipe<?>> RecipeEntry<S, R> register(String name, Supplier<? extends S> serializerSupplier) {
        var serializer = (DeferredHolder<RecipeSerializer<?>, S>) SERIALIZERS.register(name, serializerSupplier);
        var type = (DeferredHolder<RecipeType<?>, RecipeType<R>>) (DeferredHolder<?, ?>) TYPES.register(name, () -> RecipeType.simple(PoopSky.loc(name)));
        return new RecipeEntry<>(serializer, type, name);
    }

    public record RecipeEntry<S extends RecipeSerializer<?>, R extends Recipe<?>>(
            DeferredHolder<RecipeSerializer<?>, S> serializer,
            DeferredHolder<RecipeType<?>, RecipeType<R>> type,
            String folder) {
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}