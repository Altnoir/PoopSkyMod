package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class OmenSmithingTemplateItem extends SmithingTemplateItem {
    private static final ChatFormatting TITLE_FORMAT;
    private static final ChatFormatting DESCRIPTION_FORMAT;
    private static final Component INGREDIENTS_TITLE;
    private static final Component APPLIES_TO_TITLE;
    private static final Component OMEN_UPGRADE;
    private static final Component OMEN_UPGRADE_APPLIES_TO;
    private static final Component OMEN_UPGRADE_INGREDIENTS;
    private static final Component OMEN_UPGRADE_BASE_SLOT_DESCRIPTION;
    private static final Component OMEN_UPGRADE_ADDITIONS_SLOT_DESCRIPTION;
    private static final Identifier EMPTY_SLOT_HELMET;
    private static final Identifier EMPTY_SLOT_CHESTPLATE;
    private static final Identifier EMPTY_SLOT_LEGGINGS;
    private static final Identifier EMPTY_SLOT_BOOTS;
    private static final Identifier EMPTY_SLOT_HOE;
    private static final Identifier EMPTY_SLOT_AXE;
    private static final Identifier EMPTY_SLOT_SWORD;
    private static final Identifier EMPTY_SLOT_SHOVEL;
    private static final Identifier EMPTY_SLOT_PICKAXE;
    private static final Identifier EMPTY_SLOT_INGOT;
    private final Component appliesTo;
    private final Component ingredients;
    private final Component upgradeDescription;

    public OmenSmithingTemplateItem(Component appliesTo, Component ingredients, Component upgradeDescription, Component baseSlotDescription,
                                    Component additionsSlotDescription, List<Identifier> baseSlotEmptyIcons,
                                    List<Identifier> additionalSlotEmptyIcons, Item.Properties properties) {
        super(appliesTo, ingredients, baseSlotDescription, additionsSlotDescription, baseSlotEmptyIcons, additionalSlotEmptyIcons, properties);
        this.appliesTo = appliesTo;
        this.ingredients = ingredients;
        this.upgradeDescription = upgradeDescription;
    }

    public static SmithingTemplateItem createOmenUpgradeTemplate(Item.Properties properties) {
        return new OmenSmithingTemplateItem(OMEN_UPGRADE_APPLIES_TO, OMEN_UPGRADE_INGREDIENTS, OMEN_UPGRADE,
                OMEN_UPGRADE_BASE_SLOT_DESCRIPTION, OMEN_UPGRADE_ADDITIONS_SLOT_DESCRIPTION,
                createNetheriteUpgradeIconList(), createNetheriteUpgradeMaterialList(), properties);
    }

    private static List<Identifier> createNetheriteUpgradeIconList() {
        return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS);
    }

    private static List<Identifier> createNetheriteUpgradeMaterialList() {
        return List.of(EMPTY_SLOT_INGOT);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.accept(this.upgradeDescription);
        tooltip.accept(CommonComponents.EMPTY);
        tooltip.accept(APPLIES_TO_TITLE);
        tooltip.accept(CommonComponents.space().append(this.appliesTo));
        tooltip.accept(INGREDIENTS_TITLE);
        tooltip.accept(CommonComponents.space().append(this.ingredients));
    }

    static {
        TITLE_FORMAT = ChatFormatting.GRAY;
        DESCRIPTION_FORMAT = ChatFormatting.BLUE;
        INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", PoopSky.mcloc("smithing_template.ingredients"))).withStyle(TITLE_FORMAT);
        APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", PoopSky.mcloc("smithing_template.applies_to"))).withStyle(TITLE_FORMAT);
        OMEN_UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", PoopSky.loc("omen_upgrade"))).withStyle(TITLE_FORMAT);
        OMEN_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", PoopSky.loc("smithing_template.omen_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
        OMEN_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", PoopSky.loc("smithing_template.omen_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
        OMEN_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", PoopSky.loc("smithing_template.omen_upgrade.base_slot_description")));
        OMEN_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", PoopSky.loc("smithing_template.omen_upgrade.additions_slot_description")));
        EMPTY_SLOT_HELMET = PoopSky.mcloc("item/empty_armor_slot_helmet");
        EMPTY_SLOT_CHESTPLATE = PoopSky.mcloc("item/empty_armor_slot_chestplate");
        EMPTY_SLOT_LEGGINGS = PoopSky.mcloc("item/empty_armor_slot_leggings");
        EMPTY_SLOT_BOOTS = PoopSky.mcloc("item/empty_armor_slot_boots");
        EMPTY_SLOT_HOE = PoopSky.mcloc("item/empty_slot_hoe");
        EMPTY_SLOT_AXE = PoopSky.mcloc("item/empty_slot_axe");
        EMPTY_SLOT_SWORD = PoopSky.mcloc("item/empty_slot_sword");
        EMPTY_SLOT_SHOVEL = PoopSky.mcloc("item/empty_slot_shovel");
        EMPTY_SLOT_PICKAXE = PoopSky.mcloc("item/empty_slot_pickaxe");
        EMPTY_SLOT_INGOT = PoopSky.mcloc("item/empty_slot_ingot");
    }
}
