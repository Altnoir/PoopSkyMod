package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class PSItemModelProvider extends ItemModelProvider {
    public PSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(PSItems.POOP.get());
        basicItem(PSItems.POOP_BALL.get());
        basicItem(PSItems.POOP_DUMPLINGS.get());
        basicItem(PSItems.TOILET_PLUG.get());
        basicItem(PSItems.SPALL.get());
        basicItem(PSItems.LAWRENCE_MUSIC_DISC.get());
        basicItem(PSItems.TOILET_LINKER.get());
        basicItem(PSItems.URINE_BOTTLE.get());
    }
}
