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
import com.altnoir.poopsky.init.ToiletTypes;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class PoItemGroups {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private static final PoCreativeTabSection BASIC_ITEMS = section("itemGroup.poopsky.section.basic");
    private static final PoCreativeTabSection BASIC_BLOCKS = section("itemGroup.poopsky.section.basic_blocks");
    private static final PoCreativeTabSection BASIC_TOILETS = section("itemGroup.poopsky.section.basic_toilets");
    private static final PoCreativeTabSection BASIC_FLIES = section("itemGroup.poopsky.section.basic_flies");

    private static final PoCreativeTabSection DECO_POOP = section("itemGroup.poopsky_deco.section.poop");
    private static final PoCreativeTabSection DECO_TILE = section("itemGroup.poopsky_deco.section.tile");
    private static final PoCreativeTabSection DECO_WOOD = section("itemGroup.poopsky_deco.section.wood");
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
                            BASIC_TOILETS,
                            BASIC_FLIES
                    ).build()
            ).register();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_DECORATIVE =
            REGISTRATE.generic("poopsky_deco", Registries.CREATIVE_MODE_TAB, () ->
                    PoSectionedCreativeModeTab.configure(
                            CreativeModeTab.builder()
                                    .title(Component.translatable("itemgroup.poopsky_deco"))
                                    .icon(PoBlocks.BROWN_TILE_BLOCK::asStack),
                            PoItemGroups::populateDecorativeSections,
                            DECO_POOP, DECO_TILE, DECO_WOOD, DECO_TOILETS
                    ).build()
            ).register();

    private static void populateBasicSections() {
        PoItems.getAllItems().stream()
                .filter(item -> !PoBlocks.isDecorativeItem(item))
                .filter(item -> !(item instanceof BlockItem))
                .filter(item -> !(item instanceof FlyItem))
                .forEach(BASIC_ITEMS::add);

        Set<Block> skip = Set.of(
                PoBlocks.URINE_LIQUID.get(),
                PoBlocks.WATER_COMPOOPER.get(),
                PoBlocks.LAVA_COMPOOPER.get(),
                PoBlocks.POWDER_SNOW_COMPOOPER.get(),
                PoBlocks.URINE_COMPOOPER.get(),
                PoBlocks.ROUNDWORM_VINES_PLANT.get(),
                PoBlocks.WOODEN_TOILET.get(),
                PoBlocks.HARD_TOILET.get()
        );
        PoItems.getAllItems().stream()
                .filter(item -> item instanceof BlockItem)
                .filter(item -> item != Items.AIR)
                .filter(item -> !skip.contains(((BlockItem) item).getBlock()))
                .filter(item -> !PoBlocks.isDecorativeItem(item))
                .forEach(BASIC_BLOCKS::add);

        BASIC_TOILETS.add(() -> ToiletBlockItem.withType(PoBlocks.WOODEN_TOILET.get(), ToiletTypes.OAK));
        BASIC_TOILETS.add(() -> ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), ToiletTypes.WHITE_TILE));
        BASIC_TOILETS.add(() -> ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), ToiletTypes.GOLD));

        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            if (createFly(id) && !PoMods.CREATE.isLoaded()) {
                continue;
            }
            if (ae2Fly(id) && !PoMods.AE2.isLoaded()) {
                continue;
            }
            if (mekFly(id) && !PoMods.MEKANISM.isLoaded()) {
                continue;
            }
            BASIC_FLIES.add(() -> FlyItem.withType(id));
        }
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
        } else if (name.startsWith("ginkgo_") || name.contains("ginkgo_")) {
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

    private static boolean createFly(String id) {
        return FlyTypes.ZINC.id().equals(id);
    }

    private static boolean ae2Fly(String id) {
        return FlyTypes.CERTUS.id().equals(id) || FlyTypes.SKY_DUST.id().equals(id);
    }

    private static boolean mekFly(String id) {
        return FlyTypes.OSMIUM.id().equals(id) || FlyTypes.TIN.id().equals(id) || FlyTypes.LEAD.id().equals(id) || FlyTypes.URANIUM.id().equals(id) || FlyTypes.FLUORITE.id().equals(id);
    }

    public static void register() {
    }
}
