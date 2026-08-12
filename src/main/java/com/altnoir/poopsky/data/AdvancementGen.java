package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.*;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class AdvancementGen {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private AdvancementGen() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, AdvancementGen::generate);
    }

    private static void generate(RegistrateAdvancementProvider provider) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        PoBlocks.WOODEN_TOILET.get(),
                        Component.translatable("advancements.poopsky.root.title"),
                        Component.translatable("advancements.poopsky.root.description"),
                        PoopSky.loc("textures/block/poop_block.png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("join_poopsky", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.POOP.get()))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(provider, modId("root"));

        AdvancementHolder poop_block = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        PoBlocks.POOP_BLOCK.get(),
                        Component.translatable("advancements.poopsky.poop_block.title"),
                        Component.translatable("advancements.poopsky.poop_block.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_block", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.POOP_BLOCK.get()))
                .save(provider, modId("poop_block"));
        Advancement.Builder.advancement()
                .parent(poop_block)
                .display(
                        PoBlocks.POOP_BLOCK.get(),
                        Component.translatable("advancements.poopsky.poop_block_slide.title"),
                        Component.translatable("advancements.poopsky.poop_block_slide.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_block_slide", SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(PoBlocks.POOP_BLOCK.get()))
                .save(provider, modId("poop_block_slide"));

        AdvancementHolder poop_crafting_table = Advancement.Builder.advancement()
                .parent(poop_block)
                .display(
                        PoBlocks.POOP_CRAFTING_TABLE.get(),
                        Component.translatable("advancements.poopsky.poop_crafting_table.title"),
                        Component.translatable("advancements.poopsky.poop_crafting_table.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.POOP_CRAFTING_TABLE.get()))
                .save(provider, modId("poop_crafting_table"));

        AdvancementHolder saltpeter_cluster = Advancement.Builder.advancement()
                .parent(poop_block)
                .display(
                        PoBlocks.SALTPETER_CLUSTER.get(),
                        Component.translatable("advancements.poopsky.saltpeter_cluster.title"),
                        Component.translatable("advancements.poopsky.saltpeter_cluster.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("saltpeter_cluster", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.SALTPETER_SHARD.get()))
                .save(provider, modId("saltpeter_cluster"));
        Advancement.Builder.advancement()
                .parent(saltpeter_cluster)
                .display(
                        Blocks.POWDER_SNOW,
                        Component.translatable("advancements.poopsky.powder_snow.title"),
                        Component.translatable("advancements.poopsky.powder_snow.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("powder_snow", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POWDER_SNOW_BUCKET))
                .save(provider, modId("powder_snow"));

        AdvancementHolder poop_sapling = Advancement.Builder.advancement()
                .parent(poop_block)
                .display(
                        PoBlocks.POOP_SAPLING.get(),
                        Component.translatable("advancements.poopsky.poop_sapling.title"),
                        Component.translatable("advancements.poopsky.poop_sapling.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_sapling", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.POOP_SAPLING.get()))
                .save(provider, modId("poop_sapling"));
        Advancement.Builder.advancement()
                .parent(poop_crafting_table)
                .display(
                        Blocks.POINTED_DRIPSTONE,
                        Component.translatable("advancements.poopsky.pointed_dripstone.title"),
                        Component.translatable("advancements.poopsky.pointed_dripstone.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("pointed_dripstone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.POINTED_DRIPSTONE))
                .save(provider, modId("pointed_dripstone"));

        AdvancementHolder compooper = Advancement.Builder.advancement()
                .parent(poop_crafting_table)
                .display(
                        PoBlocks.COMPOOPER.get(),
                        Component.translatable("advancements.poopsky.compooper.title"),
                        Component.translatable("advancements.poopsky.compooper.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("compooper", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.COMPOOPER.get()))
                .save(provider, modId("compooper"));
        AdvancementHolder toilet_plug = Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        PoItems.TOILET_PLUG,
                        Component.translatable("advancements.poopsky.toilet_plug.title"),
                        Component.translatable("advancements.poopsky.toilet_plug.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("toilet_plug", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.TOILET_PLUG.get()))
                .save(provider, modId("toilet_plug"));
        Advancement.Builder.advancement()
                .parent(toilet_plug)
                .display(
                        PoBlocks.PLACER,
                        Component.translatable("advancements.poopsky.placer.title"),
                        Component.translatable("advancements.poopsky.placer.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("placer", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.PLACER.get()))
                .save(provider, modId("placer"));
        Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        PoItems.OMEN_CHESTPLATE,
                        Component.translatable("advancements.poopsky.omen_armor.title"),
                        Component.translatable("advancements.poopsky.omen_armor.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("omen_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.OMEN_HELMET))
                .addCriterion("omen_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.OMEN_CHESTPLATE))
                .addCriterion("omen_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.OMEN_LEGGINGS))
                .addCriterion("omen_boots", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.OMEN_BOOTS))
                .save(provider, modId("omen_armor"));

        AdvancementHolder chili = Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        PoItems.DRAGON_BREATH_CHILI,
                        Component.translatable("advancements.poopsky.chili.title"),
                        Component.translatable("advancements.poopsky.chili.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("chili", EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(PoEffects.INTESTINAL_SPASM)))
                .save(provider, modId("chili"));
        Advancement.Builder.advancement()
                .parent(chili)
                .display(
                        PoBlocks.DRIED_POOP_BLOCK,
                        Component.translatable("advancements.poopsky.dried_poop_block.title"),
                        Component.translatable("advancements.poopsky.dried_poop_block.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("dried_poop_block", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.DRIED_POOP_BLOCK.get()))
                .save(provider, modId("dried_poop_block"));
        AdvancementHolder chili_poop = Advancement.Builder.advancement()
                .parent(chili)
                .display(
                        PoItems.CHILI_POOP,
                        Component.translatable("advancements.poopsky.chili_poop.title"),
                        Component.translatable("advancements.poopsky.chili_poop.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("chili_poop", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.CHILI_POOP.get()))
                .save(provider, modId("chili_poop"));
        AdvancementHolder chili_poop_block = Advancement.Builder.advancement()
                .parent(chili_poop)
                .display(
                        PoBlocks.CHILI_POOP_BLOCK,
                        Component.translatable("advancements.poopsky.chili_poop_block.title"),
                        Component.translatable("advancements.poopsky.chili_poop_block.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("chili_poop_block", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.CHILI_POOP_BLOCK.get()))
                .save(provider, modId("chili_poop_block"));
        Advancement.Builder.advancement()
                .parent(chili_poop_block)
                .display(
                        PoBlocks.FLUSH_TOILET,
                        Component.translatable("advancements.poopsky.flush_toilet.title"),
                        Component.translatable("advancements.poopsky.flush_toilet.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("flush_toilet", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.FLUSH_TOILET.get()))
                .save(provider, modId("flush_toilet"));
        var rainbowStack = new ItemStack(PoBlocks.HARD_TOILET.get());
        rainbowStack.set(PoComponents.TOILET_TYPE.get(), ToiletTypes.RAINBOW);

        Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        rainbowStack,
                        Component.translatable("advancements.poopsky.rainbow_toilet.title"),
                        Component.translatable("advancements.poopsky.rainbow_toilet.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("rainbow_toilet", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                        .of(PoBlocks.HARD_TOILET.get())
                        .hasComponents(DataComponentPredicate.builder()
                                .expect(PoComponents.TOILET_TYPE.get(), ToiletTypes.RAINBOW)
                                .build())))
                .save(provider, modId("rainbow_toilet"));

        AdvancementHolder urine_compooper = Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        PoBlocks.URINE_COMPOOPER.get(),
                        Component.translatable("advancements.poopsky.urine_compooper.title"),
                        Component.translatable("advancements.poopsky.urine_compooper.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("urine_compooper", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.URINE_COMPOOPER.get()))
                .save(provider, modId("urine_compooper"));
        AdvancementHolder maggots = Advancement.Builder.advancement()
                .parent(urine_compooper)
                .display(
                        PoBlocks.MAGGOTS_BLOCK.get(),
                        Component.translatable("advancements.poopsky.maggots.title"),
                        Component.translatable("advancements.poopsky.maggots.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("maggots", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.MAGGOTS_BLOCK.get()))
                .save(provider, modId("maggots"));
        AdvancementHolder poolime_maggots_block = Advancement.Builder.advancement()
                .parent(maggots)
                .display(
                        PoBlocks.POOLIME_MAGGOTS_BLOCK.get(),
                        Component.translatable("advancements.poopsky.poolime_maggots_block.title"),
                        Component.translatable("advancements.poopsky.poolime_maggots_block.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("poolime_maggots_block", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.POOLIME_MAGGOTS_BLOCK.get()))
                .save(provider, modId("poolime_maggots_block"));
        AdvancementHolder poop_ball = Advancement.Builder.advancement()
                .parent(poolime_maggots_block)
                .display(
                        PoItems.POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.poop_ball.title"),
                        Component.translatable("advancements.poopsky.poop_ball.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_ball", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.POOP_BALL.get()))
                .save(provider, modId("poop_ball"));
        AdvancementHolder poolime_block = Advancement.Builder.advancement()
                .parent(poop_ball)
                .display(
                        PoBlocks.POOLIME_BLOCK.get(),
                        Component.translatable("advancements.poopsky.poolime_block.title"),
                        Component.translatable("advancements.poopsky.poolime_block.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poolime_block", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.POOLIME_BLOCK.get()))
                .save(provider, modId("poolime_block"));
        Advancement.Builder.advancement()
                .parent(poolime_block)
                .display(
                        PoBlocks.BROWN_TILE_BLOCK.get(),
                        Component.translatable("advancements.poopsky.brown_tile_block.title"),
                        Component.translatable("advancements.poopsky.brown_tile_block.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("brown_tile_block", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.BROWN_TILE_BLOCK.get()))
                .save(provider, modId("brown_tile_block"));
        Advancement.Builder.advancement()
                .parent(poop_ball)
                .display(
                        PoItems.WITHER_POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.wither_poop_ball.title"),
                        Component.translatable("advancements.poopsky.wither_poop_ball.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("wither_poop_ball", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.WITHER_POOP_BALL.get()))
                .save(provider, modId("wither_poop_ball"));
        AdvancementHolder fly = Advancement.Builder.advancement()
                .parent(maggots)
                .display(
                        PoItems.FLY.get(),
                        Component.translatable("advancements.poopsky.fly.title"),
                        Component.translatable("advancements.poopsky.fly.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("fly", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(PoEntityType.FLY.get())))
                .save(provider, modId("fly"));
        AdvancementHolder fly_catcher = Advancement.Builder.advancement()
                .parent(fly)
                .display(
                        PoItems.FLY_CATCHER.get(),
                        Component.translatable("advancements.poopsky.fly_catcher.title"),
                        Component.translatable("advancements.poopsky.fly_catcher.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("fly_catcher", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.FLY_CATCHER.get()))
                .save(provider, modId("fly_catcher"));
        Advancement.Builder.advancement()
                .parent(fly_catcher)
                .display(
                        PoBlocks.FLY_BARREL.get(),
                        Component.translatable("advancements.poopsky.fly_barrel.title"),
                        Component.translatable("advancements.poopsky.fly_barrel.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("fly_barrel", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.FLY_BARREL.get()))
                .save(provider, modId("fly_barrel"));
        Advancement.Builder.advancement()
                .parent(fly_catcher)
                .display(
                        PoBlocks.BREEDING_CHEST.get(),
                        Component.translatable("advancements.poopsky.breeding_chest.title"),
                        Component.translatable("advancements.poopsky.breeding_chest.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("breeding_chest", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.BREEDING_CHEST.get()))
                .save(provider, modId("breeding_chest"));

        Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        PoItems.SAPLING_POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.sapling.title"),
                        Component.translatable("advancements.poopsky.sapling.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("sapling", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.SAPLING_POOP_BALL.get()))
                .save(provider, modId("sapling"));

        AdvancementHolder coal_block = Advancement.Builder.advancement()
                .parent(poop_sapling)
                .display(
                        Blocks.COAL_BLOCK,
                        Component.translatable("advancements.poopsky.coal_block.title"),
                        Component.translatable("advancements.poopsky.coal_block.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("coal_block", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COAL_BLOCK))
                .save(provider, modId("coal_block"));
        Advancement.Builder.advancement()
                .parent(coal_block)
                .display(
                        Items.COCOA_BEANS,
                        Component.translatable("advancements.poopsky.cocoa.title"),
                        Component.translatable("advancements.poopsky.cocoa.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("cocoa", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COCOA_BEANS))
                .save(provider, modId("cocoa"));
        AdvancementHolder string = Advancement.Builder.advancement()
                .parent(coal_block)
                .display(
                        Items.STRING,
                        Component.translatable("advancements.poopsky.string.title"),
                        Component.translatable("advancements.poopsky.string.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("string", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STRING))
                .save(provider, modId("string"));
        Advancement.Builder.advancement()
                .parent(string)
                .display(
                        PoItems.SEA_POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.sea_poop_ball.title"),
                        Component.translatable("advancements.poopsky.sea_poop_ball.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("sea_poop_ball", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.SEA_POOP_BALL.get()))
                .save(provider, modId("sea_poop_ball"));
        Advancement.Builder.advancement()
                .parent(string)
                .display(
                        PoItems.FOLIUM_SENNAE,
                        Component.translatable("advancements.poopsky.foliium_senna.title"),
                        Component.translatable("advancements.poopsky.foliium_senna.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("foliium_senna", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.FOLIUM_SENNAE.get()))
                .save(provider, modId("foliium_senna"));
        AdvancementHolder sieve = Advancement.Builder.advancement()
                .parent(string)
                .display(
                        PoBlocks.SIEVE,
                        Component.translatable("advancements.poopsky.sieve.title"),
                        Component.translatable("advancements.poopsky.sieve.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("sieve", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.SIEVE.get()))
                .save(provider, modId("sieve"));

        AdvancementHolder king_of_dragon_fruit = Advancement.Builder.advancement()
                .parent(sieve)
                .display(
                        PoItems.KING_OF_DRAGON_FRUIT,
                        Component.translatable("advancements.poopsky.king_of_dragon_fruit.title"),
                        Component.translatable("advancements.poopsky.king_of_dragon_fruit.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("king_of_dragon_fruit", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.KING_OF_DRAGON_FRUIT.get()))
                .save(provider, modId("king_of_dragon_fruit"));
        Advancement.Builder.advancement()
                .parent(king_of_dragon_fruit)
                .display(
                        PoBlocks.POOP_TNT,
                        Component.translatable("advancements.poopsky.pooop_tnt.title"),
                        Component.translatable("advancements.poopsky.pooop_tnt.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("pooop_tnt", InventoryChangeTrigger.TriggerInstance.hasItems(PoBlocks.POOP_TNT.get()))
                .save(provider, modId("pooop_tnt"));

        AdvancementHolder roundworm = Advancement.Builder.advancement()
                .parent(poop_sapling)
                .display(
                        PoItems.ROUNDWORM,
                        Component.translatable("advancements.poopsky.roundworm.title"),
                        Component.translatable("advancements.poopsky.roundworm.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("roundworm", InventoryChangeTrigger.TriggerInstance.hasItems(PoItems.ROUNDWORM.get()))
                .save(provider, modId("roundworm"));

        AdvancementHolder summon_villager = Advancement.Builder.advancement()
                .parent(roundworm)
                .display(
                        Blocks.CARVED_PUMPKIN,
                        Component.translatable("advancements.poopsky.summon_villager.title"),
                        Component.translatable("advancements.poopsky.summon_villager.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("summon_villager", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.VILLAGER)))
                .save(provider, modId("summon_villager"));
    }

    private static String modId(String path) {
        return PoopSky.MOD_ID + ":" + path;
    }
}
