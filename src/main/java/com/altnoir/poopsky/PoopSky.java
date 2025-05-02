package com.altnoir.poopsky;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.effect.PSEffect;
import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.keybinding.PlugActionPayload;
import com.altnoir.poopsky.keybinding.PoopBallPayload;
import com.altnoir.poopsky.particle.PSParticle;
import com.altnoir.poopsky.potion.PSPotions;
import com.altnoir.poopsky.sound.PSSoundEvents;
import com.altnoir.poopsky.villager.PSVillagers;
import com.altnoir.poopsky.worldgen.PSWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PoopSky implements ModInitializer {
	public static final String MOD_ID = "poopsky";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		PSBlocks.registerModBlocks();
		PSItems.registerModItems();

		PSSoundEvents.registerSounds();
		PSParticle.registerParticles();
		PSEffect.registerEffects();
		PSPotions.registerPotions();
		PSComponents.registerComponents();

		PSEntities.registerBlockEntities();
		PSEntities.registerEntities();
		PSVillagers.registerVillagers();

		PSItemGroups.registerItemGroups();
		PSWorldGeneration.generatePSWorldGen();

		PoopBallPayload.register();
		PlugActionPayload.register();
		PlugActionPayload.registerServerReceiver();

		FuelRegistry.INSTANCE.add(PSItems.POOP, 200);
		FuelRegistry.INSTANCE.add(PSItems.POOP_BALL, 400);
		FuelRegistry.INSTANCE.add(PSItems.KIITEOOKINI_MUSIC_DISC, 1600);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_SAPLING, 200);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_LEAVES, 200);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_BLOCK, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_STAIRS, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_SLAB, 400);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_VERTICAL_SLAB, 400);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_BUTTON, 200);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_PRESSURE_PLATE, 400);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_FENCE, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_FENCE_GATE, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_WALL, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_DOOR, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_TRAPDOOR, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_LOG, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.STRIPPED_POOP_LOG, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_PIECE, 400);
		FuelRegistry.INSTANCE.add(PSBlocks.STOOL, 800);

		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.OAK_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.SPRUCE_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.BIRCH_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.JUNGLE_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.ACACIA_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.CHERRY_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.DARK_OAK_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.MANGROVE_TOILET, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ToiletBlocks.BAMBOO_TOILET, 5, 20);

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.WATER, Items.ROTTEN_FLESH, PSPotions.FECAL_INCONTINENCE_POTION);
			builder.registerPotionRecipe(PSPotions.FECAL_INCONTINENCE_POTION, Items.REDSTONE, PSPotions.LONG_FECAL_INCONTINENCE_POTION);
			builder.registerPotionRecipe(PSPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE_DUST, PSPotions.STRONG_FECAL_INCONTINENCE_POTION);
		});

		TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 1, factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 1),
					new ItemStack(PSItems.POOP, 4),44, 3, 0.05f)
			);
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(PSBlocks.POOP_BLOCK, 2),
					new ItemStack(Items.EMERALD,1),88, 5, 0.1f)
			);
		});
		TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 2, factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 1),
					new ItemStack(PSBlocks.POOP_SAPLING, 1),16, 10, 0.07f)
			);
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(PSBlocks.STRIPPED_POOP_EMPTY_LOG, 1),
					new ItemStack(Items.EMERALD,3),44, 10, 0.1f)
			);
		});
		TradeOfferHelper.registerVillagerOffers(PSVillagers.POOP_MAKER, 3, factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 12),
					new ItemStack(PSItems.KIITEOOKINI_MUSIC_DISC, 1),4, 15, 0.1f)
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
					new TradedItem(Items.EMERALD, 16),
					new ItemStack(ToiletBlocks.RAINBOW_TOILET, 1),8, 25, 0.25f)
			);
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 21),
					new ItemStack(Items.LAVA_BUCKET, 1),1, 25, 0.25f)
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
	}
}