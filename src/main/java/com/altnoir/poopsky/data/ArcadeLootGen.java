package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
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
        REGISTRATE.addDataGenerator(ProviderType.LOOT, provider -> provider.addLootAction(LootContextParamSets.EMPTY, ArcadeLootGen::generate));
    }

    public static ResourceKey<LootTable> lootTableKey(Block block) {
        return ResourceKey.create(Registries.LOOT_TABLE, PoopSky.loc("gameplay/arcade/" + PoopSky.getBlockPath(block)));
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
                        .add(LootItem.lootTableItem(PoItems.TOKEN).setWeight(80)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 12.0F))))
                        .add(LootItem.lootTableItem(Items.GLOW_INK_SAC).setWeight(35)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F))))
                        .add(LootItem.lootTableItem(Items.RABBIT_FOOT).setWeight(35)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                        .add(LootItem.lootTableItem(Items.RABBIT_HIDE).setWeight(35)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F))))
                        .add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(35)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                        .add(LootItem.lootTableItem(Items.SMALL_DRIPLEAF).setWeight(30)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                        .add(LootItem.lootTableItem(Items.ECHO_SHARD).setWeight(30)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))))
                        .add(LootItem.lootTableItem(Items.DISC_FRAGMENT_5).setWeight(20))
                        .add(LootItem.lootTableItem(Items.TURTLE_SCUTE).setWeight(20))
                        .add(LootItem.lootTableItem(Items.ARMADILLO_SCUTE).setWeight(20))
                        .add(LootItem.lootTableItem(Items.NAUTILUS_SHELL).setWeight(20))
                        .add(LootItem.lootTableItem(Items.TURTLE_EGG).setWeight(15))
                        .add(LootItem.lootTableItem(Items.SNIFFER_EGG).setWeight(15))
                        .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL).setWeight(10))
                        .add(LootItem.lootTableItem(Items.DRAGON_HEAD).setWeight(10))
                        .add(LootItem.lootTableItem(Items.SCULK_CATALYST).setWeight(10))
                        .add(LootItem.lootTableItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).setWeight(10))
                        .add(LootItem.lootTableItem(Items.HEART_OF_THE_SEA).setWeight(5))
                        .add(LootItem.lootTableItem(Items.HEAVY_CORE).setWeight(5))
                        .add(LootItem.lootTableItem(Items.ELYTRA).setWeight(5))
                        .add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS).setWeight(3))
                );
    }
}