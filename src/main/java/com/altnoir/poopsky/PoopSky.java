package com.altnoir.poopsky;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.effect.PSEffect;
import com.altnoir.poopsky.entity.PSEntities;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.keybinding.PlugActionPayload;
import com.altnoir.poopsky.particle.PSParticle;
import com.altnoir.poopsky.potion.PSPotions;
import com.altnoir.poopsky.sound.PSSoundEvents;
import com.altnoir.poopsky.villager.PSVillagers;
import com.altnoir.poopsky.villager.VillagerTrade;
import com.altnoir.poopsky.worldgen.foliage.PSFoliagePlacerType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoopSky implements ModInitializer {
	public static final String MOD_ID = "poopsky";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier POOP_STAT = Identifier.of(MOD_ID, "poop_stat");
	@Override
	public void onInitialize() {
		PSBlocks.registerModBlocks();
		ToiletBlocks.registerToiletBlocks();
		PSItems.registerModItems();

		PSComponents.registerComponents();
		PSSoundEvents.registerSounds();
		PSParticle.registerParticles();
		PSEffect.registerEffects();
		PSPotions.registerPotions();

		PSItemGroups.registerItemGroups();

		PSEntities.registerEntities();
		PSVillagers.registerVillagers();
		PSFoliagePlacerType.registerFoliagePlacer();
		PlugActionPayload.registerServerReceiver();

		Registry.register(Registries.CUSTOM_STAT, POOP_STAT, POOP_STAT);
		Stats.CUSTOM.getOrCreateStat(POOP_STAT, StatFormatter.DEFAULT);

		FuelRegistry.INSTANCE.add(PSItems.POOP, 200);
		FuelRegistry.INSTANCE.add(PSItems.POOP_BALL, 400);
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
		FuelRegistry.INSTANCE.add(PSBlocks.POOP_EMPTY_LOG, 800);
		FuelRegistry.INSTANCE.add(PSBlocks.STRIPPED_POOP_EMPTY_LOG, 800);
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

		//CompostingChanceRegistry.INSTANCE.add(PSItems.MAGGOTS_SEEDS, 0.3f);
		StrippableBlockRegistry.register(PSBlocks.POOP_LOG,  PSBlocks.STRIPPED_POOP_LOG);
		StrippableBlockRegistry.register(PSBlocks.POOP_EMPTY_LOG,  PSBlocks.STRIPPED_POOP_EMPTY_LOG);

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.WATER, Items.ROTTEN_FLESH, PSPotions.FECAL_INCONTINENCE_POTION);
			builder.registerPotionRecipe(PSPotions.FECAL_INCONTINENCE_POTION, Items.REDSTONE, PSPotions.LONG_FECAL_INCONTINENCE_POTION);
			builder.registerPotionRecipe(PSPotions.FECAL_INCONTINENCE_POTION, Items.GLOWSTONE_DUST, PSPotions.STRONG_FECAL_INCONTINENCE_POTION);
		});

		VillagerTrade.registerVillagerTrades();
	}
}