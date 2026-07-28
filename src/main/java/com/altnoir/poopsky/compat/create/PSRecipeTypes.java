package com.altnoir.poopsky.compat.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.create.content.kinetics.fan.processing.DigestingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unchecked")
public enum PSRecipeTypes implements IRecipeTypeInfo {
    DIGESTING(DigestingRecipe::new);

    public final ResourceLocation id;
    public final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    public final DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject;

    PSRecipeTypes(StandardProcessingRecipe.Factory<?> processingFactory) {
        String name = name().toLowerCase();
        id = PoopSky.loc(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, () -> new StandardProcessingRecipe.Serializer<>(processingFactory));
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
    }

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) typeObject.get();
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER =
                DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER =
                DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);
    }
}