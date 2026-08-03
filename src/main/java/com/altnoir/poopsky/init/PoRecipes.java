package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.*;
import com.tterrag.registrate.fabric.registry.DeferredHolder;
import com.tterrag.registrate.fabric.registry.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class PoRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final RecipeEntry<SieveRecipe.Serializer, SieveRecipe> SIEVE = register("sieve", SieveRecipe.Serializer::new);
    public static final RecipeEntry<FlyBarrelRecipe.Serializer, FlyBarrelRecipe> FLY_BARREL = register("fly_barrel", FlyBarrelRecipe.Serializer::new);
    public static final RecipeEntry<BreedingChestRecipe.Serializer, BreedingChestRecipe> BREEDING_CHEST = register("breeding_chest", BreedingChestRecipe.Serializer::new);
    public static final RecipeEntry<POPExplosionRecipe.Serializer, POPExplosionRecipe> POP_EXPLOSION = register("pop_explosion", POPExplosionRecipe.Serializer::new);
    public static final RecipeEntry<AnalPressingRecipe.Serializer, AnalPressingRecipe> ANAL_PRESSING = register("anal_pressing", AnalPressingRecipe.Serializer::new);
    public static final RecipeEntry<CompooperRecipe.Serializer, CompooperRecipe> COMPOOPER = register("compooper", CompooperRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, ToiletShapedRecipe.Serializer> TOILET_SHAPED_SERIALIZER = registerSerializer("toilet_shaped", ToiletShapedRecipe.Serializer::new);

    @SuppressWarnings("unchecked")
    private static <S extends RecipeSerializer<?>, R extends Recipe<?>> RecipeEntry<S, R> register(String name, Supplier<? extends S> serializerSupplier) {
        var serializer = (DeferredHolder<RecipeSerializer<?>, S>) registerSerializer(name, serializerSupplier);
        var type = (DeferredHolder<RecipeType<?>, RecipeType<R>>) (DeferredHolder<?, ?>) TYPES.register(name, () -> simple(PoopSky.loc(name)));
        return new RecipeEntry<>(serializer, type, name);
    }

    private static <S extends RecipeSerializer<?>> DeferredHolder<RecipeSerializer<?>, S> registerSerializer(String name, Supplier<? extends S> serializerSupplier) {
        return SERIALIZERS.register(name, serializerSupplier);
    }

    public record RecipeEntry<S extends RecipeSerializer<?>, R extends Recipe<?>>(
            DeferredHolder<RecipeSerializer<?>, S> serializer,
            DeferredHolder<RecipeType<?>, RecipeType<R>> type,
            String folder) {
    }

    // port from https://github.com/neoforged/NeoForge/blob/1.21.1/patches/net/minecraft/world/item/crafting/RecipeType.java.patch
    public static <T extends Recipe<?>> RecipeType<T> simple(final ResourceLocation name) {
        final String toString = name.toString();
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return toString;
            }
        };
    }

    public static void register() {
        SERIALIZERS.register();
        TYPES.register();
    }
}
