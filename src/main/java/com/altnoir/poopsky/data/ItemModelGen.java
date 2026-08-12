package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;

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

    public ItemModelGen(PackOutput output) {
        super(PoopSky.registrate(), output);
    }

    @Override
    protected void registerModels() {
        flyItem();
        toiletPlugItem();
        bigSowordItem();
        flyCatcherItem();
        trimmedArmorItem(PoItems.OMEN_HELMET, ArmorType.HELMET);
        trimmedArmorItem(PoItems.OMEN_CHESTPLATE, ArmorType.CHESTPLATE);
        trimmedArmorItem(PoItems.OMEN_LEGGINGS, ArmorType.LEGGINGS);
        trimmedArmorItem(PoItems.OMEN_BOOTS, ArmorType.BOOTS);
        withExistingParent(name(PoItems.POOLIME_SPAWN_EGG), mcLoc("item/template_spawn_egg"));
        withExistingParent(name(PoItems.FLY_SPAWN_EGG), mcLoc("item/template_spawn_egg"));
        generated(PoItems.URINE_BUCKET);
    }

    private void toiletPlugItem() {
        var baseModel = nested()
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"));

        var guiModel = nested()
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/toilet_plug"));

        getBuilder("toilet_plug")
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(baseModel)
                .perspective(ItemDisplayContext.GUI, guiModel)
                .perspective(ItemDisplayContext.GROUND, guiModel)
                .perspective(ItemDisplayContext.FIXED, guiModel)
                .end()
                .guiLight(GuiLight.FRONT);
    }

    private void bigSowordItem() {
        this.withExistingParent(name(PoItems.MILOS_SWORD), modLoc("item/big_sword"))
                .texture("layer0", itemTexture(PoItems.MILOS_SWORD));
    }

    private void flyCatcherItem() {
        this.withExistingParent(name(PoItems.FLY_CATCHER), mcLoc("item/handheld_rod"))
                .texture("layer0", itemTexture(PoItems.FLY_CATCHER));
    }

    private void flyItem() {
        getBuilder("fly")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", PoopSky.loc("item/fly"));

        for (String id : FlyType.FLY_TYPES) {
            if (id.equals(FlyTypes.NORMAL.id())) continue;
            String flyId = "fly_" + id;
            getBuilder(flyId)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", PoopSky.loc("item/" + flyId));
        }
    }

    private void trimmedArmorItem(ItemEntry<? extends Item> itemDeferredItem, ArmorType type) {
        trimMaterials.forEach((trimMaterial, value) -> {
            float trimValue = value;

            String armorType = switch (type) {
                case HELMET -> "helmet";
                case CHESTPLATE -> "chestplate";
                case LEGGINGS -> "leggings";
                case BOOTS -> "boots";
                default -> throw new IllegalArgumentException("Unsupported humanoid armor type: " + type);
            };

            String armorItemPath = name(itemDeferredItem);
            String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.identifier().getPath();
            String currentTrimName = armorItemPath + "_" + trimMaterial.identifier().getPath() + "_trim";
            Identifier trimResLoc = Identifier.parse(trimPath); // minecraft namespace
            Identifier trimNameResLoc = modLoc(currentTrimName);

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
