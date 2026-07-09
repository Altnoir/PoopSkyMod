package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.item.PArmorMaterials;
import com.altnoir.poopsky.init.PEffects;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class OmenArmorItem extends ArmorItem {
    private static final int BOOTS_SLOT = 0;
    private static final int LEGGINGS_SLOT = 1;
    private static final int CHESTPLATE_SLOT = 2;
    private static final int HELMET_SLOT = 3;
    private static final Map<Holder<ArmorMaterial>, List<MobEffectInstance>> ARMOR_EFFECT =
            (new ImmutableMap.Builder<Holder<ArmorMaterial>, List<MobEffectInstance>>())
                    .put(PArmorMaterials.OMEN_ARMOR_MATERIAL, List.of(
                                    new MobEffectInstance(PEffects.OMENER, 160, 0, false, false),
                                    new MobEffectInstance(MobEffects.HUNGER, 160, 1, false, false)
                            )
                    ).build();

    public OmenArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && !level.isClientSide() && hasFullSuitOfArmorOn(player)) {
            evaluateArmorEffects(player);
        }
    }

    private void evaluateArmorEffects(Player player) {
        for (Map.Entry<Holder<ArmorMaterial>, List<MobEffectInstance>> entry : ARMOR_EFFECT.entrySet()) {
            Holder<ArmorMaterial> mapArmorMaterial = entry.getKey();
            List<MobEffectInstance> mapEffect = entry.getValue();

            if (hasPlayerCorrectArmorOn(mapArmorMaterial, player)) {
                addEffectToPlayer(player, mapEffect);
            }
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

    private boolean hasPlayerCorrectArmorOn(Holder<ArmorMaterial> mapArmorMaterial, Player player) {
        for (ItemStack armorStack : player.getArmorSlots()) {
            if (!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }
        var inventory = player.getInventory();
        ArmorItem boots = ((ArmorItem) inventory.getArmor(BOOTS_SLOT).getItem());
        ArmorItem leggings = ((ArmorItem) inventory.getArmor(LEGGINGS_SLOT).getItem());
        ArmorItem chestplate = ((ArmorItem) inventory.getArmor(CHESTPLATE_SLOT).getItem());
        ArmorItem helmet = ((ArmorItem) inventory.getArmor(HELMET_SLOT).getItem());

        return boots.getMaterial() == mapArmorMaterial && leggings.getMaterial() == mapArmorMaterial
                && chestplate.getMaterial() == mapArmorMaterial && helmet.getMaterial() == mapArmorMaterial;
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        var inventory = player.getInventory();
        ItemStack boots = inventory.getArmor(BOOTS_SLOT);
        ItemStack leggings = inventory.getArmor(LEGGINGS_SLOT);
        ItemStack chestplate = inventory.getArmor(CHESTPLATE_SLOT);
        ItemStack helmet = inventory.getArmor(HELMET_SLOT);

        return !boots.isEmpty() && !leggings.isEmpty() && !chestplate.isEmpty() && !helmet.isEmpty();
    }
}