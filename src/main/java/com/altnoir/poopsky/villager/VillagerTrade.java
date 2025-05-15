package com.altnoir.poopsky.villager;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;

import java.util.Optional;

public class VillagerTrade {
    public static void registerVillagerTrades() {
        TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 1),
                    new ItemStack(PSItems.POOP, 4),44, 3, 0.05f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(PSBlocks.POOP_BLOCK, 2),
                    new ItemStack(Items.EMERALD,1),88, 5, 0.1f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 8),
                    new ItemStack(PSItems.DRAGON_BREATH_CHILI,1),8, 5, 0.15f)
            );
        });

        TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 2, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 5),
                    new ItemStack(PSItems.POOP_SOUP, 1),16, 10, 0.07f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(PSBlocks.STRIPPED_POOP_EMPTY_LOG, 1),
                    new ItemStack(Items.EMERALD,3),44, 10, 0.1f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(PSItems.POOP_BREAD, 2),
                    new ItemStack(Items.RAW_COPPER,1),18, 10, 0.1f)
            );
        });

        TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 3, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 12),
                    new ItemStack(PSItems.LAWRENCE_MUSIC_DISC, 1),4, 15, 0.1f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 8),
                    new ItemStack(PSBlocks.STOOL, 1),16, 15, 0.1f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(PSItems.URINE_BOTTLE, 1),
                    PotionContentsComponent.createStack(Items.POTION, Potions.WATER),3, 15, 0.1f)
            );
        });

        TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 4, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 12),
                    new ItemStack(ToiletBlocks.RAINBOW_TOILET, 1),8, 25, 0.25f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 18),
                    new ItemStack(PSBlocks.POOP_CAKE, 1),1, 25, 0.25f)
            );
        });

        TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 5, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 37),
                    new ItemStack(PSItems.TOILET_LINKER, 1),4, 50, 0.5f)
            );
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 64),
                    Optional.of(new TradedItem(Items.BREEZE_ROD, 12)),
                    new ItemStack(PSItems.TOILET_PLUG, 1),1, 100, 1.0f)
            );
        });

        PoopSky.LOGGER.info("Registering Mod Villager Trades for " + PoopSky.MOD_ID);
    }
}
