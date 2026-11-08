package com.altnoir.poopsky.content.item;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class PToolTiers {
    public static final Tier MILOS = new ForgeTier(4, 4088, 4F, 10F, 30, BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(PoItems.OMINOUS_FILTHY_INGOT));
}
