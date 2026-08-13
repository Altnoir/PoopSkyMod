package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ConfigValueCondition;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.ToiletTypes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.*;

public final class VillagerTradeGen {
    private static final Map<String, List<String>> TRADE_SETS = new LinkedHashMap<>();

    private VillagerTradeGen() {
    }

    public static void bootstrapTrades(BootstrapContext<VillagerTrade> context) {
        TRADE_SETS.clear();
        add(context, "poopmaker", 1, "emerald_poop", Items.EMERALD, 1, PoItems.POOP, 4, 44, 3, 0.05F);
        add(context, "poopmaker", 1, "emerald_dragon_breath_chili", Items.EMERALD, 8, PoItems.DRAGON_BREATH_CHILI, 1, 10, 5, 0.15F);
        add(context, "poopmaker", 1, "emerald_glass_bottle", Items.EMERALD, 1, Items.GLASS_BOTTLE, 3, 20, 3, 0.05F);

        add(context, "poopmaker", 2, "poolime_block_emerald", PoBlocks.POOLIME_BLOCK, 1, Items.EMERALD, 5, 44, 10, 0.1F);
        add(context, "poopmaker", 2, "emerald_omen_upgrade_smithing_template", Items.EMERALD, 24,
                PoItems.OMEN_UPGRADE_SMITHING_TEMPLATE, 1, 1, 40, 0.3F, configDisabled("upgrade_template"));

        add(context, "poopmaker", 3, "emerald_stool", Items.EMERALD, 8, PoBlocks.STOOL, 1, 16, 15, 0.1F);
        add(context, "poopmaker", 3, "urine_bottle_water_potion", new TradeCost(PoItems.URINE_BOTTLE, 1),
                new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                        .set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER))
                        .build()), 3, 15, 0.1F, null);
        add(context, "poopmaker", 3, "emerald_poop_vegetable_sticks", Items.EMERALD, 8,
                PoItems.POOP_VEGETABLE_STICKS, 2, 32, 15, 0.1F);

        add(context, "poopmaker", 4, "emerald_poop_cake", Items.EMERALD, 18, PoBlocks.POOP_CAKE, 1, 4, 25, 0.25F);
        add(context, "poopmaker", 4, "emerald_urine_bucket", Items.EMERALD, 20, PoItems.URINE_BUCKET, 1, 4, 25, 0.25F);
        add(context, "poopmaker", 4, "emerald_poop_pasta", Items.EMERALD, 3, PoItems.POOP_PASTA, 1, 32, 10, 0.25F);

        add(context, "poopmaker", 5, "emerald_brown_tile_block", Items.EMERALD, 1, PoBlocks.BROWN_TILE_BLOCK, 4, 128, 10, 0.25F);
        add(context, "poopmaker", 5, "emerald_rainbow_toilet", new TradeCost(Items.EMERALD, 12),
                new ItemStackTemplate(PoBlocks.HARD_TOILET.asItem(), DataComponentPatch.builder()
                        .set(PoComponents.TOILET_TYPE.get(), ToiletTypes.RAINBOW)
                        .build()), 8, 25, 0.5F, null);
        add(context, "poopmaker", 5, "emerald_and_breeze_rod_toilet_plug",
                new TradeCost(Items.EMERALD, 42), new TradeCost(Items.BREEZE_ROD, 6),
                new ItemStackTemplate(PoItems.TOILET_PLUG.asItem()), 2, 100, 1.0F, configDisabled("plug_trades"));

        add(context, "gastronome", 1, "poop_emerald", PoItems.POOP, 4, Items.EMERALD, 1, 88, 5, 0.1F);
        add(context, "gastronome", 1, "poop_block_emerald", PoBlocks.POOP_BLOCK, 2, Items.EMERALD, 3, 88, 5, 0.1F);
        add(context, "gastronome", 1, "maggots_block_emerald", PoBlocks.MAGGOTS_BLOCK, 8, Items.EMERALD, 8, 88, 5, 0.1F);
        add(context, "gastronome", 1, "urine_bottle_emerald", PoItems.URINE_BOTTLE, 1, Items.EMERALD, 2, 88, 5, 0.1F);

        add(context, "gastronome", 2, "poop_dumplings_emerald", PoItems.POOP_DUMPLINGS, 1, Items.EMERALD, 2, 64, 10, 0.2F);
        add(context, "gastronome", 2, "poop_bread_emerald", PoItems.POOP_BREAD, 1, Items.EMERALD, 2, 64, 10, 0.2F);
        add(context, "gastronome", 2, "poop_soup_emerald", PoItems.POOP_SOUP, 1, Items.EMERALD, 3, 64, 10, 0.2F);

        add(context, "gastronome", 3, "poop_vegetable_sticks_emerald", PoItems.POOP_VEGETABLE_STICKS, 1, Items.EMERALD, 3, 64, 10, 0.25F);
        add(context, "gastronome", 3, "poop_pasta_emerald", PoItems.POOP_PASTA, 1, Items.EMERALD, 3, 64, 10, 0.25F);
        add(context, "gastronome", 3, "poodding_emerald", PoItems.POODDING, 1, Items.EMERALD, 3, 64, 10, 0.25F);

        add(context, "gastronome", 4, "pooburger_meat_emerald", PoItems.POOBURGER_MEAT, 1, Items.EMERALD, 6, 64, 15, 0.5F);
        add(context, "gastronome", 4, "pooburger_emerald", PoItems.POOBURGER, 1, Items.EMERALD, 9, 32, 20, 0.5F);

        add(context, "gastronome", 5, "emerald_lawrence_music_disc", Items.EMERALD, 8, PoItems.LAWRENCE_MUSIC_DISC, 1, 2, 15, 1.0F);
        add(context, "gastronome", 5, "emerald_light_dance_music_disc", Items.EMERALD, 8, PoItems.LIGHT_DANCE_MUSIC_DISC, 1, 2, 30, 1.0F);
        add(context, "gastronome", 5, "emerald_moon_bowl_music_disc", Items.EMERALD, 8, PoItems.MOON_BOWL_MUSIC_DISC, 1, 2, 30, 1.0F);
    }

    public static void bootstrapTradeSets(BootstrapContext<TradeSet> context) {
        HolderGetter<VillagerTrade> trades = context.lookup(Registries.VILLAGER_TRADE);
        TRADE_SETS.forEach((setPath, tradePaths) -> context.register(
                key(Registries.TRADE_SET, setPath),
                new TradeSet(
                        HolderSet.direct(tradePaths.stream().map(path -> trades.getOrThrow(key(Registries.VILLAGER_TRADE, path))).toList()),
                        ConstantValue.exactly(2.0F),
                        false,
                        Optional.of(PoopSky.loc("trade_set/" + setPath))
                )
        ));
    }

    private static void add(BootstrapContext<VillagerTrade> context, String profession, int level, String name,
                            ItemLike wanted, int wantedCount, ItemLike given, int givenCount,
                            int maxUses, int xp, float reputationDiscount) {
        add(context, profession, level, name, wanted, wantedCount, given, givenCount,
                maxUses, xp, reputationDiscount, null);
    }

    private static void add(BootstrapContext<VillagerTrade> context, String profession, int level, String name,
                            ItemLike wanted, int wantedCount, ItemLike given, int givenCount,
                            int maxUses, int xp, float reputationDiscount, LootItemCondition condition) {
        add(context, profession, level, name, new TradeCost(wanted, wantedCount),
                new ItemStackTemplate(given.asItem(), givenCount), maxUses, xp, reputationDiscount, condition);
    }

    private static void add(BootstrapContext<VillagerTrade> context, String profession, int level, String name,
                            TradeCost wanted, ItemStackTemplate given, int maxUses, int xp,
                            float reputationDiscount, LootItemCondition condition) {
        add(context, profession, level, name, wanted, null, given,
                maxUses, xp, reputationDiscount, condition);
    }

    private static void add(BootstrapContext<VillagerTrade> context, String profession, int level, String name,
                            TradeCost wanted, TradeCost additionalWanted, ItemStackTemplate given,
                            int maxUses, int xp, float reputationDiscount, LootItemCondition condition) {
        String tradePath = profession + "/" + level + "/" + name;
        context.register(key(Registries.VILLAGER_TRADE, tradePath),
                new VillagerTrade(wanted, Optional.ofNullable(additionalWanted), given, maxUses, xp, reputationDiscount,
                        Optional.ofNullable(condition), List.of()));
        TRADE_SETS.computeIfAbsent(profession + "/level_" + level, ignored -> new ArrayList<>()).add(tradePath);
    }

    private static LootItemCondition configDisabled(String key) {
        return new ConfigValueCondition(key, false);
    }

    private static <T> ResourceKey<T> key(ResourceKey<net.minecraft.core.Registry<T>> registry, String path) {
        return ResourceKey.create(registry, PoopSky.loc(path));
    }
}
