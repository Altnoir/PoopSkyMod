package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BakedModelEventHandler {
    private static final String[] WOOD_SUFFIXES = {"", "_n", "_ns"};
    private static final String[] LAVA_SUFFIXES = {"", "_n", "_ns", "_lava", "_lava_n", "_lava_ns"};
    private static final List<String> SHIT_MODEL_PATHS = List.of("shit", "chili_shit", "golden_shit");

    private BakedModelEventHandler() {
    }

    public static void register() {
        ModelLoadingPlugin.register(context -> {
            List<ResourceLocation> models = new ArrayList<>();
            models.add(ToiletPlugItemRenderer.TWO_DIMENSIONAL_MODEL);
            addAllToiletModels(models, "wooden_toilet", ToiletType.Category.WOOD, false);
            addAllToiletModels(models, "hard_toilet", ToiletType.Category.HARD, true);
            addFlyItemModels(models);
            addContextualShitModels(models);
            context.addModels(models);
            context.modifyModelAfterBake().register(ModelModifier.WRAP_PHASE, BakedModelEventHandler::wrapModel);
        });
    }

    private static void addAllToiletModels(List<ResourceLocation> models, String blockPath,
                                            ToiletType.Category category, boolean hasLava) {
        String[] suffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;
        for (String suffix : suffixes) {
            models.add(PoopSky.loc("block/" + blockPath + suffix));
        }
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            for (String suffix : suffixes) {
                models.add(PoopSky.loc("block/" + blockPath + "_" + type.id() + suffix));
            }
        }
    }

    private static void addFlyItemModels(List<ResourceLocation> models) {
        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            String flyId = id.equals(FlyTypes.NORMAL.id()) ? "fly" : "fly_" + id;
            models.add(PoopSky.loc("item/" + flyId));
        }
    }

    private static void addContextualShitModels(List<ResourceLocation> models) {
        for (String path : SHIT_MODEL_PATHS) {
            models.add(PoopSky.loc("item/" + path + "_flat"));
            models.add(PoopSky.loc("block/" + path));
        }
    }

    private static @Nullable BakedModel wrapModel(@Nullable BakedModel original,
                                                   ModelModifier.AfterBake.Context context) {
        if (original == null || context.topLevelId() == null) {
            return original;
        }

        ResourceLocation modelId = context.topLevelId().id();
        boolean inventoryModel = context.topLevelId().toString().endsWith("#inventory");
        if (modelId.equals(PoBlocks.WOODEN_TOILET.getId())) {
            return inventoryModel
                    ? createToiletItemModel(original, context, "wooden_toilet", ToiletType.Category.WOOD)
                    : createBlockModel(original, context, "wooden_toilet", ToiletType.Category.WOOD, false);
        }
        if (modelId.equals(PoBlocks.HARD_TOILET.getId())) {
            return inventoryModel
                    ? createToiletItemModel(original, context, "hard_toilet", ToiletType.Category.HARD)
                    : createBlockModel(original, context, "hard_toilet", ToiletType.Category.HARD, true);
        }
        if (modelId.equals(BuiltInRegistries.ITEM.getKey(PoItems.FLY.get()))) {
            return createFlyItemModel(original, context);
        }
        return original;
    }

    public static @Nullable BakedModel getContextualShitModel(String path, boolean blockModel) {
        String modelPath = blockModel ? "block/" + path : "item/" + path + "_flat";
        FabricBakedModelManager modelManager = (FabricBakedModelManager) Minecraft.getInstance().getModelManager();
        return modelManager.getModel(PoopSky.loc(modelPath));
    }

    private static BakedModel createBlockModel(BakedModel original, ModelModifier.AfterBake.Context context,
                                                String blockPath, ToiletType.Category category, boolean hasLava) {
        String[] suffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;
        BakedModel[] templateModels = bakeModels(context, blockPath, "", suffixes);
        Map<ToiletType, BakedModel[]> variantModels = new HashMap<>();
        Map<ToiletType, ResourceLocation> variantTextures = new HashMap<>();
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            variantModels.put(type, bakeModels(context, blockPath, "_" + type.id(), suffixes));
            variantTextures.put(type, toiletTexture(type));
        }
        return new ToiletBakedModel(original, templateModels, variantModels, variantTextures, hasLava);
    }

    private static BakedModel createToiletItemModel(BakedModel original, ModelModifier.AfterBake.Context context,
                                                     String blockPath, ToiletType.Category category) {
        Map<String, BakedModel> typeModels = new LinkedHashMap<>();
        for (var entry : ToiletType.getByCategory(category).entrySet()) {
            BakedModel model = context.baker().bake(
                    PoopSky.loc("block/" + blockPath + "_" + entry.getKey()), BlockModelRotation.X0_Y0);
            if (model != null) {
                typeModels.put(entry.getKey(), model);
            }
        }
        return new ToiletItemBakedModel(original, typeModels);
    }

    private static BakedModel createFlyItemModel(BakedModel original, ModelModifier.AfterBake.Context context) {
        Map<String, BakedModel> flyModels = new LinkedHashMap<>();
        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            String flyId = id.equals(FlyTypes.NORMAL.id()) ? "fly" : "fly_" + id;
            BakedModel model = context.baker().bake(PoopSky.loc("item/" + flyId), BlockModelRotation.X0_Y0);
            if (model != null) {
                flyModels.put(id, model);
            }
        }
        return new FlyItemBakedModel(original, flyModels);
    }

    private static BakedModel[] bakeModels(ModelModifier.AfterBake.Context context, String blockPath,
                                            String typeSuffix, String[] stateSuffixes) {
        BakedModel[] models = new BakedModel[stateSuffixes.length];
        for (int i = 0; i < stateSuffixes.length; i++) {
            models[i] = context.baker().bake(
                    PoopSky.loc("block/" + blockPath + typeSuffix + stateSuffixes[i]),
                    BlockModelRotation.X0_Y0);
        }
        return models;
    }

    private static ResourceLocation toiletTexture(ToiletType type) {
        String texture = type.texture();
        if (texture != null) {
            String namespace = type.sourceBlock() != null
                    ? blockKey(type.sourceBlock()).getNamespace()
                    : PoopSky.MOD_ID;
            return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + texture);
        }
        ResourceLocation key = blockKey(type.sourceBlock());
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "block/" + key.getPath());
    }

    private static ResourceLocation blockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
