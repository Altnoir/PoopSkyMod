package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.init.PoItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.SpawnEggItem;

public class ItemModelGen extends FabricModelProvider {
    public ItemModelGen(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generateAll(generators);
    }

    public static void generateAll(ItemModelGenerators generators) {
        for (Item item : PoItems.getAllItems()) {
            if (item instanceof BlockItem || item instanceof SpawnEggItem || item == PoItems.TOILET_PLUG.get()) continue;

            if (item instanceof ArmorItem armorItem) {
                generators.generateArmorTrims(armorItem);
            } else if (item instanceof DiggerItem || item instanceof SwordItem) {
                generators.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
            } else {
                generators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            }
        }
    }
}
