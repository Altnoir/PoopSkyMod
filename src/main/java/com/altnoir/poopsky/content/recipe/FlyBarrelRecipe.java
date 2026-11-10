package com.altnoir.poopsky.content.recipe;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 苍蝇窝产出配方：fly_type -> 产物 ItemStack
 */
public record FlyBarrelRecipe(String flyTypeId, Output result, FluidInput fluidInputs) implements Recipe<RecipeInput> {

    public FlyBarrelRecipe(String flyTypeId, Output result) {
        this(flyTypeId, result, new FluidInput(PoopSky.loc("urine"), 800));
    }

    public boolean matches(String flyTypeId) {
        return this.flyTypeId.equals(flyTypeId);
    }

    public ItemStack resultStack() {
        return result.toStack();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false; // 不使用标准 RecipeInput 匹配
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return resultStack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return resultStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PoRecipes.FLY_BARREL.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return PoRecipes.FLY_BARREL.type().get();
    }

    public record Output(ResourceLocation id, int count) {
        public static final Codec<Output> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(Output::id),
                ExtraCodecs.intRange(1, 99).fieldOf("count").forGetter(Output::count)
        ).apply(inst, Output::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Output> STREAM_CODEC =
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC, Output::id,
                        ByteBufCodecs.VAR_INT, Output::count,
                        Output::new);

        public ItemStack toStack() {
            return new ItemStack(BuiltInRegistries.ITEM.get(id), count);
        }
    }

    public record FluidInput(ResourceLocation fluid, int amount) {
        public static final Codec<FluidInput> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidInput::amount),
                ResourceLocation.CODEC.fieldOf("fluid").forGetter(FluidInput::fluid)
        ).apply(inst, (amount, fluid) -> new FluidInput(fluid, amount)));
        public static final StreamCodec<RegistryFriendlyByteBuf, FluidInput> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, FluidInput::fluid,
                ByteBufCodecs.VAR_INT, FluidInput::amount, FluidInput::new);

        public FluidStack toStack() {
            return new FluidStack(BuiltInRegistries.FLUID.get(fluid), amount);
        }
    }

    public static class Serializer implements RecipeSerializer<FlyBarrelRecipe> {
        public static final MapCodec<FlyBarrelRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ExtraCodecs.NON_EMPTY_STRING.fieldOf("fly_type").forGetter(FlyBarrelRecipe::flyTypeId),
                Output.CODEC.fieldOf("result").forGetter(FlyBarrelRecipe::result),
                FluidInput.CODEC.fieldOf("fluid_inputs").forGetter(FlyBarrelRecipe::fluidInputs)
        ).apply(inst, FlyBarrelRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FlyBarrelRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, FlyBarrelRecipe::flyTypeId,
                        Output.STREAM_CODEC, FlyBarrelRecipe::result,
                        FluidInput.STREAM_CODEC, FlyBarrelRecipe::fluidInputs,
                        FlyBarrelRecipe::new);

        @Override
        public MapCodec<FlyBarrelRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FlyBarrelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
