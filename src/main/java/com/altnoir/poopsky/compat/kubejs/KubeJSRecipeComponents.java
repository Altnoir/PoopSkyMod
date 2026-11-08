package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.FlyBarrelRecipe;
import com.altnoir.poopsky.content.recipe.POPExplosionRecipe;
import com.altnoir.poopsky.content.recipe.SieveRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public final class KubeJSRecipeComponents {
    public static final RecipeComponent<SieveRecipe.ChanceItemStack> SIEVE_OUTPUT = new SieveOutputComponent();
    public static final RecipeComponent<POPExplosionRecipe.Output> BLOCK_OR_ITEM = new BlockOrItemComponent();
    public static final RecipeComponent<FlyBarrelRecipe.Output> FLY_BARREL_RESULT = new FlyBarrelResultComponent();
    public static final RecipeComponent<ItemStack> STACK_INPUT = new StackComponent(dev.latvian.mods.kubejs.recipe.component.ComponentRole.INPUT);
    public static final RecipeComponent<ItemStack> STACK_OUTPUT = new StackComponent(dev.latvian.mods.kubejs.recipe.component.ComponentRole.OUTPUT);

    private abstract static class CodecComponent<T> implements RecipeComponent<T> {
        private final com.mojang.serialization.Codec<T> codec;
        private final Class<T> type;
        CodecComponent(com.mojang.serialization.Codec<T> codec, Class<T> type) { this.codec = codec; this.type = type; }
        @Override public Class<?> componentClass() { return type; }
        @Override public dev.latvian.mods.kubejs.recipe.component.ComponentRole role() { return dev.latvian.mods.kubejs.recipe.component.ComponentRole.OUTPUT; }
        @Override public com.google.gson.JsonElement write(dev.latvian.mods.kubejs.recipe.RecipeJS recipe, T value) {
            return codec.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, value).getOrThrow(false, message -> { throw new IllegalArgumentException(message); });
        }
        @Override public T read(dev.latvian.mods.kubejs.recipe.RecipeJS recipe, Object value) {
            if (value instanceof com.google.gson.JsonElement json) return codec.parse(com.mojang.serialization.JsonOps.INSTANCE, json).getOrThrow(false, message -> { throw new IllegalArgumentException(message); });
            return wrap(recipe, value);
        }
        public abstract T wrap(dev.latvian.mods.kubejs.recipe.RecipeJS recipe, Object value);
    }
    private static final class StackComponent extends CodecComponent<ItemStack> {
        private final dev.latvian.mods.kubejs.recipe.component.ComponentRole role;
        StackComponent(dev.latvian.mods.kubejs.recipe.component.ComponentRole role) { super(com.altnoir.poopsky.content.recipe.PoRecipeCodecs.ITEM_STACK, ItemStack.class); this.role=role; }
        @Override public dev.latvian.mods.kubejs.recipe.component.ComponentRole role() { return role; }
        @Override public ItemStack wrap(dev.latvian.mods.kubejs.recipe.RecipeJS recipe,Object value) { return toItemStack(value); }
    }

    private KubeJSRecipeComponents() {
    }

    private static final class SieveOutputComponent extends CodecComponent<SieveRecipe.ChanceItemStack> {
        private SieveOutputComponent() {
            super(SieveRecipe.ChanceItemStack.CODEC, SieveRecipe.ChanceItemStack.class);
        }

        @Override
        public SieveRecipe.ChanceItemStack wrap(dev.latvian.mods.kubejs.recipe.RecipeJS context, Object value) {
            if (value instanceof SieveRecipe.ChanceItemStack output) {
                return output;
            }
            if (value instanceof Map<?, ?> map) {
                ItemStack item = toItemStack(map.get("item"));
                float chance = map.get("chance") instanceof Number number ? number.floatValue() : 1.0F;
                if (chance < 0.0F || chance > 1.0F) {
                    throw new IllegalArgumentException("Sieve output chance must be between 0.0 and 1.0");
                }
                return new SieveRecipe.ChanceItemStack(item, chance);
            }
            throw new IllegalArgumentException("Invalid sieve output: " + value);
        }
    }

    private static final class BlockOrItemComponent extends CodecComponent<POPExplosionRecipe.Output> {
        private BlockOrItemComponent() {
            super(POPExplosionRecipe.Output.CODEC, POPExplosionRecipe.Output.class);
        }

        @Override
        public POPExplosionRecipe.Output wrap(dev.latvian.mods.kubejs.recipe.RecipeJS context, Object value) {
            if (value instanceof POPExplosionRecipe.Output output) {
                return output;
            }
            if (value instanceof Block block) {
                return new POPExplosionRecipe.Output(block, null);
            }
            if (value instanceof Item item) {
                return new POPExplosionRecipe.Output(null, item);
            }
            if (value instanceof ItemStack stack) {
                return new POPExplosionRecipe.Output(null, stack.getItem());
            }
            if (value instanceof CharSequence text) {
                ResourceLocation id = PoopSky.tryParse(text.toString());
                if (id != null) {
                    Block block = BuiltInRegistries.BLOCK.get(id);
                    if (block != Blocks.AIR) {
                        return new POPExplosionRecipe.Output(block, null);
                    }
                    Item item = BuiltInRegistries.ITEM.get(id);
                    if (item != Items.AIR) {
                        return new POPExplosionRecipe.Output(null, item);
                    }
                }
            }
            throw new IllegalArgumentException("Invalid block or item id: " + value);
        }
    }

    private static final class FlyBarrelResultComponent extends CodecComponent<FlyBarrelRecipe.Output> {
        private FlyBarrelResultComponent() {
            super(FlyBarrelRecipe.Output.CODEC, FlyBarrelRecipe.Output.class);
        }

        @Override
        public FlyBarrelRecipe.Output wrap(dev.latvian.mods.kubejs.recipe.RecipeJS context, Object value) {
            if (value instanceof FlyBarrelRecipe.Output output) {
                return output;
            }
            if (value instanceof ItemStack stack) {
                return new FlyBarrelRecipe.Output(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getCount());
            }
            if (value instanceof Item item) {
                return new FlyBarrelRecipe.Output(BuiltInRegistries.ITEM.getKey(item), 1);
            }
            if (value instanceof CharSequence text) {
                ResourceLocation id = PoopSky.tryParse(text.toString());
                if (id != null) {
                    Item item = BuiltInRegistries.ITEM.get(id);
                    if (item != Items.AIR) {
                        return new FlyBarrelRecipe.Output(id, 1);
                    }
                }
            }
            throw new IllegalArgumentException("Invalid fly barrel result: " + value);
        }
    }

    private static ItemStack toItemStack(Object value) {
        if (value instanceof ItemStack stack) {
            return stack.copy();
        }
        if (value instanceof Item item) {
            return new ItemStack(item);
        }
        if (value instanceof CharSequence text) {
            ResourceLocation id = PoopSky.tryParse(text.toString());
            if (id != null) {
                Item item = BuiltInRegistries.ITEM.get(id);
                if (item != Items.AIR) {
                    return new ItemStack(item);
                }
            }
        }
        throw new IllegalArgumentException("Invalid item id: " + value);
    }
}