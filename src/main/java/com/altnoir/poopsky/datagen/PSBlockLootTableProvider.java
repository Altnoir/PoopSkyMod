package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.block.p.PoopPieceBlock;
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
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
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

        dropSelf(PSBlocks.POOPLIME_BLOCK.get());
        dropSelf(PSBlocks.COMPOOPER.get());
        dropOther(PSBlocks.WATER_COMPOOPER.get(), PSBlocks.COMPOOPER.get());
        dropOther(PSBlocks.LAVA_COMPOOPER.get(), PSBlocks.COMPOOPER.get());
        dropOther(PSBlocks.URINE_COMPOOPER.get(), PSBlocks.COMPOOPER.get());
        add(PSBlocks.POOP_PIECE.get(), createPoopPieceDrop(PSBlocks.POOP_PIECE.get(), PSItems.POOP_BALL.get()));
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
        ).when(LootItemRandomChanceCondition.randomChance(1.0F).invert());

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
                        LootItem.lootTableItem(PSItems.POOP)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
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