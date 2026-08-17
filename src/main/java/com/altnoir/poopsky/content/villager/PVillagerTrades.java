package com.altnoir.poopsky.content.villager;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.ToiletTypes;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;
import java.util.Optional;

public class PVillagerTrades {
    public static void registerTrades(VillagerProfession type, Int2ObjectMap<List<VillagerTrades.ItemListing>> trades) {
        if (type == PoVillagers.POOP_MAKER.value()) {
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(PoItems.POOP.get(), 4), 44, 3, 0.05f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(PoItems.DRAGON_BREATH_CHILI.get(), 1), 10, 5, 0.15f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(Items.GLASS_BOTTLE, 3), 20, 3, 0.05f)
            );

            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoBlocks.POOLIME_BLOCK, 1),
                    new ItemStack(Items.EMERALD, 5), 44, 10, 0.1f)
            );
            if (!Config.upgradeTemplate) {
                trades.get(2).add((entity, random) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, 24),
                        new ItemStack(PoItems.OMEN_UPGRADE_SMITHING_TEMPLATE.get(), 1), 1, 40, 0.3f)
                );
            }

            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(PoBlocks.STOOL, 1), 16, 15, 0.1f)
            );
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.URINE_BOTTLE, 1),
                    potion(Potions.WATER), 3, 15, 0.1f)
            );
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(PoItems.POOP_VEGETABLE_STICKS.get(), 2), 32, 15, 0.1f)
            );

            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 18),
                    new ItemStack(PoBlocks.POOP_CAKE, 1), 4, 25, 0.25f)
            );
            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 20),
                    new ItemStack(PoItems.URINE_BUCKET.get(), 1), 4, 25, 0.25f)
            );
            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3),
                    new ItemStack(PoItems.POOP_PASTA.get(), 1), 32, 10, 0.25f)
            );

            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(PoBlocks.BROWN_TILE_BLOCK, 4), 128, 10, 0.25f)
            );
            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    ToiletBlockItem.withType(PoBlocks.HARD_TOILET.get(), ToiletTypes.RAINBOW), 8, 25, 0.5f)
            );
            if (!Config.plugTrades) {
                trades.get(5).add((entity, random) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, 42),
                        Optional.of(new ItemCost(Items.BREEZE_ROD, 6)),
                        new ItemStack(PoItems.TOILET_PLUG.get(), 1), 2, 100, 1.0f)
                );
            }
        }

        if (type == PoVillagers.GASTRONOME.value()) {
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOP.get(), 4),
                    new ItemStack(Items.EMERALD, 1), 88, 5, 0.1f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoBlocks.POOP_BLOCK, 2),
                    new ItemStack(Items.EMERALD, 3), 88, 5, 0.1f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoBlocks.MAGGOTS_BLOCK, 8),
                    new ItemStack(Items.EMERALD, 8), 88, 5, 0.1f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.URINE_BOTTLE, 1),
                    new ItemStack(Items.EMERALD, 2), 88, 5, 0.1f)
            );

            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOP_DUMPLINGS.get(), 1),
                    new ItemStack(Items.EMERALD, 2), 64, 10, 0.2f)
            );
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOP_BREAD.get(), 1),
                    new ItemStack(Items.EMERALD, 2), 64, 10, 0.2f)
            );
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOP_SOUP.get(), 1),
                    new ItemStack(Items.EMERALD, 3), 64, 10, 0.2f)
            );

            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOP_VEGETABLE_STICKS.get(), 1),
                    new ItemStack(Items.EMERALD, 3), 64, 10, 0.25f)
            );
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOP_PASTA.get(), 1),
                    new ItemStack(Items.EMERALD, 3), 64, 10, 0.25f)
            );
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POODDING.get(), 1),
                    new ItemStack(Items.EMERALD, 3), 64, 10, 0.25f)
            );

            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOBURGER_MEAT.get(), 1),
                    new ItemStack(Items.EMERALD, 6), 64, 15, 0.5f)
            );
            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PoItems.POOBURGER.get(), 1),
                    new ItemStack(Items.EMERALD, 9), 32, 20, 0.5f)
            );

            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(PoItems.LAWRENCE_MUSIC_DISC.get(), 1), 2, 15, 1.0f)
            );
            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(PoItems.LIGHT_DANCE_MUSIC_DISC.get(), 1), 2, 30, 1.0f)
            );
            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(PoItems.MOON_BOWL_MUSIC_DISC.get(), 1), 2, 30, 1.0f)
            );
            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(PoItems.THEME_MUSIC_DISC.get(), 1), 2, 30, 1.0f)
            );
        }
    }

    private static ItemStack potion(Holder<Potion> potion) {
        return PotionContents.createItemStack(Items.POTION, potion);
    }

    private static ItemCost potionCost(Holder<Potion> potion) {
        return new ItemCost(Items.POTION).withComponents(p_330063_ -> p_330063_.expect(DataComponents.POTION_CONTENTS, new PotionContents(potion)));
    }
}