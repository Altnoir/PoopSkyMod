package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.effect.PSPotions;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.villager.PSVillagers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = PoopSky.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class PSGameEvents {
    @SubscribeEvent
    public static void onBrewingRecipeRegistry(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, Items.ROTTEN_FLESH, PSPotions.FECAL_INCONTINENCE_POTION);
        builder.addMix(PSPotions.FECAL_INCONTINENCE_POTION, Items.REDSTONE, PSPotions.LONG_FECAL_INCONTINENCE_POTION);
        builder.addMix(PSPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE_DUST, PSPotions.STRONG_FECAL_INCONTINENCE_POTION);
    }

    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        if (event.getType() == PSVillagers.POOP_MAKER.value()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(PSItems.POOP.get(), 4), 44, 3, 0.05f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PSBlocks.POOP_BLOCK, 2),
                    new ItemStack(Items.EMERALD, 1), 88, 5, 0.1f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 16),
                    new ItemStack(PSItems.DRAGON_BREATH_CHILI.get(), 1), 10, 5, 0.15f)
            );
            trades.get(1).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(Items.GLASS_BOTTLE, 3), 20, 3, 0.05f)
            );


            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(PSItems.POOP_SOUP.get(), 1), 16, 10, 0.07f)
            );
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PSBlocks.STRIPPED_POOP_EMPTY_LOG, 1),
                    new ItemStack(Items.EMERALD, 5), 44, 10, 0.1f)
            );
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PSItems.POOP_BREAD.get(), 2),
                    new ItemStack(Items.RAW_COPPER, 1), 18, 10, 0.1f)
            );
            trades.get(2).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PSItems.POOP.get(), 88),
                    new ItemStack(PSItems.LAWRENCE_MUSIC_DISC.get(), 1), 2, 15, 0.1f)
            );


            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PSItems.POOP.get(), 88),
                    new ItemStack(PSItems.LIGHT_DANCE_MUSIC_DISC.get(), 1), 2, 30, 0.1f)
            );
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PSItems.POOP.get(), 88),
                    new ItemStack(PSItems.MOON_BOWL_MUSIC_DISC.get(), 1), 2, 30, 0.1f)
            );
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(PSBlocks.STOOL, 1), 16, 15, 0.1f)
            );
            trades.get(3).add((entity, random) -> new MerchantOffer(
                    new ItemCost(PSItems.URINE_BOTTLE, 1),
                    potion(Potions.WATER), 3, 15, 0.1f)
            );


            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 12),
                    new ItemStack(ToiletBlocks.RAINBOW_TOILET, 1), 8, 25, 0.25f)
            );
            trades.get(4).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 18),
                    new ItemStack(PSBlocks.POOP_CAKE, 1), 1, 25, 0.25f)
            );


            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    new ItemStack(PSItems.TOILET_LINKER.get(), 1), 4, 50, 0.5f)
            );
            trades.get(5).add((entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 64),
                    Optional.of(new ItemCost(Items.BREEZE_ROD, 8)),
                    new ItemStack(PSItems.TOILET_PLUG.get(), 1), 1, 100, 1.0f)
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
