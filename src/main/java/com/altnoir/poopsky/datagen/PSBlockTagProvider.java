package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PSBlockTagProvider extends BlockTagsProvider {
    public PSBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MOSS_REPLACEABLE)
                .add(PSBlocks.POOP_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(PSBlocks.POOP_PIECE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(PSBlocks.COMPOOPER.get());
    }
}
