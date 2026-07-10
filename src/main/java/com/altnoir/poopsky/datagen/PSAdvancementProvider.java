package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.altnoir.poopsky.init.*;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class PSAdvancementProvider {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    private PSAdvancementProvider() {
    }

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, PSAdvancementProvider::generate);
    }

    private static void generate(RegistrateAdvancementProvider provider) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        PBlocks.WOODEN_TOILET.get(),
                        Component.translatable("advancements.poopsky.root.title"),
                        Component.translatable("advancements.poopsky.root.description"),
                        PoopSky.loc("textures/block/poop_block.png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("join_poopsky", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.POOP.get()))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(provider, modId("root"));

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        PBlocks.POOP_BLOCK.get(),
                        Component.translatable("advancements.poopsky.poop_block_slide.title"),
                        Component.translatable("advancements.poopsky.poop_block_slide.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_block_slide", SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(PBlocks.POOP_BLOCK.get()))
                .save(provider, modId("poop_block_slide"));

        AdvancementHolder poop_sapling = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        PBlocks.POOP_SAPLING.get(),
                        Component.translatable("advancements.poopsky.poop_sapling.title"),
                        Component.translatable("advancements.poopsky.poop_sapling.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_sapling", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.POOP_SAPLING.get()))
                .save(provider, modId("poop_sapling"));
        Advancement.Builder.advancement()
                .parent(poop_sapling)
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
                .parent(poop_sapling)
                .display(
                        PBlocks.COMPOOPER.get(),
                        Component.translatable("advancements.poopsky.compooper.title"),
                        Component.translatable("advancements.poopsky.compooper.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("compooper", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.COMPOOPER.get()))
                .save(provider, modId("compooper"));
        AdvancementHolder urine_compooper = Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        PBlocks.URINE_COMPOOPER.get(),
                        Component.translatable("advancements.poopsky.urine_compooper.title"),
                        Component.translatable("advancements.poopsky.urine_compooper.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("urine_compooper", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.URINE_COMPOOPER.get()))
                .save(provider, modId("urine_compooper"));
        AdvancementHolder maggots = Advancement.Builder.advancement()
                .parent(urine_compooper)
                .display(
                        PBlocks.MAGGOTS_BLOCK.get(),
                        Component.translatable("advancements.poopsky.maggots.title"),
                        Component.translatable("advancements.poopsky.maggots.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("maggots", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.MAGGOTS_BLOCK.get()))
                .save(provider, modId("maggots"));
        AdvancementHolder poolime_maggots_block = Advancement.Builder.advancement()
                .parent(maggots)
                .display(
                        PBlocks.POOLIME_MAGGOTS_BLOCK.get(),
                        Component.translatable("advancements.poopsky.poolime_maggots_block.title"),
                        Component.translatable("advancements.poopsky.poolime_maggots_block.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("poolime_maggots_block", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.POOLIME_MAGGOTS_BLOCK.get()))
                .save(provider, modId("poolime_maggots_block"));
        AdvancementHolder poop_ball = Advancement.Builder.advancement()
                .parent(poolime_maggots_block)
                .display(
                        PItems.POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.poop_ball.title"),
                        Component.translatable("advancements.poopsky.poop_ball.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("poop_ball", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.POOP_BALL.get()))
                .save(provider, modId("poop_ball"));
        Advancement.Builder.advancement()
                .parent(poop_ball)
                .display(
                        PItems.WITHER_POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.wither_poop_ball.title"),
                        Component.translatable("advancements.poopsky.wither_poop_ball.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("wither_poop_ball", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.WITHER_POOP_BALL.get()))
                .save(provider, modId("wither_poop_ball"));
        AdvancementHolder fly = Advancement.Builder.advancement()
                .parent(maggots)
                .display(
                        PItems.FLY.get(),
                        Component.translatable("advancements.poopsky.fly.title"),
                        Component.translatable("advancements.poopsky.fly.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("fly", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(PEntityType.FLY.get())))
                .save(provider, modId("fly"));
        AdvancementHolder fly_catcher = Advancement.Builder.advancement()
                .parent(fly)
                .display(
                        PItems.FLY_CATCHER.get(),
                        Component.translatable("advancements.poopsky.fly_catcher.title"),
                        Component.translatable("advancements.poopsky.fly_catcher.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("fly_catcher", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.FLY_CATCHER.get()))
                .save(provider, modId("fly_catcher"));
        Advancement.Builder.advancement()
                .parent(fly_catcher)
                .display(
                        PBlocks.FLY_BARREL.get(),
                        Component.translatable("advancements.poopsky.fly_barrel.title"),
                        Component.translatable("advancements.poopsky.fly_barrel.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("fly_barrel", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.FLY_BARREL.get()))
                .save(provider, modId("fly_barrel"));
        Advancement.Builder.advancement()
                .parent(fly_catcher)
                .display(
                        PBlocks.BREEDING_CHEST.get(),
                        Component.translatable("advancements.poopsky.breeding_chest.title"),
                        Component.translatable("advancements.poopsky.breeding_chest.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("breeding_chest", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.BREEDING_CHEST.get()))
                .save(provider, modId("breeding_chest"));

        Advancement.Builder.advancement()
                .parent(compooper)
                .display(
                        PItems.SAPLING_POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.sapling.title"),
                        Component.translatable("advancements.poopsky.sapling.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("sapling", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.SAPLING_POOP_BALL.get()))
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
                        PItems.SEA_POOP_BALL.get(),
                        Component.translatable("advancements.poopsky.sea_poop_ball.title"),
                        Component.translatable("advancements.poopsky.sea_poop_ball.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("sea_poop_ball", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.SEA_POOP_BALL.get()))
                .save(provider, modId("sea_poop_ball"));
        Advancement.Builder.advancement()
                .parent(string)
                .display(
                        PItems.FOLIUM_SENNAE,
                        Component.translatable("advancements.poopsky.foliium_senna.title"),
                        Component.translatable("advancements.poopsky.foliium_senna.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("foliium_senna", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.FOLIUM_SENNAE.get()))
                .save(provider, modId("foliium_senna"));
        AdvancementHolder sieve = Advancement.Builder.advancement()
                .parent(string)
                .display(
                        PBlocks.SIEVE,
                        Component.translatable("advancements.poopsky.sieve.title"),
                        Component.translatable("advancements.poopsky.sieve.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("sieve", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.SIEVE.get()))
                .save(provider, modId("sieve"));

        AdvancementHolder king_of_dragon_fruit = Advancement.Builder.advancement()
                .parent(sieve)
                .display(
                        PItems.KING_OF_DRAGON_FRUIT,
                        Component.translatable("advancements.poopsky.king_of_dragon_fruit.title"),
                        Component.translatable("advancements.poopsky.king_of_dragon_fruit.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("king_of_dragon_fruit", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.KING_OF_DRAGON_FRUIT.get()))
                .save(provider, modId("king_of_dragon_fruit"));
        Advancement.Builder.advancement()
                .parent(king_of_dragon_fruit)
                .display(
                        PBlocks.POOP_TNT,
                        Component.translatable("advancements.poopsky.pooop_tnt.title"),
                        Component.translatable("advancements.poopsky.pooop_tnt.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("pooop_tnt", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.POOP_TNT.get()))
                .save(provider, modId("pooop_tnt"));

        AdvancementHolder roundworm = Advancement.Builder.advancement()
                .parent(poop_sapling)
                .display(
                        PItems.ROUNDWORM,
                        Component.translatable("advancements.poopsky.roundworm.title"),
                        Component.translatable("advancements.poopsky.roundworm.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("roundworm", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.ROUNDWORM.get()))
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
        AdvancementHolder toilet_plug = Advancement.Builder.advancement()
                .parent(summon_villager)
                .display(
                        PItems.TOILET_PLUG,
                        Component.translatable("advancements.poopsky.toilet_plug.title"),
                        Component.translatable("advancements.poopsky.toilet_plug.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("toilet_plug", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.TOILET_PLUG.get()))
                .save(provider, modId("toilet_plug"));
        Advancement.Builder.advancement()
                .parent(toilet_plug)
                .display(
                        PBlocks.PLACER,
                        Component.translatable("advancements.poopsky.placer.title"),
                        Component.translatable("advancements.poopsky.placer.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("placer", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.PLACER.get()))
                .save(provider, modId("placer"));
        Advancement.Builder.advancement()
                .parent(summon_villager)
                .display(
                        PItems.OMEN_CHESTPLATE,
                        Component.translatable("advancements.poopsky.omen_armor.title"),
                        Component.translatable("advancements.poopsky.omen_armor.description"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("omen_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.OMEN_HELMET))
                .addCriterion("omen_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.OMEN_CHESTPLATE))
                .addCriterion("omen_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.OMEN_LEGGINGS))
                .addCriterion("omen_boots", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.OMEN_BOOTS))
                .save(provider, modId("omen_armor"));

        AdvancementHolder chili = Advancement.Builder.advancement()
                .parent(summon_villager)
                .display(
                        PItems.DRAGON_BREATH_CHILI,
                        Component.translatable("advancements.poopsky.chili.title"),
                        Component.translatable("advancements.poopsky.chili.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("chili", EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.Builder.effects().and(PEffects.INTESTINAL_SPASM)))
                .save(provider, modId("chili"));

        var rainbowStack = new ItemStack(PBlocks.HARD_TOILET.get());
        rainbowStack.set(PComponents.TOILET_TYPE.get(), PToiletTypes.RAINBOW);

        Advancement.Builder.advancement()
                .parent(summon_villager)
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
                        .of(PBlocks.HARD_TOILET.get())
                        .hasComponents(DataComponentPredicate.builder()
                                .expect(PComponents.TOILET_TYPE.get(), PToiletTypes.RAINBOW)
                                .build())))
                .save(provider, modId("rainbow_toilet"));

        Advancement.Builder.advancement()
                .parent(chili)
                .display(
                        PItems.CHILI_POOP,
                        Component.translatable("advancements.poopsky.chili_poop.title"),
                        Component.translatable("advancements.poopsky.chili_poop.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("chili_poop", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.CHILI_POOP.get()))
                .save(provider, modId("chili_poop"));
    }

    private static String modId(String path) {
        return PoopSky.MOD_ID + ":" + path;
    }
}
