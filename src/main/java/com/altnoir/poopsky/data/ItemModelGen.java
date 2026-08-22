package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoItems;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
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

    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(PoopSky.registrate(), output, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        flyItem();
        gachaponItem();
        toiletPlugItem();
        bigSowordItem();
        flyCatcherItem();
        trimmedArmorItem(PoItems.OMEN_HELMET);
        trimmedArmorItem(PoItems.OMEN_CHESTPLATE);
        trimmedArmorItem(PoItems.OMEN_LEGGINGS);
        trimmedArmorItem(PoItems.OMEN_BOOTS);
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

    private void gachaponItem() {
        for (String color : new String[]{"yellow", "red", "blue"}) {
            gachaponItemModel("gachapon_" + color);
        }
        gachaponMainModel();
    }

    private void gachaponItemModel(String modelName) {
        var base = nested()
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", PoopSky.loc("item/" + modelName));
        var entityModel = nested()
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"));

        getBuilder(modelName)
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(base)
                .perspective(ItemDisplayContext.GROUND, entityModel)
                .end()
                .guiLight(GuiLight.FRONT);
    }

    private void gachaponMainModel() {
        var base = nested()
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", PoopSky.loc("item/gachapon"));
        var entityModel = nested()
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"));

        var builder = getBuilder("gachapon")
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(base)
                .perspective(ItemDisplayContext.GROUND, entityModel)
                .end()
                .guiLight(GuiLight.FRONT);

        int[] modelData = {1, 2, 3};
        String[] colors = {"yellow", "red", "blue"};
        for (int i = 0; i < colors.length; i++) {
            builder.override()
                    .predicate(PoopSky.mcloc("custom_model_data"), modelData[i])
                    .model(new ModelFile.UncheckedModelFile(PoopSky.loc("item/gachapon_" + colors[i])))
                    .end();
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
            ResourceLocation trimResLoc = PoopSky.parse(trimPath); // minecraft namespace
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