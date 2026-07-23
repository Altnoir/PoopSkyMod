package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.renderer.ToiletPlugItemRenderer;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoBlocks;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ToiletModelEventHandler {
    private static final String[] WOOD_SUFFIXES = {"", "_n", "_ns"};
    private static final String[] LAVA_SUFFIXES = {"", "_n", "_ns", "_lava", "_lava_n", "_lava_ns"};

    private ToiletModelEventHandler() {
    }

    public static void register() {
        ModelLoadingPlugin.register(context -> {
            List<ResourceLocation> models = new ArrayList<>();
            models.add(ToiletPlugItemRenderer.TWO_DIMENSIONAL_MODEL);
            addAllToiletModels(models, "wooden_toilet", ToiletType.Category.WOOD, false);
            addAllToiletModels(models, "hard_toilet", ToiletType.Category.HARD, true);
            context.addModels(models);
            context.modifyModelAfterBake().register(ModelModifier.WRAP_PHASE, ToiletModelEventHandler::wrapModel);
        });
    }

    private static void addAllToiletModels(
            List<ResourceLocation> models,
            String blockPath,
            ToiletType.Category category,
            boolean hasLava
    ) {
        String[] suffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;
        for (String suffix : suffixes) {
            models.add(PoopSky.loc("block/" + blockPath + suffix));
        }
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            models.add(PoopSky.loc("block/" + blockPath + "_" + type.id()));
        }
    }

    private static @Nullable BakedModel wrapModel(
            @Nullable BakedModel original,
            ModelModifier.AfterBake.Context context
    ) {
        if (original == null || context.topLevelId() == null) return original;

        ResourceLocation modelId = context.topLevelId().id();
        if (modelId.equals(PoBlocks.WOODEN_TOILET.getId())) {
            return createModel(original, context, "wooden_toilet", ToiletType.Category.WOOD, false);
        }
        if (modelId.equals(PoBlocks.HARD_TOILET.getId())) {
            return createModel(original, context, "hard_toilet", ToiletType.Category.HARD, true);
        }
        return original;
    }

    private static BakedModel createModel(
            BakedModel original,
            ModelModifier.AfterBake.Context context,
            String blockPath,
            ToiletType.Category category,
            boolean hasLava
    ) {
        String[] suffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;
        BakedModel[] templateModels = bakeModels(context, blockPath, "", suffixes);
        Map<ToiletType, ResourceLocation> variantTextures = new HashMap<>();

        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            variantTextures.put(type, toiletTexture(type));
        }

        return new ToiletBakedModel(original, templateModels, variantTextures, hasLava);
    }

    private static BakedModel[] bakeModels(
            ModelModifier.AfterBake.Context context,
            String blockPath,
            String typeSuffix,
            String[] stateSuffixes
    ) {
        BakedModel[] models = new BakedModel[stateSuffixes.length];
        for (int i = 0; i < stateSuffixes.length; i++) {
            ResourceLocation modelId = PoopSky.loc("block/" + blockPath + typeSuffix + stateSuffixes[i]);
            models[i] = context.baker().bake(modelId, BlockModelRotation.X0_Y0);
        }
        return models;
    }

    private static ResourceLocation toiletTexture(ToiletType toiletType) {
        String texture = toiletType.texture();
        if (texture != null) {
            String namespace = toiletType.sourceBlock() != null
                    ? blockKey(toiletType.sourceBlock()).getNamespace()
                    : PoopSky.MOD_ID;
            return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + texture);
        }

        ResourceLocation key = blockKey(toiletType.sourceBlock());
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "block/" + key.getPath());
    }

    private static ResourceLocation blockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
