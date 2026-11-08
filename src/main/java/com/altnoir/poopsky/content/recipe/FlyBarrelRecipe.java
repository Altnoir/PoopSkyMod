package com.altnoir.poopsky.content.recipe;

import net.minecraft.world.Container;

import com.altnoir.poopsky.init.PoRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.ByteBufCodecs;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 苍蝇窝产出配方：fly_type -> 产物 ItemStack
 */
public record FlyBarrelRecipe(ResourceLocation id, String flyTypeId, Output result) implements Recipe<Container> {
    public FlyBarrelRecipe(String flyTypeId, Output result) { this(new ResourceLocation("poopsky", "unregistered"), flyTypeId, result); }
    @Override public ResourceLocation getId() { return id; }
    private FlyBarrelRecipe withId(ResourceLocation id) { return new FlyBarrelRecipe(id, flyTypeId, result); }

    public boolean matches(String flyTypeId) {
        return this.flyTypeId.equals(flyTypeId);
    }

    public ItemStack resultStack() {
        return result.toStack();
    }

    @Override
    public boolean matches(Container input, Level level) {
        return false; // 不使用标准 Container 匹配
    }

    @Override
    public ItemStack assemble(Container input, net.minecraft.core.RegistryAccess registries) {
        return resultStack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.RegistryAccess registries) {
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
                ResourceLocation.CODEC.fieldOf("item").forGetter(Output::id),
                Codec.intRange(1, 99).fieldOf("count").forGetter(Output::count)
        ).apply(inst, Output::new));

        public static final StreamCodec<FriendlyByteBuf, Output> STREAM_CODEC =
                StreamCodec.composite(
                        com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.RESOURCE_LOCATION, Output::id,
                        ByteBufCodecs.VAR_INT, Output::count,
                        Output::new);

        public ItemStack toStack() {
            return new ItemStack(BuiltInRegistries.ITEM.get(id), count);
        }
    }

    public static class Serializer implements RecipeSerializer<FlyBarrelRecipe> {
        public static final MapCodec<FlyBarrelRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ExtraCodecs.NON_EMPTY_STRING.fieldOf("fly_type").forGetter(FlyBarrelRecipe::flyTypeId),
                Output.CODEC.fieldOf("result").forGetter(FlyBarrelRecipe::result)
        ).apply(inst, FlyBarrelRecipe::new));

        public static final StreamCodec<FriendlyByteBuf, FlyBarrelRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, FlyBarrelRecipe::flyTypeId,
                        Output.STREAM_CODEC, FlyBarrelRecipe::result,
                        FlyBarrelRecipe::new);

        @Override
        public FlyBarrelRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            return CODEC.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, json)
                    .getOrThrow(false, error -> { throw new com.google.gson.JsonSyntaxException(error); }).withId(id);
        }

        @Override
        public FlyBarrelRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return STREAM_CODEC.decode(buffer).withId(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FlyBarrelRecipe recipe) {
            STREAM_CODEC.encode(buffer, recipe);
        }

        public MapCodec<FlyBarrelRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<FriendlyByteBuf, FlyBarrelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
