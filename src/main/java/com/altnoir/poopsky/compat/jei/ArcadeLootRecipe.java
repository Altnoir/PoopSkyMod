package com.altnoir.poopsky.compat.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ArcadeLootRecipe(ResourceLocation id, ItemStack input, List<Output> outputs) {
    public static final int OUTPUT_CAPACITY = ArcadeLootRecipeCategory.OUTPUT_COLUMNS * ArcadeLootRecipeCategory.OUTPUT_ROWS;

    public record Output(ItemStack item, @Nullable TagKey<Item> tag, float chance) {
        public static Output item(ItemStack item, float chance) {
            return new Output(item, null, chance);
        }

        public static Output tag(TagKey<Item> tag, float chance) {
            return new Output(ItemStack.EMPTY, tag, chance);
        }
    }
}
