package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.block.p.CompooperBlock;
import com.altnoir.poopsky.block.p.PoopPieceBlock;
import com.altnoir.poopsky.block.p.RoundwormVinesPlantBlock;
import com.altnoir.poopsky.block.p.UrineCompooperBlock;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PSBlockLootTableProvider extends BlockLootSubProvider {
    protected PSBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        ToiletBlocks.BLOCKS.getEntries().stream()
                .map(DeferredHolder::get)
                .forEach(this::dropSelf);

        this.add(PSBlocks.POOP_LOG.get(), this::createSpallOreDrops);
        this.add(PSBlocks.STRIPPED_POOP_LOG.get(), this::createSpallOreDrops);
        this.add(PSBlocks.POOP_LEAVES_IRON.get(), this::createIronLeavesDrops);
        this.add(PSBlocks.POOP_LEAVES_GOLD.get(), this::createGoldLeavesDrops);
        this.add(PSBlocks.POOP_LEAVES.get(), this::createLeavesDrops);
        dropSelf(PSBlocks.POOP_SAPLING.get());
        dropSelf(PSBlocks.POOP_EMPTY_LOG.get());
        dropSelf(PSBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        dropSelf(PSBlocks.POOP_BLOCK.get());
        dropSelf(PSBlocks.POOP_STAIRS.get());
        add(PSBlocks.POOP_SLAB.get(), block -> createSlabItemTable(PSBlocks.POOP_SLAB.get()));
        dropSelf(PSBlocks.POOP_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.POOP_BUTTON.get());
        dropSelf(PSBlocks.POOP_PRESSURE_PLATE.get());
        dropSelf(PSBlocks.POOP_FENCE.get());
        dropSelf(PSBlocks.POOP_FENCE_GATE.get());
        dropSelf(PSBlocks.POOP_WALL.get());
        dropSelf(PSBlocks.POOP_TRAPDOOR.get());
        add(PSBlocks.POOP_DOOR.get(), block -> createDoorTable(PSBlocks.POOP_DOOR.get()));

        dropSelf(PSBlocks.CHILI_POOP_BLOCK.get());
        dropSelf(PSBlocks.CHILI_POOP_STAIRS.get());
        add(PSBlocks.CHILI_POOP_SLAB.get(), block -> createSlabItemTable(PSBlocks.CHILI_POOP_SLAB.get()));
        dropSelf(PSBlocks.CHILI_POOP_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.CHILI_POOP_WALL.get());

        dropSelf(PSBlocks.GOLDEN_POOP_BLOCK.get());
        dropSelf(PSBlocks.GOLDEN_POOP_STAIRS.get());
        add(PSBlocks.GOLDEN_POOP_SLAB.get(), block -> createSlabItemTable(PSBlocks.GOLDEN_POOP_SLAB.get()));
        dropSelf(PSBlocks.GOLDEN_POOP_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.GOLDEN_POOP_WALL.get());

        dropSelf(PSBlocks.POOP_BRICKS.get());
        dropSelf(PSBlocks.CRACKED_POOP_BRICKS.get());
        dropSelf(PSBlocks.POOP_BRICK_STAIRS.get());
        add(PSBlocks.POOP_BRICK_SLAB.get(), block -> createSlabItemTable(PSBlocks.POOP_BRICK_SLAB.get()));
        dropSelf(PSBlocks.POOP_BRICK_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.POOP_BRICK_WALL.get());

        dropSelf(PSBlocks.MOSSY_POOP_BRICKS.get());
        dropSelf(PSBlocks.MOSSY_POOP_BRICK_STAIRS.get());
        add(PSBlocks.MOSSY_POOP_BRICK_SLAB.get(), block -> createSlabItemTable(PSBlocks.MOSSY_POOP_BRICK_SLAB.get()));
        dropSelf(PSBlocks.MOSSY_POOP_BRICK_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.MOSSY_POOP_BRICK_WALL.get());

        dropSelf(PSBlocks.DRIED_POOP_BLOCK.get());
        dropSelf(PSBlocks.DRIED_POOP_BLOCK_STAIRS.get());
        add(PSBlocks.DRIED_POOP_BLOCK_SLAB.get(), block -> createSlabItemTable(PSBlocks.DRIED_POOP_BLOCK_SLAB.get()));
        dropSelf(PSBlocks.DRIED_POOP_BLOCK_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.DRIED_POOP_BLOCK_WALL.get());

        dropSelf(PSBlocks.SMOOTH_POOP_BLOCK.get());
        dropSelf(PSBlocks.SMOOTH_POOP_BLOCK_STAIRS.get());
        add(PSBlocks.SMOOTH_POOP_BLOCK_SLAB.get(), block -> createSlabItemTable(PSBlocks.SMOOTH_POOP_BLOCK_SLAB.get()));
        dropSelf(PSBlocks.SMOOTH_POOP_BLOCK_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.SMOOTH_POOP_BLOCK_WALL.get());

        dropSelf(PSBlocks.CUT_POOP_BLOCK.get());
        dropSelf(PSBlocks.CUT_POOP_BLOCK_STAIRS.get());
        add(PSBlocks.CUT_POOP_BLOCK_SLAB.get(), block -> createSlabItemTable(PSBlocks.CUT_POOP_BLOCK_SLAB.get()));
        dropSelf(PSBlocks.CUT_POOP_BLOCK_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.CUT_POOP_BLOCK_WALL.get());

        dropSelf(PSBlocks.TILE_BLOCK.get());
        dropSelf(PSBlocks.TILE_BLOCK_STAIRS.get());
        add(PSBlocks.TILE_BLOCK_SLAB.get(), block -> createSlabItemTable(PSBlocks.TILE_BLOCK_SLAB.get()));
        dropSelf(PSBlocks.TILE_BLOCK_VERTICAL_SLAB.get());
        dropSelf(PSBlocks.TILE_BLOCK_WALL.get());

        dropSelf(PSBlocks.RAW_POOP_BLOCK.get());
        dropSelf(PSBlocks.RAW_SAPING_POOP_BLOCK.get());
        dropSelf(PSBlocks.RAW_WITHER_POOP_BLOCK.get());

        dropSelf(PSBlocks.POOP_CAKE.get());
        PSBlocks.getPoopCandleCakes().forEach((candle, candleCake) ->
                this.add(candleCake.get(), createCandleCakeDrops(candle)));
        dropSelf(PSBlocks.POOLIME_BLOCK.get());
        dropSelf(PSBlocks.POOLIME_POOP_BLOCK.get());
        dropSelf(PSBlocks.STOOL.get());
        this.add(PSBlocks.COMPOOPER.get(), this::createCompoomerDrops);
        dropOther(PSBlocks.WATER_COMPOOPER.get(), PSBlocks.COMPOOPER.get());
        dropOther(PSBlocks.LAVA_COMPOOPER.get(), PSBlocks.COMPOOPER.get());
        dropOther(PSBlocks.POWER_SNOW_COMPOOPER.get(), PSBlocks.COMPOOPER.get());
        this.add(PSBlocks.URINE_COMPOOPER.get(), this::createUrineCompoomerDrops);
        dropSelf(PSBlocks.SIEVE.get());
        add(PSBlocks.POOP_PIECE.get(), createPoopPieceDrop(PSBlocks.POOP_PIECE.get(), PSItems.POOP_BALL.get()));

        LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(PSBlocks.MAGGOTS.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, CropBlock.MAX_AGE));
        add(PSBlocks.MAGGOTS.get(), maggotsCropDrops(PSBlocks.MAGGOTS.get(), PSItems.MAGGOTS_SEEDS.get(), builder));

        LootItemCondition.Builder builder2 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(PSBlocks.ROUNDWORM_VINES_PLANT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RoundwormVinesPlantBlock.SEEDS, true));
        add(PSBlocks.ROUNDWORM_VINES_PLANT.get(), createRoundwormVinesDrop(PSItems.ROUNDWORM.get(), builder2));
        dropOther(PSBlocks.ROUNDWORM_VINES.get(), PSItems.ROUNDWORM.get());
    }

    protected LootTable.@NotNull Builder createPoopPieceDrop(Block block, Item item) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        LootPoolEntryContainer.Builder<?> nonSilkTouch = AlternativesEntry.alternatives(
                IntStream.rangeClosed(1, 8)
                        .mapToObj(i -> LootItem.lootTableItem(item)
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PoopPieceBlock.LAYERS, i)))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)))
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                        ).toArray(LootPoolEntryContainer.Builder[]::new)
        ).when(hasSilkTouch().invert());

        LootPoolEntryContainer.Builder<?> silkTouch = AlternativesEntry.alternatives(
                IntStream.rangeClosed(1, 8)
                        .mapToObj(i -> {
                            if (i == 8) {
                                return LootItem.lootTableItem(PSBlocks.POOP_BLOCK.get());
                            }
                            return LootItem.lootTableItem(block)
                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PoopPieceBlock.LAYERS, i)))
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)));
                        }).toArray(LootPoolEntryContainer.Builder[]::new)
        ).when(hasSilkTouch());

        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(AlternativesEntry.alternatives(nonSilkTouch, silkTouch))
                );
    }

    protected LootTable.Builder maggotsCropDrops(Block crop, Item seeds) {
        HolderLookup.RegistryLookup<Enchantment> impl = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(crop,
                this.applyExplosionDecay(crop, LootItem.lootTableItem(seeds)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(ApplyBonusCount.addUniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE))))
        );
    }

    protected LootTable.Builder maggotsCropDrops(Block cropBlock, Item seedsItem, LootItemCondition.Builder dropGrownCropCondition) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.applyExplosionDecay(cropBlock,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.WHEAT_SEEDS).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                                .add(LootItem.lootTableItem(Items.SWEET_BERRIES).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                                .add(LootItem.lootTableItem(Items.CARROT).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                                .add(LootItem.lootTableItem(Items.POTATO).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                        )
                        .withPool(LootPool.lootPool()
                                .when(dropGrownCropCondition)
                                .add(
                                        LootItem.lootTableItem(seedsItem)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                                )
                        )
        );
    }

    protected LootTable.Builder createRoundwormVinesDrop(Item seedsItem, LootItemCondition.Builder dropGrownCropCondition) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                                .add(LootItem.lootTableItem(Items.MELON_SEEDS).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                                .add(LootItem.lootTableItem(Items.FROGSPAWN).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                                .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).when(dropGrownCropCondition).otherwise(LootItem.lootTableItem(seedsItem)))
                )
                .withPool(LootPool.lootPool()
                        .when(dropGrownCropCondition)
                        .add(
                                LootItem.lootTableItem(seedsItem)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3))
                        )
                );
    }

    protected LootTable.Builder createSpallOreDrops(Block block) {
        var registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block,
                        LootItem.lootTableItem(PSItems.SPALL)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    protected LootTable.Builder createIronLeavesDrops(Block block) {
        var registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block,
                        LootItem.lootTableItem(Items.IRON_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    protected LootTable.Builder createGoldLeavesDrops(Block block) {
        var registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block,
                        LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    protected LootTable.Builder createLeavesDrops(Block block) {
        var registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block,
                this.applyExplosionDecay(block,
                        LootItem.lootTableItem(PSItems.POOP.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        ).withPool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(this.doesNotHaveShearsOrSilkTouch())
                        .add(((LootPoolSingletonContainer.Builder<?>)
                                this.applyExplosionCondition(block, LootItem.lootTableItem(PSItems.ROUNDWORM.get())))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))
        );
    }

    protected LootTable.Builder createCompoomerDrops(Block block) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(this.applyExplosionDecay(block, LootItem.lootTableItem(PSBlocks.COMPOOPER.get())))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(PSItems.SAPING_POOP_BALL.get()))
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(CompooperBlock.POOP_LEVEL, CompooperBlock.READY))
                                )
                );
    }

    protected LootTable.Builder createUrineCompoomerDrops(Block block) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(this.applyExplosionDecay(block, LootItem.lootTableItem(PSBlocks.COMPOOPER.get())))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(PSItems.MAGGOTS_SEEDS.get()))
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(UrineCompooperBlock.MAGGOTS, true))
                                )
                );
    }

    private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
        return this.hasShearsOrSilkTouch().invert();
    }

    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return HAS_SHEARS.or(this.hasSilkTouch());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return Stream.concat(
                        PSBlocks.BLOCKS.getEntries().stream(),
                        ToiletBlocks.BLOCKS.getEntries().stream()
                )
                .map(Holder::value)
                .collect(Collectors.toList());
    }
}