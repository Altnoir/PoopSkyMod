package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSItemTagProvider extends ItemTagsProvider {


    public PSItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Items.FOODS)
                .add(PSItems.POOP.get())
                .add(PSItems.SAPING_BALL.get())
                .add(PSItems.POOP_BREAD.get())
                .add(PSItems.POOP_DUMPLINGS.get())
                .add(PSItems.POOP_SOUP.get())
                .add(PSItems.POOP_VEGETABLE_STICKS.get())
                .add(PSItems.POOBURGER_MEAT.get())
                .add(PSItems.POOBURGER.get())
                .add(PSItems.POOP_PASTA.get())
                .add(PSItems.POODDING.get())
                .add(PSItems.DRAGON_BREATH_CHILI.get());
    }
}
