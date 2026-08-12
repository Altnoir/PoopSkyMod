package com.altnoir.poopsky.content.item;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

public final class PArmorMaterials {
    public static final ResourceKey<EquipmentAsset> OMEN_ARMOR_ASSET = ResourceKey.create(
            EquipmentAssets.ROOT_ID,
            PoopSky.loc("omen"));
    public static final ArmorMaterial OMEN_ARMOR_MATERIAL = new ArmorMaterial(
            24,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 2);
                map.put(ArmorType.LEGGINGS, 6);
                map.put(ArmorType.CHESTPLATE, 8);
                map.put(ArmorType.HELMET, 3);
                map.put(ArmorType.BODY, 11);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            1.0F,
            0.05F,
            PoTags.Items.REPAIRS_OMEN_ARMOR,
            OMEN_ARMOR_ASSET
    );

    private PArmorMaterials() {
    }
}
