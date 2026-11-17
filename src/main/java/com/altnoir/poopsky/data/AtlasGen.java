package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AtlasGen extends SpriteSourceProvider {
    public AtlasGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void gather() {
        atlas(BLOCKS_ATLAS)
                .addSource(new DirectoryLister("entity/snail", "entity/snail/"));
        atlas(MOB_EFFECTS_ATLAS)
                .addSource(new SingleFile(
                        ResourceLocation.withDefaultNamespace("mob_effect/wither"),
                        Optional.of(PoopSky.loc("death_blight"))));
    }
}
