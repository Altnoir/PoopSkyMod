package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class PSItemModelProvider extends ItemModelProvider {
    public PSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PoopSky.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(PSItems.POOP.get());
        basicItem(PSItems.CHILI_POOP.get());
        basicItem(PSItems.POOP_BALL.get());
        basicItem(PSItems.SAPING_BALL.get());
        basicItem(PSItems.WITHER_POOP_BALL.get());
        basicItem(PSItems.POOP_BREAD.get());
        basicItem(PSItems.POOP_DUMPLINGS.get());
        basicItem(PSItems.POOP_SOUP.get());
        basicItem(PSItems.POOP_VEGETABLE_STICKS.get());
        basicItem(PSItems.POOBURGER_MEAT.get());
        basicItem(PSItems.POOBURGER.get());
        basicItem(PSItems.POOP_PASTA.get());
        basicItem(PSItems.POODDING.get());
        basicItem(PSItems.DRAGON_BREATH_CHILI.get());
        basicItem(PSItems.TOILET_PLUG.get());
        basicItem(PSItems.SPALL.get());
        basicItem(PSItems.LAWRENCE_MUSIC_DISC.get());
        basicItem(PSItems.LIGHT_DANCE_MUSIC_DISC.get());
        basicItem(PSItems.MOON_BOWL_MUSIC_DISC.get());
        basicItem(PSItems.TOILET_PLUG_WAND.get());
        basicItem(PSItems.URINE_BOTTLE.get());
        basicItem(PSItems.POOP_BUCKET.get());
        basicItem(PSItems.MAGGOTS_SEEDS.get());
        basicItem(PSItems.ROUNDWORM.get());
        basicItem(PSItems.BAKED_MAGGOTS.get());

        basicItem(PSItems.TIME_BELL.get());

        wallItem(PSBlocks.CHILI_POOP_WALL, PSBlocks.CHILI_POOP_BLOCK);
        wallItem(PSBlocks.POOP_BRICK_WALL,PSBlocks.POOP_BRICKS);
        wallItem(PSBlocks.MOSSY_POOP_BRICK_WALL,PSBlocks.MOSSY_POOP_BRICKS);
        wallItem(PSBlocks.DRIED_POOP_BLOCK_WALL, PSBlocks.DRIED_POOP_BLOCK);
        wallItem(PSBlocks.SMOOTH_POOP_BLOCK_WALL, PSBlocks.SMOOTH_POOP_BLOCK);
        wallItem(PSBlocks.CUT_POOP_BLOCK_WALL, PSBlocks.CUT_POOP_BLOCK);
        wallItem(PSBlocks.TILE_BLOCK_WALL, PSBlocks.TILE_BLOCK);
        
        // 添加液体桶的模型
        withExistingParent("poop_bucket", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/poop_bucket"));

        withExistingParent(PSItems.POOLIME_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }

    private void wallItem(DeferredBlock<?>block, DeferredBlock<?> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "block/" + baseBlock.getId().getPath()));
    }
}