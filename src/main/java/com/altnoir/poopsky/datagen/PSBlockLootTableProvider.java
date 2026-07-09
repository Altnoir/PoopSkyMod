package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.common.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.common.block.p.CompooperBlock;
import com.altnoir.poopsky.common.block.p.PoopPieceBlock;
import com.altnoir.poopsky.common.block.p.RoundwormVinesPlantBlock;
import com.altnoir.poopsky.common.block.p.UrineCompooperBlock;
import com.altnoir.poopsky.common.SetToiletTypeFunction;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PSBlockLootTableProvider extends BlockLootSubProvider {
    protected PSBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(PBlocks.WOODEN_TOILET.get(), this::dropToilet);
        this.add(PBlocks.HARD_TOILET.get(), this::dropToilet);

        this.add(PBlocks.POOP_LOG.get(), this::createSpallOreDrops);
        this.add(PBlocks.STRIPPED_POOP_LOG.get(), this::createSpallOreDrops);
        this.add(PBlocks.POOP_LEAVES_IRON.get(), this::createIronLeavesDrops);
        this.add(PBlocks.POOP_LEAVES_GOLD.get(), this::createGoldLeavesDrops);
        this.add(PBlocks.POOP_LEAVES.get(), this::createLeavesDrops);
        dropSelf(PBlocks.POOP_SAPLING.get());
        dropSelf(PBlocks.POOP_EMPTY_LOG.get());
        dropSelf(PBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        dropBlockFamily(PBlocks.POOP_FAMILY);
        dropSelf(PBlocks.POOP_BUTTON.get());
        dropSelf(PBlocks.POOP_PRESSURE_PLATE.get());
        dropSelf(PBlocks.POOP_FENCE.get());
        dropSelf(PBlocks.POOP_FENCE_GATE.get());
        dropSelf(PBlocks.POOP_TRAPDOOR.get());
        add(PBlocks.POOP_DOOR.get(), block -> createDoorTable(PBlocks.POOP_DOOR.get()));

        dropBlockFamily(PBlocks.CHILI_POOP_FAMILY);
        dropBlockFamily(PBlocks.GOLDEN_POOP_FAMILY);

        dropBlockFamily(PBlocks.POOP_BRICK_FAMILY);
        dropSelf(PBlocks.CRACKED_POOP_BRICKS.get());
        dropBlockFamily(PBlocks.MOSSY_POOP_BRICK_FAMILY);
        dropBlockFamily(PBlocks.DRIED_POOP_BLOCK_FAMILY);
        dropBlockFamily(PBlocks.SMOOTH_POOP_BLOCK_FAMILY);
        dropBlockFamily(PBlocks.CUT_POOP_BLOCK_FAMILY);
        dropBlockFamily(PBlocks.TILE_BLOCK_FAMILY);

        dropSelf(PBlocks.RAW_POOP_BLOCK.get());
        dropSelf(PBlocks.RAW_SAPLING_POOP_BLOCK.get());
        dropSelf(PBlocks.RAW_SEA_POOP_BLOCK.get());
        dropSelf(PBlocks.RAW_WITHER_POOP_BLOCK.get());

        add(PBlocks.POOP_CAKE.get(), noDrop());
        PBlocks.getPoopCandleCakes().forEach((candle, candleCake) ->
                this.add(candleCake.get(), createCandleCakeDrops(candle)));
        dropSelf(PBlocks.POOLIME_BLOCK.get());
        dropSelf(PBlocks.POOLIME_MAGGOTS_BLOCK.get());
        dropSelf(PBlocks.STOOL.get());
        this.add(PBlocks.COMPOOPER.get(), this::createCompoomerDrops);
        dropOther(PBlocks.WATER_COMPOOPER.get(), PBlocks.COMPOOPER.get());
        dropOther(PBlocks.LAVA_COMPOOPER.get(), PBlocks.COMPOOPER.get());
        dropOther(PBlocks.POWDER_SNOW_COMPOOPER.get(), PBlocks.COMPOOPER.get());
        this.add(PBlocks.URINE_COMPOOPER.get(), this::createUrineCompoomerDrops);
        dropSelf(PBlocks.PLACER.get());
        dropSelf(PBlocks.SIEVE.get());
        dropSelf(PBlocks.POOP_TNT.get());
        add(PBlocks.POOP_PIECE.get(), createPoopPieceDrop(PBlocks.POOP_PIECE.get(), PItems.POOP_BALL.get()));
        dropSelf(PBlocks.MAGGOTS_BLOCK.get());
        dropSelf(PBlocks.ROUNDWORM_BLOCK.get());

        LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(PBlocks.MAGGOTS.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, CropBlock.MAX_AGE));
        add(PBlocks.MAGGOTS.get(), maggotsCropDrops(PBlocks.MAGGOTS.get(), PItems.MAGGOTS_SEEDS.get(), builder));

        LootItemCondition.Builder builder2 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(PBlocks.ROUNDWORM_VINES_PLANT.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RoundwormVinesPlantBlock.SEEDS, true));
        add(PBlocks.ROUNDWORM_VINES_PLANT.get(), createRoundwormVinesDrop(PItems.ROUNDWORM.get(), builder2));
        dropOther(PBlocks.ROUNDWORM_VINES.get(), PItems.ROUNDWORM.get());

        dropSelf(PBlocks.FLY_BARREL.get());
        dropSelf(PBlocks.BREEDING_CHEST.get());
    }

    private void dropBlockFamily(PBlocks.BlockFamily family) {
        dropSelf(family.block().get());
        dropSelf(family.stairs().get());
        add(family.slab().get(), block -> createSlabItemTable(family.slab().get()));
        dropSelf(family.verticalSlab().get());
        dropSelf(family.wall().get());
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
                                return LootItem.lootTableItem(PBlocks.POOP_BLOCK.get());
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
                        LootItem.lootTableItem(PItems.SPALL)
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
                        LootItem.lootTableItem(PItems.POOP.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        ).withPool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(this.doesNotHaveShearsOrSilkTouch())
                        .add(((LootPoolSingletonContainer.Builder<?>)
                                this.applyExplosionCondition(block, LootItem.lootTableItem(PItems.ROUNDWORM.get())))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))
        );
    }

    protected LootTable.Builder createCompoomerDrops(Block block) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(this.applyExplosionDecay(block, LootItem.lootTableItem(PBlocks.COMPOOPER.get())))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(PItems.SAPLING_POOP_BALL.get()))
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
                                .add(this.applyExplosionDecay(block, LootItem.lootTableItem(PBlocks.COMPOOPER.get())))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(PItems.MAGGOTS_SEEDS.get()))
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(UrineCompooperBlock.MAGGOTS, true))
                                )
                );
    }

    protected LootTable.Builder dropToilet(Block block) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(block)
                                .apply(SetToiletTypeFunction.setType())));
    }

    private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
        return this.hasShearsOrSilkTouch().invert();
    }

    private LootItemCondition.Builder hasShearsOrSilkTouch() {
        return HAS_SHEARS.or(this.hasSilkTouch());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        List<Block> knownBlocks = new java.util.ArrayList<>();
        knownBlocks.add(PBlocks.WOODEN_TOILET.get());
        knownBlocks.add(PBlocks.HARD_TOILET.get());
        knownBlocks.add(PBlocks.POOP_LOG.get());
        knownBlocks.add(PBlocks.STRIPPED_POOP_LOG.get());
        knownBlocks.add(PBlocks.POOP_LEAVES_IRON.get());
        knownBlocks.add(PBlocks.POOP_LEAVES_GOLD.get());
        knownBlocks.add(PBlocks.POOP_LEAVES.get());
        knownBlocks.add(PBlocks.POOP_SAPLING.get());
        knownBlocks.add(PBlocks.POOP_EMPTY_LOG.get());
        knownBlocks.add(PBlocks.STRIPPED_POOP_EMPTY_LOG.get());

        for (var block : PBlocks.POOP_FAMILY.blocks()) knownBlocks.add(block.get());
        knownBlocks.add(PBlocks.POOP_BUTTON.get());
        knownBlocks.add(PBlocks.POOP_PRESSURE_PLATE.get());
        knownBlocks.add(PBlocks.POOP_FENCE.get());
        knownBlocks.add(PBlocks.POOP_FENCE_GATE.get());
        knownBlocks.add(PBlocks.POOP_TRAPDOOR.get());
        knownBlocks.add(PBlocks.POOP_DOOR.get());

        for (var block : PBlocks.CHILI_POOP_FAMILY.blocks()) knownBlocks.add(block.get());
        for (var block : PBlocks.GOLDEN_POOP_FAMILY.blocks()) knownBlocks.add(block.get());

        for (var block : PBlocks.POOP_BRICK_FAMILY.blocks()) knownBlocks.add(block.get());
        knownBlocks.add(PBlocks.CRACKED_POOP_BRICKS.get());
        for (var block : PBlocks.MOSSY_POOP_BRICK_FAMILY.blocks()) knownBlocks.add(block.get());
        for (var block : PBlocks.DRIED_POOP_BLOCK_FAMILY.blocks()) knownBlocks.add(block.get());
        for (var block : PBlocks.SMOOTH_POOP_BLOCK_FAMILY.blocks()) knownBlocks.add(block.get());
        for (var block : PBlocks.CUT_POOP_BLOCK_FAMILY.blocks()) knownBlocks.add(block.get());
        for (var block : PBlocks.TILE_BLOCK_FAMILY.blocks()) knownBlocks.add(block.get());

        knownBlocks.add(PBlocks.RAW_POOP_BLOCK.get());
        knownBlocks.add(PBlocks.RAW_SAPLING_POOP_BLOCK.get());
        knownBlocks.add(PBlocks.RAW_SEA_POOP_BLOCK.get());
        knownBlocks.add(PBlocks.RAW_WITHER_POOP_BLOCK.get());

        knownBlocks.add(PBlocks.POOP_CAKE.get());
        PBlocks.getPoopCandleCakes().forEach((candle, candleCake) -> knownBlocks.add(candleCake.get()));
        knownBlocks.add(PBlocks.POOLIME_BLOCK.get());
        knownBlocks.add(PBlocks.POOLIME_MAGGOTS_BLOCK.get());
        knownBlocks.add(PBlocks.STOOL.get());
        knownBlocks.add(PBlocks.COMPOOPER.get());
        knownBlocks.add(PBlocks.WATER_COMPOOPER.get());
        knownBlocks.add(PBlocks.LAVA_COMPOOPER.get());
        knownBlocks.add(PBlocks.POWDER_SNOW_COMPOOPER.get());
        knownBlocks.add(PBlocks.URINE_COMPOOPER.get());
        knownBlocks.add(PBlocks.PLACER.get());
        knownBlocks.add(PBlocks.SIEVE.get());
        knownBlocks.add(PBlocks.POOP_TNT.get());
        knownBlocks.add(PBlocks.POOP_PIECE.get());
        knownBlocks.add(PBlocks.MAGGOTS_BLOCK.get());
        knownBlocks.add(PBlocks.ROUNDWORM_BLOCK.get());
        knownBlocks.add(PBlocks.MAGGOTS.get());
        knownBlocks.add(PBlocks.ROUNDWORM_VINES_PLANT.get());
        knownBlocks.add(PBlocks.ROUNDWORM_VINES.get());
        knownBlocks.add(PBlocks.FLY_BARREL.get());
        knownBlocks.add(PBlocks.BREEDING_CHEST.get());

        return knownBlocks;
    }
}