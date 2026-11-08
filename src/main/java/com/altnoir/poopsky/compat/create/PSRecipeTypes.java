package com.altnoir.poopsky.compat.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.create.content.kinetics.fan.processing.DigestingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unchecked")
public enum PSRecipeTypes implements IRecipeTypeInfo {
    DIGESTING(DigestingRecipe::new);
    public final ResourceLocation id;
    public final RegistryObject<RecipeSerializer<?>> serializerObject;
    public final RegistryObject<RecipeType<?>> typeObject;
    PSRecipeTypes(ProcessingRecipeFactory<?> factory) {
        String name = name().toLowerCase(java.util.Locale.ROOT);
        id = PoopSky.loc(name);
        serializerObject = Registers.SERIALIZERS.register(name, () -> new ProcessingRecipeSerializer<>(factory));
        typeObject = Registers.TYPES.register(name, () -> new RecipeType<DigestingRecipe>() {
            @Override public String toString() { return id.toString(); }
        });
    }
    public static void register(IEventBus bus) { Registers.SERIALIZERS.register(bus); Registers.TYPES.register(bus); }
    @Override public ResourceLocation getId() { return id; }
    @Override public <T extends RecipeSerializer<?>> T getSerializer() { return (T) serializerObject.get(); }
    @Override public <T extends RecipeType<?>> T getType() { return (T) typeObject.get(); }
    private static class Registers {
        static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PoopSky.MOD_ID);
        static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, PoopSky.MOD_ID);
    }
}
