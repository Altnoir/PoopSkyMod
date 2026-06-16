package com.altnoir.poopsky.item;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class PArmorMaterials{
    public static final Holder<ArmorMaterial> OMEN_ARMOR_MATERIAL = register("omen",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.BODY, 11);
            }),
            30, SoundEvents.ARMOR_EQUIP_NETHERITE, 1.0F, 0.05F, PItems.OMINOUS_FILTHY_INGOT.get()
    );

    private static Holder<ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Item repairIngredient
    ) {
        EnumMap<ArmorItem.Type, Integer> fullDefense = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            fullDefense.put(type, defense.getOrDefault(type, 0));
        }
        Supplier<Ingredient> ingredient = () -> Ingredient.of(repairIngredient);
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(PoopSky.loc(name)));

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, PoopSky.loc(name),
                new ArmorMaterial(fullDefense, enchantmentValue, equipSound, ingredient, layers, toughness, knockbackResistance)
        );
    }
}