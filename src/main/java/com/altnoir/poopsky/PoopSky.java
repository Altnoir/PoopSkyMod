package com.altnoir.poopsky;

import com.altnoir.poopsky.Fluid.PSFluids;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.effect.PSEffect;
import com.altnoir.poopsky.entity.PSBlockEntities;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticle;
import com.altnoir.poopsky.potion.PSPotions;
import com.altnoir.poopsky.sound.PSSounds;
import com.altnoir.poopsky.worldgen.PSWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.ItemTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoopSky implements ModInitializer {
	public static final String MOD_ID = "poopsky";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PSItemGroups.registerItemGroups();

		PSFluids.registerModFluids();
		PSBlocks.registerModBlocks();
		PSItems.registerModItems();

		PSSounds.registerSounds();
		PSParticle.registerParticles();

		PSEffect.registerEffects();
		PSPotions.registerPotions();

		PSComponents.registerComponents();
		PSBlockEntities.registerBlockEntities();

		PSWorldGeneration.genenatePSWorldGen();

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


		LOGGER.info("Hello Fabric world!");
	}
}