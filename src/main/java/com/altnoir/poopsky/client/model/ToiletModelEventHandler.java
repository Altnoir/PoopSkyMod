package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.block.ToiletType;
import com.altnoir.poopsky.init.PBlocks;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToiletModelEventHandler {
    private static final String[] WOOD_SUFFIXES = {"", "_n", "_ns"};
    private static final String[] LAVA_SUFFIXES = {"", "_n", "_ns", "_lava", "_lava_n", "_lava_ns"};

    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        registerAllToiletModels(event, "wooden_toilet", ToiletType.Category.WOOD, false);
        registerAllToiletModels(event, "hard_toilet", ToiletType.Category.HARD, true);
    }

    private static void registerAllToiletModels(ModelEvent.RegisterAdditional event, String blockPath, ToiletType.Category category, boolean hasLava) {
        String[] suffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;
        // 注册模板模型（无类型后缀，作为兜底）
        for (String suffix : suffixes) {
            event.register(new ModelResourceLocation(
                    PoopSky.loc("block/" + blockPath + suffix),
                    ModelResourceLocation.STANDALONE_VARIANT));
        }
        // 注册每种材质的变种模型
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            String typeSuffix = "_" + type.id();
            for (String suffix : suffixes) {
                event.register(new ModelResourceLocation(
                        PoopSky.loc("block/" + blockPath + typeSuffix + suffix),
                        ModelResourceLocation.STANDALONE_VARIANT));
            }
        }
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        wrapToiletModels(models, PBlocks.WOODEN_TOILET.getId(), "wooden_toilet", ToiletType.Category.WOOD, false);
        wrapToiletModels(models, PBlocks.HARD_TOILET.getId(), "hard_toilet", ToiletType.Category.HARD, true);
    }

    private static void wrapToiletModels(Map<ModelResourceLocation, BakedModel> models, ResourceLocation blockId, String blockPath, ToiletType.Category category, boolean hasLava) {
        String[] stateSuffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;

        Map<ToiletType, BakedModel[]> variantModels = new HashMap<>();
        Map<ToiletType, ResourceLocation> variantTextures = new HashMap<>();
        BakedModel[] templateModels = new BakedModel[stateSuffixes.length];
        for (int i = 0; i < stateSuffixes.length; i++) {
            ResourceLocation modelLoc = PoopSky.loc("block/" + blockPath + stateSuffixes[i]);
            ModelResourceLocation mrl = new ModelResourceLocation(modelLoc, ModelResourceLocation.STANDALONE_VARIANT);
            templateModels[i] = models.get(mrl);
        }

        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            String typeSuffix = "_" + type.id();
            BakedModel[] typeModels = new BakedModel[stateSuffixes.length];
            for (int i = 0; i < stateSuffixes.length; i++) {
                ResourceLocation modelLoc = PoopSky.loc("block/" + blockPath + typeSuffix + stateSuffixes[i]);
                ModelResourceLocation mrl = new ModelResourceLocation(modelLoc, ModelResourceLocation.STANDALONE_VARIANT);
                typeModels[i] = models.get(mrl);
            }
            variantModels.put(type, typeModels);
            variantTextures.put(type, toiletTexture(type));
        }

        List<ModelResourceLocation> toWrap = new ArrayList<>();
        String blockIdStr = blockId.toString();
        for (var entry : models.keySet()) {
            String entryStr = entry.toString();
            if (entryStr.startsWith(blockIdStr + "#") || entryStr.equals(blockIdStr)) {
                toWrap.add(entry);
            }
        }

        for (ModelResourceLocation key : toWrap) {
            models.compute(key, (k, original) -> new ToiletBakedModel(original, templateModels, variantModels, variantTextures, hasLava));
        }
    }

    private static ResourceLocation toiletTexture(ToiletType toiletType) {
        String tex = toiletType.texture();
        if (tex != null) {
            String namespace = toiletType.sourceBlock() != null
                    ? blockKey(toiletType.sourceBlock()).getNamespace()
                    : PoopSky.MOD_ID;
            return ResourceLocation.fromNamespaceAndPath(namespace, "block/" + tex);
        }

        Block sourceBlock = toiletType.sourceBlock();
        ResourceLocation key = blockKey(sourceBlock);
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "block/" + key.getPath());
    }

    private static ResourceLocation blockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ToiletModelEventHandler::onRegisterAdditional);
        modEventBus.addListener(ToiletModelEventHandler::onModifyBakingResult);
    }
}