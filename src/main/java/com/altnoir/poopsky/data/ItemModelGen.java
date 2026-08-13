package com.altnoir.poopsky.data;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.model.FlyTypeItemModelProperty;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.neoforged.neoforge.client.model.item.TrimmedArmorModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ItemModelGen {
    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = new LinkedHashMap<>();

    static {
        TRIM_MATERIALS.put(TrimMaterials.QUARTZ, 0.1F);
        TRIM_MATERIALS.put(TrimMaterials.IRON, 0.2F);
        TRIM_MATERIALS.put(TrimMaterials.NETHERITE, 0.3F);
        TRIM_MATERIALS.put(TrimMaterials.REDSTONE, 0.4F);
        TRIM_MATERIALS.put(TrimMaterials.COPPER, 0.5F);
        TRIM_MATERIALS.put(TrimMaterials.GOLD, 0.6F);
        TRIM_MATERIALS.put(TrimMaterials.EMERALD, 0.7F);
        TRIM_MATERIALS.put(TrimMaterials.DIAMOND, 0.8F);
        TRIM_MATERIALS.put(TrimMaterials.LAPIS, 0.9F);
        TRIM_MATERIALS.put(TrimMaterials.AMETHYST, 1.0F);
    }

    private ItemModelGen() {
    }

    public static void register() {
        PoopSky.registrate().addDataGenerator(ProviderType.ITEM_MODEL, ItemModelGen::generate);
    }

    private static void generate(RegistrateItemModelGenerator prov) {
        flyItem(prov);
        toiletPlugItem(prov);
        parentedItem(prov, PoItems.MILOS_SWORD.get(), PoopSky.loc("item/big_sword"));
        parentedItem(prov, PoItems.FLY_CATCHER.get(), prov.mcLoc("item/handheld_rod"));
        trimmedArmorItem(prov, PoItems.OMEN_HELMET.get(), ArmorType.HELMET);
        trimmedArmorItem(prov, PoItems.OMEN_CHESTPLATE.get(), ArmorType.CHESTPLATE);
        trimmedArmorItem(prov, PoItems.OMEN_LEGGINGS.get(), ArmorType.LEGGINGS);
        trimmedArmorItem(prov, PoItems.OMEN_BOOTS.get(), ArmorType.BOOTS);
        spawnEgg(prov, PoItems.POOLIME_SPAWN_EGG.get());
        spawnEgg(prov, PoItems.FLY_SPAWN_EGG.get());
        prov.generateFlatItem(PoItems.URINE_BUCKET.get(), prov.modItemTexture("urine_bucket"));
    }

    private static void flyItem(RegistrateItemModelGenerator prov) {
        Identifier normalModel = flatModel(prov, "fly", PoopSky.loc("item/fly"));
        List<net.minecraft.client.renderer.item.SelectItemModel.SwitchCase<String>> cases = FlyType.FLY_TYPES.stream()
                .filter(id -> !id.equals(FlyTypes.NORMAL.id()))
                .map(id -> {
                    String flyId = "fly_" + id;
                    Identifier model = flatModel(prov, flyId, PoopSky.loc("item/" + flyId));
                    return ItemModelUtils.when(id, ItemModelUtils.plainModel(model));
                })
                .toList();
        prov.itemModelOutput.accept(PoItems.FLY.get(), ItemModelUtils.select(
                FlyTypeItemModelProperty.INSTANCE,
                ItemModelUtils.plainModel(normalModel),
                cases));
    }

    private static void toiletPlugItem(RegistrateItemModelGenerator prov) {
        Identifier legacyModel = prov.modLoc("item/toilet_plug");
        emit(prov, legacyModel, ItemModelGen::legacyToiletPlugModel);

        Identifier specialBase = prov.modLoc("item/toilet_plug_special");
        emit(prov, specialBase, () -> parentModel(prov.mcLoc("builtin/entity")));
        Identifier guiModel = flatModel(prov, "toilet_plug_gui", PoopSky.loc("item/toilet_plug"));

        ItemModel.Unbaked special = ItemModelUtils.specialModel(specialBase, new ToiletPlugItemRenderer.Unbaked());
        ItemModel.Unbaked flat = ItemModelUtils.plainModel(guiModel);
        prov.itemModelOutput.accept(PoItems.TOILET_PLUG.get(), ItemModelUtils.select(
                new DisplayContext(),
                special,
                ItemModelUtils.when(List.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED), flat)));
    }

    private static void parentedItem(RegistrateItemModelGenerator prov, Item item, Identifier parent) {
        String name = prov.name(() -> item);
        Identifier model = prov.modLoc("item/" + name);
        emit(prov, model, () -> texturedModel(parent, Map.of("layer0", prov.modLoc("item/" + name))));
        prov.createWithExistingModel(item, model);
    }

    private static void spawnEgg(RegistrateItemModelGenerator prov, Item item) {
        Identifier model = prov.modLoc("item/" + prov.name(() -> item));
        emit(prov, model, () -> parentModel(prov.mcLoc("item/template_spawn_egg")));
        prov.createWithExistingModel(item, model);
    }

    private static void trimmedArmorItem(RegistrateItemModelGenerator prov, Item item, ArmorType type) {
        String name = prov.name(() -> item);
        String armorType = switch (type) {
            case HELMET -> "helmet";
            case CHESTPLATE -> "chestplate";
            case LEGGINGS -> "leggings";
            case BOOTS -> "boots";
            default -> throw new IllegalArgumentException("Unsupported humanoid armor type: " + type);
        };
        Identifier baseTexture = prov.modLoc("item/" + name);
        Identifier baseModel = prov.modLoc("item/" + name);
        JsonObject legacyBase = texturedModel(prov.mcLoc("item/generated"), Map.of("layer0", baseTexture));
        JsonArray overrides = new JsonArray();

        for (Map.Entry<ResourceKey<TrimMaterial>, Float> entry : TRIM_MATERIALS.entrySet()) {
            String material = entry.getKey().identifier().getPath();
            Identifier trimModel = prov.modLoc("item/" + name + "_" + material + "_trim");
            Identifier trimTexture = prov.mcLoc("trims/items/" + armorType + "_trim_" + material);
            emit(prov, trimModel, () -> texturedModel(prov.mcLoc("item/generated"), Map.of(
                    "layer0", baseTexture,
                    "layer1", trimTexture)));

            JsonObject predicate = new JsonObject();
            predicate.addProperty("minecraft:trim_type", entry.getValue());
            JsonObject override = new JsonObject();
            override.addProperty("model", trimModel.toString());
            override.add("predicate", predicate);
            overrides.add(override);
        }
        legacyBase.add("overrides", overrides);
        emit(prov, baseModel, () -> legacyBase);

        ItemModel.Unbaked plain = ItemModelUtils.plainModel(baseModel);
        prov.itemModelOutput.accept(item, new TrimmedArmorModel.Unbaked(
                plain,
                prov.mcLoc("trims/items/" + armorType + "_trim")));
    }

    private static Identifier flatModel(RegistrateItemModelGenerator prov, String name, Identifier texture) {
        Identifier model = prov.modLoc("item/" + name);
        ModelTemplates.FLAT_ITEM.create(model, TextureMapping.layer0(new net.minecraft.client.resources.model.sprite.Material(texture)), prov.modelOutput);
        return model;
    }

    private static JsonObject legacyToiletPlugModel() {
        JsonObject result = new JsonObject();
        result.addProperty("loader", "neoforge:separate_transforms");
        result.addProperty("gui_light", "front");
        result.add("base", parentModel(Identifier.withDefaultNamespace("builtin/entity")));
        JsonObject perspectives = new JsonObject();
        for (String context : List.of("fixed", "ground", "gui")) {
            perspectives.add(context, texturedModel(
                    Identifier.withDefaultNamespace("item/generated"),
                    Map.of("layer0", PoopSky.loc("item/toilet_plug"))));
        }
        result.add("perspectives", perspectives);
        return result;
    }

    private static JsonObject parentModel(Identifier parent) {
        JsonObject result = new JsonObject();
        result.addProperty("parent", parent.toString());
        return result;
    }

    private static JsonObject texturedModel(Identifier parent, Map<String, Identifier> textures) {
        JsonObject result = parentModel(parent);
        JsonObject textureJson = new JsonObject();
        textures.forEach((slot, texture) -> textureJson.addProperty(slot, texture.toString()));
        result.add("textures", textureJson);
        return result;
    }

    private static void emit(RegistrateItemModelGenerator prov, Identifier id, ModelInstance model) {
        prov.modelOutput.accept(id, model);
    }
}
