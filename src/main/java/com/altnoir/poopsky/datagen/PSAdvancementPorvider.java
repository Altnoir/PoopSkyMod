package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.ToiletBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.item.p.SapingBallItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PSAdvancementPorvider extends AdvancementProvider {
    public PSAdvancementPorvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AdventureAdvancements()));
    }

    private static class AdventureAdvancements implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            ToiletBlocks.OAK_TOILET.get(),
                            Component.translatable("advancements.poopsky.root.title"),
                            Component.translatable("advancements.poopsky.root.description"),
                            PoopSky.loc("textures/block/poop_block.png"),
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("join_poopsky", InventoryChangeTrigger.TriggerInstance.hasItems(PSItems.POOP.get()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(saver, modId("root"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            PSBlocks.POOP_BLOCK.get(),
                            Component.translatable("advancements.poopsky.poop_block_slide.title"),
                            Component.translatable("advancements.poopsky.poop_block_slide.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("poop_block_slide", SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(PSBlocks.POOP_BLOCK.get()))
                    .save(saver, modId("poop_block_slide"), existingFileHelper);

            AdvancementHolder poop_saping = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            PSBlocks.POOP_SAPLING.get(),
                            Component.translatable("advancements.poopsky.poop_sapling.title"),
                            Component.translatable("advancements.poopsky.poop_sapling.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("poop_sapling", InventoryChangeTrigger.TriggerInstance.hasItems(PSBlocks.POOP_SAPLING.get()))
                    .save(saver, modId("poop_sapling"), existingFileHelper);

            AdvancementHolder compooper = Advancement.Builder.advancement()
                    .parent(poop_saping)
                    .display(
                            PSBlocks.COMPOOPER.get(),
                            Component.translatable("advancements.poopsky.compooer.title"),
                            Component.translatable("advancements.poopsky.compooer.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("compooer", InventoryChangeTrigger.TriggerInstance.hasItems(PSBlocks.COMPOOPER.get()))
                    .save(saver, modId("compooer"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(compooper)
                    .display(
                            PSItems.SAPING_POOP_BALL.get(),
                            Component.translatable("advancements.poopsky.sapling.title"),
                            Component.translatable("advancements.poopsky.sapling.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("sapling", InventoryChangeTrigger.TriggerInstance.hasItems(PSItems.SAPING_POOP_BALL.get()))
                    .save(saver, modId("sapling"), existingFileHelper);

            AdvancementHolder coal_block = Advancement.Builder.advancement()
                    .parent(poop_saping)
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
        }

        private ResourceLocation modId(String path) {
            return PoopSky.loc(path);
        }
    }
}
