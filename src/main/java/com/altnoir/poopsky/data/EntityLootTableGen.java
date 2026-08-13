package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.loot.RegistrateEntityLootTables;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider.LootType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
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

public final class EntityLootTableGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private EntityLootTableGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(
                ProviderType.LOOT,
                provider -> provider.addLootAction(LootType.ENTITY, EntityLootTableGen::generate));
    }

    private static void generate(RegistrateEntityLootTables loot) {
        HolderLookup.Provider registries = loot.getRegistries();

        loot.add(
                PoEntityType.POOLIME.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(PoItems.POOP_BALL.get())
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                                        .when(killedByFrog(registries).invert())
                                        )
                                        .add(
                                                LootItem.lootTableItem(PoItems.POOP_BALL.get())
                                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                                        .when(killedByFrog(registries))
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
                PoEntityType.FLY.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(PoItems.MAGGOTS_SEEDS.get())
                                                        .apply(SmeltItemFunction.smelted().when(shouldSmeltLoot(registries)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, ConstantValue.exactly(1.0F)))
                                        )
                        )
        );
        loot.add(PoEntityType.TOILET_PLUG.get(), LootTable.lootTable());
        loot.add(PoEntityType.STOOL.get(), LootTable.lootTable());
        loot.add(PoEntityType.FLUSH_TOILET.get(), LootTable.lootTable());
        loot.add(PoEntityType.FLUSH_TOILET_CART.get(), LootTable.lootTable());
        loot.add(PoEntityType.GOLDEN_FLUSH_TOILET_CART.get(), LootTable.lootTable());
        loot.add(PoEntityType.POOP_TNT.get(), LootTable.lootTable());
        loot.add(PoEntityType.GINKGO_BOAT.get(), LootTable.lootTable());
        loot.add(PoEntityType.GINKGO_CHEST_BOAT.get(), LootTable.lootTable());
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
                                                                .withComponents(DataComponentMatchers.Builder.components().partial(
                                                                        DataComponentPredicates.ENCHANTMENTS,
                                                                        EnchantmentsPredicate.enchantments(
                                                                                List.of(new EnchantmentPredicate(
                                                                                        enchantments.getOrThrow(EnchantmentTags.SMELTS_LOOT),
                                                                                        MinMaxBounds.Ints.ANY))
                                                                        )
                                                                ).build())
                                                )
                                )
                )
        );
    }

    private static LootItemCondition.Builder killedByFrog(HolderLookup.Provider registries) {
        return DamageSourceCondition.hasDamageSource(
                DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity()
                        .of(registries.lookupOrThrow(Registries.ENTITY_TYPE), EntityType.FROG)));
    }
}
