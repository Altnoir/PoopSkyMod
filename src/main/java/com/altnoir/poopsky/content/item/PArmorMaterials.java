package com.altnoir.poopsky.content.item;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class PArmorMaterials {
    public static final ArmorMaterial OMEN_ARMOR_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) {
            // Hotfix2 sets all four pieces to the helmet base durability (11 * 24).
            return 264;
        }
        @Override public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) { case BOOTS -> 2; case LEGGINGS -> 6; case CHESTPLATE -> 8; case HELMET -> 3; };
        }
        @Override public int getEnchantmentValue() { return 30; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_NETHERITE; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(PoItems.OMINOUS_FILTHY_INGOT.get()); }
        @Override public String getName() { return "poopsky:omen"; }
        @Override public float getToughness() { return 1.0F; }
        @Override public float getKnockbackResistance() { return 0.05F; }
    };
    private PArmorMaterials() {}
}
