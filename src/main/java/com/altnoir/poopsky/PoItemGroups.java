package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
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
import java.util.Set;

public class PoItemGroups {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private static final PoCreativeTabSection BASIC_ITEMS = section("itemGroup.poopsky.section.basic");
    private static final PoCreativeTabSection BASIC_BLOCKS = section("itemGroup.poopsky.section.basic_blocks");
    private static final PoCreativeTabSection BASIC_FLIES = section("itemGroup.poopsky.section.basic_flies");

    private static final PoCreativeTabSection DECO_POOP = section("itemGroup.poopsky_deco.section.poop");
    private static final PoCreativeTabSection DECO_WOOD = section("itemGroup.poopsky_deco.section.wood");
    private static final PoCreativeTabSection DECO_TILE = section("itemGroup.poopsky_deco.section.tile");
    private static final PoCreativeTabSection DECO_TOILETS = section("itemGroup.poopsky_deco.section.toilets");

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_TAB =
            REGISTRATE.generic("poopsky", Registries.CREATIVE_MODE_TAB, () ->
                    PoSectionedCreativeModeTab.configure(
                            CreativeModeTab.builder()
                                    .title(Component.translatable("itemgroup.poopsky"))
                                    .icon(PoBlocks.WOODEN_TOILET::asStack),
                            PoItemGroups::populateBasicSections,
                            BASIC_ITEMS,
                            BASIC_BLOCKS,
                            BASIC_FLIES
                    ).build()
            ).register();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_DECORATIVE =
            REGISTRATE.generic("poopsky_deco", Registries.CREATIVE_MODE_TAB, () ->
                    PoSectionedCreativeModeTab.configure(
                            CreativeModeTab.builder()
                                    .title(Component.translatable("itemgroup.poopsky_deco"))
                                    .icon(PoBlocks.BROWN_TILE_BLOCK::asStack),
                            parameters -> populateDecorativeSections(),
                            DECO_POOP,
                            DECO_WOOD,
                            DECO_TILE,
                            DECO_TOILETS
                    ).build()
            ).register();

    private static void populateBasicSections(CreativeModeTab.ItemDisplayParameters parameters) {
        Set<Block> skippedBlocks = Set.of(
                PoBlocks.URINE_LIQUID.get(),
                PoBlocks.WATER_COMPOOPER.get(),
                PoBlocks.LAVA_COMPOOPER.get(),
                PoBlocks.POWDER_SNOW_COMPOOPER.get(),
                PoBlocks.URINE_COMPOOPER.get(),
                PoBlocks.ROUNDWORM_VINES_PLANT.get(),
                PoBlocks.WOODEN_TOILET.get(),
                PoBlocks.HARD_TOILET.get(),
                PoBlocks.FLUSH_TOILET.get(),
                PoBlocks.GOLDEN_FLUSH_TOILET.get()
        );
        for (Item item : PoItems.getAllItems()) {
            if (item == Items.AIR || item instanceof FlyItem || PoBlocks.isDecorativeItem(item)) {
                continue;
            }
            if (item instanceof BlockItem blockItem && !(item instanceof ItemNameBlockItem)) {
                if (!skippedBlocks.contains(blockItem.getBlock())) {
                    BASIC_BLOCKS.add(item);
                }
            } else {
                BASIC_ITEMS.add(item);
            }
        }

        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            if (isFlyVisible(id)) {
                BASIC_FLIES.add(() -> FlyItem.withType(id));
            }
        }

        parameters.holders()
                .lookup(Registries.POTION)
                .ifPresent(potions -> List.of(Items.TIPPED_ARROW, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)
                        .forEach(item -> addPotions(potions, item, parameters.enabledFeatures())));
    }

    private static void populateDecorativeSections() {
        PoItems.getAllItems().stream()
                .filter(item -> PoBlocks.isDecorativeItem(item) || PoBlocks.isAllTabItem(item))
                .forEach(PoItemGroups::addDecorativeItem);

        for (var type : ToiletType.getByCategory(ToiletType.Category.WOOD).values()) {
            DECO_TOILETS.add(() -> ToiletBlockItem.withType(PoBlocks.WOODEN_TOILET.get(), type));
        }
        for (var type : ToiletType.getByCategory(ToiletType.Category.HARD).values()) {
            DECO_TOILETS.add(() -> ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), type));
        }
    }

    private static void addDecorativeItem(Item item) {
        String name = item.builtInRegistryHolder().key().location().getPath();
        if (name.contains("tile")) {
            DECO_TILE.add(item);
        } else if (name.contains("ginkgo_")) {
            DECO_WOOD.add(item);
        } else if (name.endsWith("flush_toilet")) {
            DECO_TOILETS.add(item);
        } else {
            DECO_POOP.add(item);
        }
    }

    private static PoCreativeTabSection section(String translationKey) {
        return new PoCreativeTabSection(Component.translatable(translationKey));
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
                .filter(reference -> reference.key().location().getNamespace().equals(PoopSky.MOD_ID))
                .map(reference -> PotionContents.createItemStack(item, reference))
                .forEach(BASIC_ITEMS::add);
    }

    public static void register() {
    }
}
