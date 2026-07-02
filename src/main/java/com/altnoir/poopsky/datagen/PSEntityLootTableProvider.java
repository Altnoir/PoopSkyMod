package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.init.PItems;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SlimePredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class PSEntityLootTableProvider extends EntityLootSubProvider {
    protected PSEntityLootTableProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        this.add(
                PEntityType.POOLIME.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(PItems.POOP_BALL.get())
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                                        .when(this.killedByFrog().invert())
                                        )
                                        .add(
                                                LootItem.lootTableItem(PItems.POOP_BALL.get())
                                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                                        .when(this.killedByFrog())
                                        )
                                        .when(
                                                LootItemEntityPropertyCondition.hasProperties(
                                                        LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity().subPredicate(SlimePredicate.sized(MinMaxBounds.Ints.exactly(1)))
                                                )
                                        )
                        )
        );
        this.add(
                PEntityType.FLY.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(PItems.MAGGOTS_SEEDS.get())
                                                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, ConstantValue.exactly(1.0F)))
                                        )
                        )
        );
        this.add(
                PEntityType.MAGGOT.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(PItems.MAGGOTS_SEEDS.get())
                                                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, ConstantValue.exactly(1.0F)))
                                        )
                        )
        );
    }

    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
        return PEntityType.ENTITY_TYPES.getEntries().stream().map(Holder::value);
    }
}
