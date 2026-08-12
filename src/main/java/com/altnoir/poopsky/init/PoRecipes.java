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

public class PoRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final RecipeEntry<RecipeSerializer<SieveRecipe>, SieveRecipe> SIEVE = register("sieve", () -> SieveRecipe.SERIALIZER);
    public static final RecipeEntry<RecipeSerializer<FlyBarrelRecipe>, FlyBarrelRecipe> FLY_BARREL = register("fly_barrel", () -> FlyBarrelRecipe.SERIALIZER);
    public static final RecipeEntry<RecipeSerializer<BreedingChestRecipe>, BreedingChestRecipe> BREEDING_CHEST = register("breeding_chest", () -> BreedingChestRecipe.SERIALIZER);
    public static final RecipeEntry<RecipeSerializer<POPExplosionRecipe>, POPExplosionRecipe> POP_EXPLOSION = register("pop_explosion", () -> POPExplosionRecipe.SERIALIZER);
    public static final RecipeEntry<RecipeSerializer<AnalPressingRecipe>, AnalPressingRecipe> ANAL_PRESSING = register("anal_pressing", () -> AnalPressingRecipe.SERIALIZER);
    public static final RecipeEntry<RecipeSerializer<CompooperRecipe>, CompooperRecipe> COMPOOPER = register("compooper", () -> CompooperRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ToiletShapedRecipe>> TOILET_SHAPED_SERIALIZER =
            registerSerializer("toilet_shaped", () -> ToiletShapedRecipe.SERIALIZER);

    private static <S extends RecipeSerializer<?>, R extends Recipe<?>> RecipeEntry<S, R> register(String name, Supplier<S> serializerSupplier) {
        DeferredHolder<RecipeSerializer<?>, S> serializer = registerSerializer(name, serializerSupplier);
        DeferredHolder<RecipeType<?>, RecipeType<R>> type = TYPES.register(name, () -> RecipeType.simple(PoopSky.loc(name)));
        return new RecipeEntry<>(serializer, type, name);
    }

    private static <S extends RecipeSerializer<?>> DeferredHolder<RecipeSerializer<?>, S> registerSerializer(String name, Supplier<S> serializerSupplier) {
        return SERIALIZERS.register(name, serializerSupplier);
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
