package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.init.PItems;
import com.altnoir.poopsky.init.PRegistries;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.loot.RegistrateEntityLootTables;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider.LootType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;

public final class PSEntityLootTableProvider {
    private PSEntityLootTableProvider() {
    }

    public static void register() {
        PRegistries.REGISTRATE.addDataGenerator(
                ProviderType.LOOT,
                provider -> provider.addLootAction(LootType.ENTITY, PSEntityLootTableProvider::generate));
    }

    private static void generate(RegistrateEntityLootTables loot) {
        HolderLookup.Provider registries = loot.getRegistries();

        loot.add(
                PEntityType.POOLIME.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(PItems.POOP_BALL.get())
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                                        .when(killedByFrog().invert())
                                        )
                                        .add(
                                                LootItem.lootTableItem(PItems.POOP_BALL.get())
                                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                                        .when(killedByFrog())
                                        )
                                        .when(
                                                LootItemEntityPropertyCondition.hasProperties(
                                                        LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity().subPredicate(SlimePredicate.sized(MinMaxBounds.Ints.exactly(1)))
                                                )
                                        )
                        )
        );
        loot.add(
                PEntityType.FLY.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(PItems.MAGGOTS_SEEDS.get())
                                                        .apply(SmeltItemFunction.smelted().when(shouldSmeltLoot(registries)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, ConstantValue.exactly(1.0F)))
                                        )
                        )
        );
    }

    private static AnyOfCondition.Builder shouldSmeltLoot(HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);
        return AnyOfCondition.anyOf(
                LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.THIS,
                        EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))
                ),
                LootItemEntityPropertyCondition.hasProperties(
                        LootContext.EntityTarget.DIRECT_ATTACKER,
                        EntityPredicate.Builder.entity()
                                .equipment(
                                        EntityEquipmentPredicate.Builder.equipment()
                                                .mainhand(
                                                        ItemPredicate.Builder.item()
                                                                .withSubPredicate(
                                                                        ItemSubPredicates.ENCHANTMENTS,
                                                                        ItemEnchantmentsPredicate.enchantments(
                                                                                List.of(new EnchantmentPredicate(
                                                                                        enchantments.getOrThrow(EnchantmentTags.SMELTS_LOOT),
                                                                                        MinMaxBounds.Ints.ANY))
                                                                        )
                                                                )
                                                )
                                )
                )
        );
    }

    private static LootItemCondition.Builder killedByFrog() {
        return DamageSourceCondition.hasDamageSource(
                DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
    }
}
