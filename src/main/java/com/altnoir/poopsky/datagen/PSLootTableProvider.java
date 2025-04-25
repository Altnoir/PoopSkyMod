package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PSLootTableProvider extends FabricBlockLootTableProvider {
    public PSLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ToiletBlocks.OAK_TOILET);
        addDrop(ToiletBlocks.SPRUCE_TOILET);
        addDrop(ToiletBlocks.BIRCH_TOILET);
        addDrop(ToiletBlocks.JUNGLE_TOILET);
        addDrop(ToiletBlocks.ACACIA_TOILET);
        addDrop(ToiletBlocks.CHERRY_TOILET);
        addDrop(ToiletBlocks.DARK_OAK_TOILET);
        addDrop(ToiletBlocks.MANGROVE_TOILET);
        addDrop(ToiletBlocks.BAMBOO_TOILET);
        addDrop(ToiletBlocks.WHITE_CONCRETE_TOILET);
        addDrop(ToiletBlocks.ORANGE_CONCRETE_TOILET);
        addDrop(ToiletBlocks.MAGENTA_CONCRETE_TOILET);
        addDrop(ToiletBlocks.LIGHT_BLUE_CONCRETE_TOILET);
        addDrop(ToiletBlocks.YELLOW_CONCRETE_TOILET);
        addDrop(ToiletBlocks.LIME_CONCRETE_TOILET);
        addDrop(ToiletBlocks.PINK_CONCRETE_TOILET);
        addDrop(ToiletBlocks.GRAY_CONCRETE_TOILET);
        addDrop(ToiletBlocks.LIGHT_GRAY_CONCRETE_TOILET);
        addDrop(ToiletBlocks.CYAN_CONCRETE_TOILET);
        addDrop(ToiletBlocks.PURPLE_CONCRETE_TOILET);
        addDrop(ToiletBlocks.BLUE_CONCRETE_TOILET);
        addDrop(ToiletBlocks.BROWN_CONCRETE_TOILET);
        addDrop(ToiletBlocks.GREEN_CONCRETE_TOILET);
        addDrop(ToiletBlocks.RED_CONCRETE_TOILET);
        addDrop(ToiletBlocks.BLACK_CONCRETE_TOILET);
        addDrop(ToiletBlocks.RAINBOW_TOILET);

        addDrop(PSBlocks.POOP_BLOCK);
        addDrop(PSBlocks.POOP_STAIRS);
        addDrop(PSBlocks.POOP_SLAB, slabDrops(PSBlocks.POOP_SLAB));
        addDrop(PSBlocks.POOP_VERTICAL_SLAB);
        addDrop(PSBlocks.POOP_BUTTON);
        addDrop(PSBlocks.POOP_PRESSURE_PLATE);
        addDrop(PSBlocks.POOP_FENCE);
        addDrop(PSBlocks.POOP_FENCE_GATE);
        addDrop(PSBlocks.POOP_WALL);
        addDrop(PSBlocks.POOP_DOOR, doorDrops(PSBlocks.POOP_DOOR));
        addDrop(PSBlocks.POOP_TRAPDOOR);

        addDrop(PSBlocks.POOP_LOG);
        addDrop(PSBlocks.STRIPPED_POOP_LOG);
        addDrop(PSBlocks.POOP_SAPLING);
        addDrop(PSBlocks.POOP_LEAVES, leavesDrops(PSBlocks.POOP_LEAVES, PSBlocks.POOP_SAPLING, 0.1F));
    }
}
