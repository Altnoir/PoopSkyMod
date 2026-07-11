package com.altnoir.poopsky.datagen;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.PFlyTypes;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PItems;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;

public class PItemModelProvider extends RegistrateItemModelProvider {
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();

    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

    public PItemModelProvider(AbstractRegistrate<?> parent, PackOutput output, ExistingFileHelper existingFileHelper) {
        super(parent, output, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generated(PItems.POOP);
        generated(PItems.CHILI_POOP);
        generated(PItems.GOLDEN_POOP);
        generated(PItems.SEEDBED_CURSE);
        generated(PItems.FOLIUM_SENNAE);
        generated(PItems.POOP_BALL);
        generated(PItems.SAPLING_POOP_BALL);
        generated(PItems.SEA_POOP_BALL);
        generated(PItems.WITHER_POOP_BALL);
        generated(PItems.POOP_BREAD);
        generated(PItems.POOP_DUMPLINGS);
        generated(PItems.POOP_MOONCAKE);
        generated(PItems.CHILI_POOP_MOONCAKE);
        generated(PItems.GOLDEN_POOP_MOONCAKE);
        generated(PItems.POOP_SOUP);
        generated(PItems.POOP_VEGETABLE_STICKS);
        generated(PItems.POOBURGER_MEAT);
        generated(PItems.POOBURGER);
        generated(PItems.POOP_PASTA);
        generated(PItems.POODDING);
        generated(PItems.DRAGON_BREATH_CHILI);
        generated(PItems.KING_OF_DRAGON_FRUIT);
        toiletPlugItem();
        generated(PItems.SPALL);
        generated(PItems.LAWRENCE_MUSIC_DISC);
        generated(PItems.LIGHT_DANCE_MUSIC_DISC);
        generated(PItems.MOON_BOWL_MUSIC_DISC);
        generated(PItems.TOILET_PLUG_WAND);
        generated(PItems.URINE_BOTTLE);
        generated(PItems.URINE_BUCKET);
        generated(PItems.MAGGOTS_SEEDS);
        generated(PItems.ROUNDWORM);
        generated(PItems.BAKED_MAGGOTS);

        generated(PItems.FLY_CATCHER);
        generated(PItems.TIME_BELL);

        wallItem(PBlocks.CHILI_POOP_WALL, PBlocks.CHILI_POOP_BLOCK);
        wallItem(PBlocks.GOLDEN_POOP_WALL, PBlocks.GOLDEN_POOP_BLOCK);
        wallItem(PBlocks.POOP_BRICK_WALL, PBlocks.POOP_BRICKS);
        wallItem(PBlocks.MOSSY_POOP_BRICK_WALL, PBlocks.MOSSY_POOP_BRICKS);
        wallItem(PBlocks.DRIED_POOP_BLOCK_WALL, PBlocks.DRIED_POOP_BLOCK);
        wallItem(PBlocks.SMOOTH_POOP_BLOCK_WALL, PBlocks.SMOOTH_POOP_BLOCK);
        wallItem(PBlocks.CUT_POOP_BLOCK_WALL, PBlocks.CUT_POOP_BLOCK);
        wallItem(PBlocks.TILE_BLOCK_WALL, PBlocks.TILE_BLOCK);

        withExistingParent(name(PItems.POOLIME_SPAWN_EGG), mcLoc("item/template_spawn_egg"));
        withExistingParent(name(PItems.FLY_SPAWN_EGG), mcLoc("item/template_spawn_egg"));

        generated(PItems.OMINOUS_FILTHY_INGOT);
        generated(PItems.OMEN_UPGRADE_SMITHING_TEMPLATE);
        bigSowordItem();
        trimmedArmorItem(PItems.OMEN_HELMET);
        trimmedArmorItem(PItems.OMEN_CHESTPLATE);
        trimmedArmorItem(PItems.OMEN_LEGGINGS);
        trimmedArmorItem(PItems.OMEN_BOOTS);

        flyItemWithOverrides();
        blockItem(PBlocks.FLY_BARREL);
        blockItem(PBlocks.BREEDING_CHEST);
    }

    private void toiletPlugItem() {
        var baseModel = nested()
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"));

        var guiModel = nested()
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/toilet_plug"));

        getBuilder("toilet_plug")
                .guiLight(GuiLight.FRONT)
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(baseModel)
                .perspective(ItemDisplayContext.GUI, guiModel)
                .perspective(ItemDisplayContext.GROUND, guiModel)
                .perspective(ItemDisplayContext.FIXED, guiModel)
                .end();
    }

    private void bigSowordItem() {
        this.withExistingParent(name(PItems.MILOS_SWORD), modLoc("item/big_sword"))
                .texture("layer0", itemTexture(PItems.MILOS_SWORD));
    }

    private void wallItem(BlockEntry<? extends Block> block, BlockEntry<? extends Block> baseBlock) {
        this.withExistingParent(name(block), mcLoc("block/wall_inventory"))
                .texture("wall", modLoc("block/" + name(baseBlock)));
    }

    private void flyItemWithOverrides() {
        var flyBuilder = getBuilder("fly")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", PoopSky.loc("item/fly"));

        int index = 0;
        for (String id : FlyType.FLY_TYPES) {
            String flyId = id.equals(PFlyTypes.NORMAL.id()) ? "fly" : "fly_" + id;
            getBuilder(flyId)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", PoopSky.loc("item/" + flyId));
            flyBuilder.override()
                    .predicate(PoopSky.loc("fly_type"), (float) index)
                    .model(new ModelFile.UncheckedModelFile(PoopSky.MOD_ID + ":item/" + flyId))
                    .end();
            index++;
        }
    }

    private void trimmedArmorItem(ItemEntry<? extends ArmorItem> itemDeferredItem) {
        ArmorItem armorItem = itemDeferredItem.get();
        trimMaterials.forEach((trimMaterial, value) -> {
            float trimValue = value;

            String armorType = switch (armorItem.getEquipmentSlot()) {
                case HEAD -> "helmet";
                case CHEST -> "chestplate";
                case LEGS -> "leggings";
                case FEET -> "boots";
                default -> "";
            };

            String armorItemPath = name(itemDeferredItem);
            String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
            String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
            ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
            ResourceLocation trimNameResLoc = modLoc(currentTrimName);

            existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

            getBuilder(currentTrimName)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", itemTexture(itemDeferredItem))
                    .texture("layer1", trimResLoc);

            this.withExistingParent(name(itemDeferredItem),
                            mcLoc("item/generated"))
                    .override()
                    .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace() + ":item/" + trimNameResLoc.getPath()))
                    .predicate(mcLoc("trim_type"), trimValue).end()
                    .texture("layer0", itemTexture(itemDeferredItem));
        });
    }
}
