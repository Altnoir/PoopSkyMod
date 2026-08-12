package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public final class ArcadeLootGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private ArcadeLootGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(
                ProviderType.LOOT,
                provider -> provider.addLootAction(LootContextParamSets.EMPTY, ArcadeLootGen::generate));
    }

    public static ResourceKey<LootTable> lootTableKey(Block block) {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                PoopSky.loc("gameplay/arcade/" + PoopSky.getBlockPath(block)));
    }

    private static void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        for (BlockEntry<ArcadeBlock> entry : PoBlocks.getArcadeBlocks()) {
            consumer.accept(lootTableKey(entry.get()), createArcadeLoot());
        }
    }

    private static LootTable.Builder createArcadeLoot() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.DIAMOND)
                                .setWeight(30)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .setWeight(30)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F))))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE)
                                .setWeight(15))
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .setWeight(20)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 12.0F))))
                        .add(LootItem.lootTableItem(Items.NETHERITE_INGOT)
                                .setWeight(5))
                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE)
                                .setWeight(1))
                );
    }
}