package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PoRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(net.minecraftforge.registries.ForgeRegistries.RECIPE_SERIALIZERS, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final RecipeEntry<SieveRecipe.Serializer, SieveRecipe> SIEVE = register("sieve", SieveRecipe.Serializer::new);
    public static final RecipeEntry<FlyBarrelRecipe.Serializer, FlyBarrelRecipe> FLY_BARREL = register("fly_barrel", FlyBarrelRecipe.Serializer::new);
    public static final RecipeEntry<BreedingChestRecipe.Serializer, BreedingChestRecipe> BREEDING_CHEST = register("breeding_chest", BreedingChestRecipe.Serializer::new);
    public static final RecipeEntry<POPExplosionRecipe.Serializer, POPExplosionRecipe> POP_EXPLOSION = register("pop_explosion", POPExplosionRecipe.Serializer::new);
    public static final RecipeEntry<AnalPressingRecipe.Serializer, AnalPressingRecipe> ANAL_PRESSING = register("anal_pressing", AnalPressingRecipe.Serializer::new);
    public static final RecipeEntry<CompooperRecipe.Serializer, CompooperRecipe> COMPOOPER = register("compooper", CompooperRecipe.Serializer::new);

    public static final RegistryObject<ToiletShapedRecipe.Serializer> TOILET_SHAPED_SERIALIZER = registerSerializer("toilet_shaped", ToiletShapedRecipe.Serializer::new);

    @SuppressWarnings("unchecked")
    private static <S extends RecipeSerializer<?>, R extends Recipe<?>> RecipeEntry<S, R> register(String name, Supplier<? extends S> serializerSupplier) {
        var serializer = (RegistryObject<S>) registerSerializer(name, serializerSupplier);
        var type = (RegistryObject<RecipeType<R>>) (RegistryObject<?>) TYPES.register(name, () -> RecipeType.simple(PoopSky.loc(name)));
        return new RecipeEntry<>(serializer, type, name);
    }

    private static <S extends RecipeSerializer<?>> RegistryObject<S> registerSerializer(String name, Supplier<? extends S> serializerSupplier) {
        return SERIALIZERS.register(name, serializerSupplier);
    }

    public record RecipeEntry<S extends RecipeSerializer<?>, R extends Recipe<?>>(
            RegistryObject<S> serializer,
            RegistryObject<RecipeType<R>> type,
            String folder) {
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
