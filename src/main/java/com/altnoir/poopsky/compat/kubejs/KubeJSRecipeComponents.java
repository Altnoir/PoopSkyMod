package com.altnoir.poopsky.compat.kubejs;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.FlyBarrelRecipe;
import com.altnoir.poopsky.content.recipe.POPExplosionRecipe;
import com.altnoir.poopsky.content.recipe.SieveRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public final class KubeJSRecipeComponents {
    public static final RecipeComponentType<SieveRecipe.ChanceItemStack> SIEVE_OUTPUT_TYPE =
            RecipeComponentType.unit(PoopSky.loc("sieve_output"), SieveOutputComponent::new);
    public static final RecipeComponent<SieveRecipe.ChanceItemStack> SIEVE_OUTPUT = SIEVE_OUTPUT_TYPE.instance();

    public static final RecipeComponentType<POPExplosionRecipe.Output> BLOCK_OR_ITEM_TYPE =
            RecipeComponentType.unit(PoopSky.loc("block_or_item"), BlockOrItemComponent::new);
    public static final RecipeComponent<POPExplosionRecipe.Output> BLOCK_OR_ITEM = BLOCK_OR_ITEM_TYPE.instance();

    public static final RecipeComponentType<FlyBarrelRecipe.Output> FLY_BARREL_RESULT_TYPE =
            RecipeComponentType.unit(PoopSky.loc("fly_barrel_result"), FlyBarrelResultComponent::new);
    public static final RecipeComponent<FlyBarrelRecipe.Output> FLY_BARREL_RESULT = FLY_BARREL_RESULT_TYPE.instance();

    private KubeJSRecipeComponents() {
    }

    private static final class SieveOutputComponent extends SimpleRecipeComponent<SieveRecipe.ChanceItemStack> {
        private SieveOutputComponent(RecipeComponentType<SieveRecipe.ChanceItemStack> type) {
            super(type, SieveRecipe.ChanceItemStack.CODEC, TypeInfo.OBJECT);
        }

        @Override
        public SieveRecipe.ChanceItemStack wrap(RecipeScriptContext context, Object value) {
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

    private static final class BlockOrItemComponent extends SimpleRecipeComponent<POPExplosionRecipe.Output> {
        private BlockOrItemComponent(RecipeComponentType<POPExplosionRecipe.Output> type) {
            super(type, POPExplosionRecipe.Output.CODEC, TypeInfo.OBJECT);
        }

        @Override
        public POPExplosionRecipe.Output wrap(RecipeScriptContext context, Object value) {
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
                ResourceLocation id = ResourceLocation.tryParse(text.toString());
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

    private static final class FlyBarrelResultComponent extends SimpleRecipeComponent<FlyBarrelRecipe.Output> {
        private FlyBarrelResultComponent(RecipeComponentType<FlyBarrelRecipe.Output> type) {
            super(type, FlyBarrelRecipe.Output.CODEC, TypeInfo.OBJECT);
        }

        @Override
        public FlyBarrelRecipe.Output wrap(RecipeScriptContext context, Object value) {
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
                ResourceLocation id = ResourceLocation.tryParse(text.toString());
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
            ResourceLocation id = ResourceLocation.tryParse(text.toString());
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