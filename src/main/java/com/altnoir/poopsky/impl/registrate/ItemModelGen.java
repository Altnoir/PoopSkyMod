package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.PFlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
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

public class ItemModelGen extends RegistrateItemModelProvider {
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

    public ItemModelGen(AbstractRegistrate<?> parent, PackOutput output, ExistingFileHelper existingFileHelper) {
        super(parent, output, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generated(PoItems.POOP);
        generated(PoItems.CHILI_POOP);
        generated(PoItems.GOLDEN_POOP);
        generated(PoItems.SEEDBED_CURSE);
        generated(PoItems.FOLIUM_SENNAE);
        generated(PoItems.POOP_BALL);
        generated(PoItems.SAPLING_POOP_BALL);
        generated(PoItems.SEA_POOP_BALL);
        generated(PoItems.WITHER_POOP_BALL);
        generated(PoItems.POOP_BREAD);
        generated(PoItems.POOP_DUMPLINGS);
        generated(PoItems.POOP_MOONCAKE);
        generated(PoItems.CHILI_POOP_MOONCAKE);
        generated(PoItems.GOLDEN_POOP_MOONCAKE);
        generated(PoItems.POOP_SOUP);
        generated(PoItems.POOP_VEGETABLE_STICKS);
        generated(PoItems.POOBURGER_MEAT);
        generated(PoItems.POOBURGER);
        generated(PoItems.POOP_PASTA);
        generated(PoItems.POODDING);
        generated(PoItems.DRAGON_BREATH_CHILI);
        generated(PoItems.KING_OF_DRAGON_FRUIT);
        toiletPlugItem();
        generated(PoItems.SPALL);
        generated(PoItems.LAWRENCE_MUSIC_DISC);
        generated(PoItems.LIGHT_DANCE_MUSIC_DISC);
        generated(PoItems.MOON_BOWL_MUSIC_DISC);
        generated(PoItems.TOILET_PLUG_WAND);
        generated(PoItems.URINE_BOTTLE);
        generated(PoItems.URINE_BUCKET);
        generated(PoItems.MAGGOTS_SEEDS);
        generated(PoItems.ROUNDWORM);
        generated(PoItems.BAKED_MAGGOTS);

        generated(PoItems.FLY_CATCHER);
        generated(PoItems.TIME_BELL);

        wallItem(PoBlocks.CHILI_POOP_WALL, PoBlocks.CHILI_POOP_BLOCK);
        wallItem(PoBlocks.GOLDEN_POOP_WALL, PoBlocks.GOLDEN_POOP_BLOCK);
        wallItem(PoBlocks.POOP_BRICK_WALL, PoBlocks.POOP_BRICKS);
        wallItem(PoBlocks.MOSSY_POOP_BRICK_WALL, PoBlocks.MOSSY_POOP_BRICKS);
        wallItem(PoBlocks.DRIED_POOP_BLOCK_WALL, PoBlocks.DRIED_POOP_BLOCK);
        wallItem(PoBlocks.SMOOTH_POOP_BLOCK_WALL, PoBlocks.SMOOTH_POOP_BLOCK);
        wallItem(PoBlocks.CUT_POOP_BLOCK_WALL, PoBlocks.CUT_POOP_BLOCK);
        wallItem(PoBlocks.TILE_BLOCK_WALL, PoBlocks.TILE_BLOCK);

        withExistingParent(name(PoItems.POOLIME_SPAWN_EGG), mcLoc("item/template_spawn_egg"));
        withExistingParent(name(PoItems.FLY_SPAWN_EGG), mcLoc("item/template_spawn_egg"));

        generated(PoItems.OMINOUS_FILTHY_INGOT);
        generated(PoItems.OMEN_UPGRADE_SMITHING_TEMPLATE);
        bigSowordItem();
        trimmedArmorItem(PoItems.OMEN_HELMET);
        trimmedArmorItem(PoItems.OMEN_CHESTPLATE);
        trimmedArmorItem(PoItems.OMEN_LEGGINGS);
        trimmedArmorItem(PoItems.OMEN_BOOTS);

        flyItemWithOverrides();
        blockItem(PoBlocks.FLY_BARREL);
        blockItem(PoBlocks.BREEDING_CHEST);
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
        this.withExistingParent(name(PoItems.MILOS_SWORD), modLoc("item/big_sword"))
                .texture("layer0", itemTexture(PoItems.MILOS_SWORD));
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
