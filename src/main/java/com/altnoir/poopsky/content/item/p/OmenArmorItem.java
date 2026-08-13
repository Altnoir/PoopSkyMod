package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class OmenArmorItem extends Item {
    private static final List<MobEffectInstance> ARMOR_EFFECTS = List.of(
            new MobEffectInstance(PoEffects.OMENER, 160, 0, false, false),
            new MobEffectInstance(MobEffects.HUNGER, 160, 1, false, false));

    public OmenArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(properties.humanoidArmor(material, type));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player && hasFullSuitOfArmorOn(player)) {
            evaluateArmorEffects(player);
        }
    }

    private void evaluateArmorEffects(Player player) {
        if (hasPlayerCorrectArmorOn(player)) {
            addEffectToPlayer(player, ARMOR_EFFECTS);
        }
    }

    private void addEffectToPlayer(Player player, List<MobEffectInstance> mapEffect) {
        boolean hasPlayerEffect = mapEffect.stream().allMatch(effect -> player.hasEffect(effect.getEffect()));

        if (!hasPlayerEffect) {
            for (MobEffectInstance effect : mapEffect) {
                player.addEffect(new MobEffectInstance(effect.getEffect(),
                        effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
            }
        }
    }

    private static boolean hasPlayerCorrectArmorOn(Player player) {
        return player.getItemBySlot(EquipmentSlot.FEET).is(PoItems.OMEN_BOOTS.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(PoItems.OMEN_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(PoItems.OMEN_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.HEAD).is(PoItems.OMEN_HELMET.get());
    }

    private static boolean hasFullSuitOfArmorOn(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        return !boots.isEmpty() && !leggings.isEmpty() && !chestplate.isEmpty() && !helmet.isEmpty();
    }
}
