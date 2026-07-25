package com.altnoir.poopsky;

import com.altnoir.poopsky.compat.PoMods;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.impl.creative.PoCreativeTabSection;
import com.altnoir.poopsky.impl.creative.PoSectionedCreativeModeTab;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class PoItemGroups {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final ResourceKey<CreativeModeTab> POOPSKY_TAB_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, PoopSky.loc("poopsky"));
    public static final ResourceKey<CreativeModeTab> POOPSKY_DECORATIVE_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, PoopSky.loc("poopsky_deco"));

    // 基础分区
    public static final PoCreativeTabSection BASIC_ITEMS = section(
            POOPSKY_TAB_KEY, "basic/items", "itemGroup.poopsky.section.basic"
    );
    public static final PoCreativeTabSection BASIC_BLOCKS = section(
            POOPSKY_TAB_KEY, "basic/blocks", "itemGroup.poopsky.section.basic_blocks"
    );
    public static final PoCreativeTabSection BASIC_TOILETS = section(
            POOPSKY_TAB_KEY, "basic/toilets", "itemGroup.poopsky.section.basic_toilets"
    );
    public static final PoCreativeTabSection BASIC_FLY = section(
            POOPSKY_TAB_KEY, "basic/fly", "itemGroup.poopsky.section.basic_fly"
    );

    // 装饰分区
    public static final PoCreativeTabSection DECO_POOP = section(
            POOPSKY_DECORATIVE_KEY, "deco/poop", "itemGroup.poopsky_deco.section.poop"
    );
    public static final PoCreativeTabSection DECO_TILE = section(
            POOPSKY_DECORATIVE_KEY, "deco/tile", "itemGroup.poopsky_deco.section.tile"
    );
    public static final PoCreativeTabSection DECO_WOOD = section(
            POOPSKY_DECORATIVE_KEY, "deco/wood", "itemGroup.poopsky_deco.section.wood"
    );
    public static final PoCreativeTabSection DECO_TOILET = section(
            POOPSKY_DECORATIVE_KEY, "deco/toilet", "itemGroup.poopsky_deco.section.toilet"
    );

    // 创意标签页注册（延迟填充）
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_TAB = REGISTRATE.object("poopsky")
            .defaultCreativeTab(tab -> PoSectionedCreativeModeTab.configure(
                    tab.icon(PoBlocks.WOODEN_TOILET::asStack),
                    PoItemGroups::populateBasicSections,
                    BASIC_ITEMS, BASIC_BLOCKS, BASIC_TOILETS, BASIC_FLY
            ))
            .register();

    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> POOPSKY_DECORATIVE = REGISTRATE.object("poopsky_deco")
            .defaultCreativeTab(tab -> PoSectionedCreativeModeTab.configure(
                    tab.icon(PoBlocks.WHITE_TILE_BLOCK::asStack),
                    PoItemGroups::populateDecorativeSections,
                    DECO_POOP, DECO_TILE, DECO_WOOD, DECO_TOILET
            ))
            .register();

    public static void register() {
    }

    private static void populateBasicSections() {
        PoItems.getAllItems().stream()
                .filter(item -> !PoBlocks.isDecorativeItem(item))
                .filter(item -> !(item instanceof BlockItem))
                .filter(item -> !(item instanceof FlyItem))
                .forEach(item -> BASIC_ITEMS.add(getItemId(item)));

        Set<Block> skip = Set.of(PoBlocks.URINE_LIQUID.get(), PoBlocks.WATER_COMPOOPER.get(), PoBlocks.LAVA_COMPOOPER.get(),
                PoBlocks.POWDER_SNOW_COMPOOPER.get(), PoBlocks.URINE_COMPOOPER.get(), PoBlocks.ROUNDWORM_VINES_PLANT.get(),
                PoBlocks.WOODEN_TOILET.get(), PoBlocks.HARD_TOILET.get());
        PoItems.getAllItems().stream()
                .filter(item -> item instanceof BlockItem)
                .filter(item -> !skip.contains(((BlockItem) item).getBlock()))
                .filter(item -> !PoBlocks.isDecorativeItem(item))
                .forEach(item -> BASIC_BLOCKS.add(getItemId(item)));

        BASIC_TOILETS.add(getItemId(PoBlocks.WOODEN_TOILET.asItem()));
        BASIC_TOILETS.add(getItemId(PoBlocks.HARD_TOILET.asItem()));

        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            if (createFly(id) && !PoMods.CREATE.isLoaded()) continue;
            if (ae2Fly(id) && !PoMods.AE2.isLoaded()) continue;
            if (mekFly(id) && !PoMods.MEKANISM.isLoaded()) continue;
            BASIC_FLY.add(PoopSky.loc("fly_" + id));
        }
    }

    private static void populateDecorativeSections() {
        PoItems.getAllItems().stream()
                .filter(item -> PoBlocks.isDecorativeItem(item) || PoBlocks.isAllTabItem(item))
                .filter(item -> item instanceof BlockItem blockItem && isPoopBlock(blockItem.getBlock()))
                .forEach(item -> DECO_POOP.add(getItemId(item)));

        PoItems.getAllItems().stream()
                .filter(item -> PoBlocks.isDecorativeItem(item) || PoBlocks.isAllTabItem(item))
                .filter(item -> item instanceof BlockItem blockItem && isTileBlock(blockItem.getBlock()))
                .forEach(item -> DECO_TILE.add(getItemId(item)));

        PoItems.getAllItems().stream()
                .filter(item -> PoBlocks.isDecorativeItem(item) || PoBlocks.isAllTabItem(item))
                .filter(item -> item instanceof BlockItem blockItem && isWoodBlock(blockItem.getBlock()))
                .forEach(item -> DECO_WOOD.add(getItemId(item)));

        for (var type : ToiletType.getByCategory(ToiletType.Category.WOOD).values()) {
            DECO_TOILET.add(PoopSky.loc("wooden_toilet_" + type.id()));
        }
        for (var type : ToiletType.getByCategory(ToiletType.Category.HARD).values()) {
            DECO_TOILET.add(PoopSky.loc("hard_toilet_" + type.id()));
        }
    }

    private static boolean isPoopBlock(Block block) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
        return name.contains("poop");
    }

    private static boolean isTileBlock(Block block) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
        return name.contains("tile");
    }

    private static boolean isWoodBlock(Block block) {
        String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
        return name.contains("wood") || name.contains("log") || name.contains("plank");
    }

    private static ResourceLocation getItemId(Item item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
    }

    private static PoCreativeTabSection section(
            ResourceKey<CreativeModeTab> tab,
            String id,
            String translationKey
    ) {
        return new PoCreativeTabSection(tab, PoopSky.loc(id), Component.translatable(translationKey));
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
}