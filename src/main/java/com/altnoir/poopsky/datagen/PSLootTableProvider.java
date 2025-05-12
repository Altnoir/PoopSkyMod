package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
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

        addDrop(PSBlocks.POOP_BRICKS);
        addDrop(PSBlocks.CRACKED_POOP_BRICKS);
        addDrop(PSBlocks.POOP_BRICK_STAIRS);
        addDrop(PSBlocks.POOP_BRICK_SLAB, slabDrops(PSBlocks.POOP_BRICK_SLAB));
        addDrop(PSBlocks.POOP_BRICK_VERTICAL_SLAB);
        addDrop(PSBlocks.POOP_BRICK_WALL);
        addDrop(PSBlocks.MOSSY_POOP_BRICKS);
        addDrop(PSBlocks.MOSSY_POOP_BRICK_STAIRS);
        addDrop(PSBlocks.MOSSY_POOP_BRICK_SLAB, slabDrops(PSBlocks.MOSSY_POOP_BRICK_SLAB));
        addDrop(PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB);
        addDrop(PSBlocks.MOSSY_POOP_BRICK_WALL);
        addDrop(PSBlocks.DRIED_POOP_BLOCK);
        addDrop(PSBlocks.DRIED_POOP_BLOCK_STAIRS);
        addDrop(PSBlocks.DRIED_POOP_BLOCK_SLAB, slabDrops(PSBlocks.DRIED_POOP_BLOCK_SLAB));
        addDrop(PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB);
        addDrop(PSBlocks.DRIED_POOP_BLOCK_WALL);
        addDrop(PSBlocks.SMOOTH_POOP_BLOCK);
        addDrop(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS);
        addDrop(PSBlocks.SMOOTH_POOP_BLOCK_SLAB, slabDrops(PSBlocks.SMOOTH_POOP_BLOCK_SLAB));
        addDrop(PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB);
        addDrop(PSBlocks.SMOOTH_POOP_BLOCK_WALL);
        addDrop(PSBlocks.CUT_POOP_BLOCK);
        addDrop(PSBlocks.CUT_POOP_BLOCK_STAIRS);
        addDrop(PSBlocks.CUT_POOP_BLOCK_SLAB, slabDrops(PSBlocks.CUT_POOP_BLOCK_SLAB));
        addDrop(PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB);
        addDrop(PSBlocks.CUT_POOP_BLOCK_WALL);

        addDrop(PSBlocks.POOP_LOG, spallOreDrops(PSBlocks.POOP_LOG));
        addDrop(PSBlocks.POOP_EMPTY_LOG);
        addDrop(PSBlocks.STRIPPED_POOP_LOG, spallOreDrops(PSBlocks.POOP_LOG));
        addDrop(PSBlocks.STRIPPED_POOP_EMPTY_LOG);
        addDrop(PSBlocks.POOP_SAPLING);
        addDrop(PSBlocks.POOP_LEAVES, poopLeavesDrops(PSBlocks.POOP_LEAVES, PSBlocks.POOP_SAPLING, 0.1F));
        addDrop(PSBlocks.STOOL);

//        BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition.builder(PSBlocks.MAGGOTS)
//                .properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, CropBlock.MAX_AGE));
//        this.addDrop(PSBlocks.MAGGOTS, this.maggotsCropDrops(PSBlocks.MAGGOTS, PSItems.MAGGOTS_SEEDS, builder));
    }
    public LootTable.Builder maggotsCropDrops(Block crop, Item seeds, LootCondition.Builder condition) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.applyExplosionDecay(
                crop,
                LootTable.builder()
                        .pool(LootPool.builder()
                                .with(ItemEntry.builder(Items.WHEAT_SEEDS).conditionally(condition).alternatively(ItemEntry.builder(seeds)))
                                .with(ItemEntry.builder(Items.COCOA_BEANS).conditionally(condition).alternatively(ItemEntry.builder(seeds)))
                                .with(ItemEntry.builder(Items.PUMPKIN_SEEDS).conditionally(condition).alternatively(ItemEntry.builder(seeds)))
                                .with(ItemEntry.builder(Items.MELON_SEEDS).conditionally(condition).alternatively(ItemEntry.builder(seeds)))
                                .with(ItemEntry.builder(Items.BEETROOT_SEEDS).conditionally(condition).alternatively(ItemEntry.builder(seeds)))
                                .with(ItemEntry.builder(Items.CARROT).conditionally(condition).alternatively(ItemEntry.builder(seeds)))
                                .with(ItemEntry.builder(Items.POTATO).conditionally(condition).alternatively(ItemEntry.builder(seeds)))
                        )
                        .pool(
                                LootPool.builder()
                                        .conditionally(condition)
                                        .with(ItemEntry.builder(seeds).apply(ApplyBonusLootFunction.binomialWithBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))
                        )
        );
    }
    public LootTable.Builder spallOreDrops(Block drop) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(
                drop,
                (LootPoolEntry.Builder<?>)this.applyExplosionDecay(
                        drop,
                        ItemEntry.builder(PSItems.SPALL)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F)))
                                .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }
    public LootTable.Builder poopLeavesDrops(Block leaves, Block sapling, float... saplingChance) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        return this.dropsWithSilkTouchOrShears(
                leaves,
                ((LeafEntry.Builder<?>)this.addSurvivesExplosionCondition(
                        leaves,
                        ItemEntry.builder(sapling)))
                                .conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE), saplingChance))
                ).pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .conditionally(this.createWithoutShearsOrSilkTouchCondition())
                        .with(this.applyExplosionDecay(leaves, ItemEntry.builder(Items.IRON_NUGGET)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F))))
                                .conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE), 0.077F, 0.21F, 0.42F, 0.69F, 1.0F))
                        )
                ).pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .conditionally(this.createWithoutShearsOrSilkTouchCondition())
                        .with(this.applyExplosionDecay(leaves, ItemEntry.builder(PSItems.POOP)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))))
                );
    }
}
