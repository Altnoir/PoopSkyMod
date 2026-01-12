package com.altnoir.poopsky.recipe;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PSRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, PoopSky.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RearingChamberRecipe>> REARING_CHAMBER_SERIALIZER = SERIALIZERS
            .register("rearing_chamber", RearingChamberRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<RearingChamberRecipe>> REARING_CHAMBER_TYPE = TYPES
            .register("rearing_chamber", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return PoopSky.MOD_ID + ":rearing_chamber";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}