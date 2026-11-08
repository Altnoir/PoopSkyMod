package com.altnoir.poopsky.content.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

/** 保持配方文件为 Forge 通用 JSON，支持模组输入与带 NBT 的结果。 */
public final class PoRecipeCodecs {
    public static final Codec<Ingredient> INGREDIENT = Codec.PASSTHROUGH.xmap(
            value -> Ingredient.fromJson(value.convert(JsonOps.INSTANCE).getValue()),
            value -> new Dynamic<>(JsonOps.INSTANCE, value.toJson()));
    public static final Codec<ItemStack> ITEM_STACK = Codec.PASSTHROUGH.xmap(
            value -> CraftingHelper.getItemStack(value.convert(JsonOps.INSTANCE).getValue().getAsJsonObject(), true),
            value -> {
                JsonObject json = new JsonObject();
                json.addProperty("item", BuiltInRegistries.ITEM.getKey(value.getItem()).toString());
                json.addProperty("count", value.getCount());
                if (value.hasTag()) json.addProperty("nbt", value.getTag().toString());
                return new Dynamic<>(JsonOps.INSTANCE, json);
            });
    private PoRecipeCodecs() {}
}
