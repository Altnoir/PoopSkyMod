package com.altnoir.poopsky.content.item;

import com.altnoir.poopsky.fabric.port.util.SimpleTier;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
//import net.neoforged.neoforge.common.SimpleTier;

public class PToolTiers {
    public static final Tier MILOS = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            4088, 4F, 10F, 30, () -> Ingredient.of(PoItems.OMINOUS_FILTHY_INGOT));
}
