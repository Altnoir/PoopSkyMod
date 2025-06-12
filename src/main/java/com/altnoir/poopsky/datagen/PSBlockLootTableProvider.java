package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.block.p.PoopPiece;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.IntStream;

public class PSBlockLootTableProvider extends BlockLootSubProvider {
    protected PSBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        ToiletBlocks.BLOCKS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .forEach(this::dropSelf);

        dropSelf(PSBlocks.POOP_BLOCK.get());
        dropSelf(PSBlocks.COMPOOPER.get());
        add(PSBlocks.POOP_PIECE.get(), createPoopPieceDrop(PSBlocks.POOP_PIECE.get(), PSItems.POOP_BALL.get()));
    }

    protected LootTable.@NotNull Builder createPoopPieceDrop(Block block, Item item) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        LootPoolEntryContainer.Builder<?> nonSilkTouch = AlternativesEntry.alternatives(
            IntStream.rangeClosed(1, 8)
                .mapToObj(i -> LootItem.lootTableItem(item)
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PoopPiece.LAYERS, i)))
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
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PoopPiece.LAYERS, i)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)));
                }).toArray(LootPoolEntryContainer.Builder[]::new)
        ).when(hasSilkTouch());

        return LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .add(AlternativesEntry.alternatives(nonSilkTouch, silkTouch))
            );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return PSBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}