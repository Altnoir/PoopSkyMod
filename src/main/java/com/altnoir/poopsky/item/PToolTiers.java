package com.altnoir.poopsky.item;

import com.altnoir.poopsky.init.PItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class PToolTiers {
    public static final Tier MILOS = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            4088, 4F, 10F, 30, () -> Ingredient.of(PItems.OMINOUS_FILTHY_INGOT));
}
