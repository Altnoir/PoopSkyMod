package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.item.p.GashaponItem;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.creative.PoCreativeTabSection;
import com.altnoir.poopsky.impl.creative.PoSectionedCreativeModeTab;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Objects;

public class PoItemGroups {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final String POOPSKY_TAB_KEY = "itemgroup.poopsky";
    public static final String POOPSKY_DECO_TAB_KEY = "itemgroup.poopsky_deco";

    public static final PoCreativeTabSection TS_ITEMS = section("itemGroup.poopsky.section.items");
    public static final PoCreativeTabSection TS_BLOCKS = section("itemGroup.poopsky.section.blocks");
    public static final PoCreativeTabSection TS_MOBS = section("itemGroup.poopsky.section.mobs");
    public static final PoCreativeTabSection TS_POTIONS = section("itemGroup.poopsky.section.potions");

    public static final PoCreativeTabSection TS_DECO_MATERIALS = section("itemGroup.poopsky_deco.section.materials");
    public static final PoCreativeTabSection TS_DECO_TILES = section("itemGroup.poopsky_deco.section.tiles");
    public static final PoCreativeTabSection TS_DECO_TOILETS = section("itemGroup.poopsky_deco.section.toilets");

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_TAB = REGISTRATE.generic("poopsky",
            Registries.CREATIVE_MODE_TAB, () ->
                    PoSectionedCreativeModeTab.configure(
                            CreativeModeTab.builder()
                                    .title(Component.translatable(POOPSKY_TAB_KEY))
                                    .icon(PoBlocks.FLUSH_TOILET::asStack),
                            PoItemGroups::populateBasicSections,
                            TS_ITEMS,
                            TS_BLOCKS,
                            TS_MOBS,
                            TS_POTIONS
                    ).build()
    ).register();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_DECORATIVE = REGISTRATE.generic("poopsky_deco",
            Registries.CREATIVE_MODE_TAB, () ->
                    PoSectionedCreativeModeTab.configure(
                            CreativeModeTab.builder()
                                    .title(Component.translatable(POOPSKY_DECO_TAB_KEY))
                                    .icon(PoBlocks.BROWN_TILE_BLOCK::asStack),
                            parameters -> populateDecorativeSections(),
                            TS_DECO_MATERIALS,
                            TS_DECO_TILES,
                            TS_DECO_TOILETS
                    ).build()
    ).register();

    private static void populateBasicSections(CreativeModeTab.ItemDisplayParameters parameters) {
        for (Item item : PoItems.getAllItems()) {
            addBasicItem(item);
        }

        parameters.holders()
                .lookup(Registries.ENTITY_TYPE)
                .flatMap(registry -> registry.get(PoTags.EntityTypes.GASHAPON_MOB))
                .ifPresent(holders -> holders.forEach(holder -> TS_MOBS.add(() ->
                        GashaponItem.withColorAndMob(GashaponItem.PINK, Objects.requireNonNull(holder.getKey()).location().toString())
                )));
        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            if (isFlyVisible(id)) {
                TS_MOBS.add(() -> FlyItem.withType(id));
            }
        }

        parameters.holders()
                .lookup(Registries.POTION)
                .ifPresent(potions -> List.of(Items.TIPPED_ARROW, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)
                        .forEach(item -> addPotions(potions, item, parameters.enabledFeatures())));
    }

    private static void addBasicItem(Item item) {
        if (item == Items.AIR || item instanceof FlyItem || PoBlocks.isNoTabItem(item)) {
            return;
        }

        switch (item) {
            case SpawnEggItem ignored -> {
                TS_MOBS.add(item);
                return;
            }
            case GashaponItem ignored -> {
                TS_ITEMS.add(item);
                return;
            }
            case BlockItem ignored when PoBlocks.isBasicBlockItem(item) -> {
                TS_BLOCKS.add(item);
                return;
            }
            default -> {
            }
        }

        if (!PoBlocks.isDecoMaterialItem(item) && !PoBlocks.isDecoTileItem(item)) {
            TS_ITEMS.add(item);
        }
    }

    private static void populateDecorativeSections() {
        for (Item item : PoItems.getAllItems()) {
            if (!(item instanceof BlockItem)) {
                continue;
            }

            if (PoBlocks.isDecoMaterialItem(item)) {
                TS_DECO_MATERIALS.add(item);
            }

            if (PoBlocks.isDecoTileItem(item)) {
                TS_DECO_TILES.add(item);
            }
        }

        addToiletTypes(ToiletType.Category.WOOD, PoBlocks.WOODEN_TOILET.asItem());
        addToiletTypes(ToiletType.Category.HARD, PoBlocks.HARD_TOILET.asItem());
    }

    private static void addToiletTypes(ToiletType.Category category, Item toiletItem) {
        for (var type : ToiletType.getByCategory(category).values()) {
            TS_DECO_TOILETS.add(() -> ToiletBlockItem.withType(Block.byItem(toiletItem), type));
        }
    }

    private static PoCreativeTabSection section(String translationKey) {
        return new PoCreativeTabSection(translationKey);
    }

    public static List<String> translationKeys() {
        return List.of(
                POOPSKY_TAB_KEY,
                POOPSKY_DECO_TAB_KEY,
                TS_ITEMS.translationKey(),
                TS_BLOCKS.translationKey(),
                TS_MOBS.translationKey(),
                TS_POTIONS.translationKey(),
                TS_DECO_MATERIALS.translationKey(),
                TS_DECO_TILES.translationKey(),
                TS_DECO_TOILETS.translationKey()
        );
    }

    private static boolean isFlyVisible(String id) {
        if (FlyTypes.ZINC.id().equals(id)) {
            return PoMods.CREATE.isLoaded();
        }
        if (FlyTypes.CERTUS.id().equals(id) || FlyTypes.SKY_DUST.id().equals(id)) {
            return PoMods.AE2.isLoaded();
        }
        if (FlyTypes.OSMIUM.id().equals(id) || FlyTypes.TIN.id().equals(id) || FlyTypes.LEAD.id().equals(id)
                || FlyTypes.URANIUM.id().equals(id) || FlyTypes.FLUORITE.id().equals(id)) {
            return PoMods.MEKANISM.isLoaded();
        }
        return true;
    }

    private static void addPotions(HolderLookup<Potion> potions, Item item, FeatureFlagSet requiredFeatures) {
        potions.listElements()
                .filter(reference -> reference.value().isEnabled(requiredFeatures))
                .filter(reference -> reference.key().identifier().getNamespace().equals(PoopSky.MOD_ID))
                .map(reference -> PotionContents.createItemStack(item, reference))
                .forEach(TS_POTIONS::add);
    }

    public static void register() {
    }
}
