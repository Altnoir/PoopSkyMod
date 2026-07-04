package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.*;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PSAdvancementProvider extends AdvancementProvider {
    public PSAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AdventureAdvancements()));
    }

    private static class AdventureAdvancements implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
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
                    .save(saver, modId("root"), existingFileHelper);

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
                    .save(saver, modId("poop_block_slide"), existingFileHelper);

            AdvancementHolder poolime_poop_block = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            PBlocks.POOLIME_POOP_BLOCK.get(),
                            Component.translatable("advancements.poopsky.poolime_poop_block.title"),
                            Component.translatable("advancements.poopsky.poolime_poop_block.description"),
                            null,
                            AdvancementType.GOAL,
                            true,
                            true,
                            false
                    )
                    .addCriterion("poolime_poop_block", InventoryChangeTrigger.TriggerInstance.hasItems(PBlocks.POOLIME_POOP_BLOCK.get()))
                    .save(saver, modId("poolime_poop_block"), existingFileHelper);
            AdvancementHolder poop_ball = Advancement.Builder.advancement()
                    .parent(poolime_poop_block)
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
                    .save(saver, modId("poop_ball"), existingFileHelper);
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
                    .save(saver, modId("wither_poop_ball"), existingFileHelper);

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
                    .save(saver, modId("poop_sapling"), existingFileHelper);

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
                    .save(saver, modId("compooper"), existingFileHelper);
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
                    .save(saver, modId("urine_compooper"), existingFileHelper);
            Advancement.Builder.advancement()
                    .parent(urine_compooper)
                    .display(
                            PItems.MAGGOTS_SEEDS.get(),
                            Component.translatable("advancements.poopsky.maggots_seeds.title"),
                            Component.translatable("advancements.poopsky.maggots_seeds.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("maggots_seeds", InventoryChangeTrigger.TriggerInstance.hasItems(PItems.MAGGOTS_SEEDS.get()))
                    .save(saver, modId("maggots_seeds"), existingFileHelper);

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
                    .save(saver, modId("sapling"), existingFileHelper);

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
                    .save(saver, modId("coal_block"), existingFileHelper);
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
                    .save(saver, modId("cocoa"), existingFileHelper);
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
                    .save(saver, modId("string"), existingFileHelper);
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
                    .save(saver, modId("sea_poop_ball"), existingFileHelper);
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
                    .save(saver, modId("foliium_senna"), existingFileHelper);
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
                    .save(saver, modId("sieve"), existingFileHelper);

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
                    .save(saver, modId("king_of_dragon_fruit"), existingFileHelper);
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
                    .save(saver, modId("pooop_tnt"), existingFileHelper);

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
                    .save(saver, modId("roundworm"), existingFileHelper);

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
                    .save(saver, modId("summon_villager"), existingFileHelper);
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
                    .save(saver, modId("toilet_plug"), existingFileHelper);
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
                    .save(saver, modId("placer"), existingFileHelper);
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
                    .save(saver, modId("omen_armor"), existingFileHelper);

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
                    .save(saver, modId("chili"), existingFileHelper);

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
                    .save(saver, modId("rainbow_toilet"), existingFileHelper);

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
                    .save(saver, modId("chili_poop"), existingFileHelper);
        }

        private ResourceLocation modId(String path) {
            return PoopSky.loc(path);
        }
    }
}