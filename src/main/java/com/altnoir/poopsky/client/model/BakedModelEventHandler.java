package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.ModelEvent;

import java.util.*;

public class BakedModelEventHandler {
    private static final String[] WOOD_SUFFIXES = {"", "_n", "_ns"};
    private static final String[] LAVA_SUFFIXES = {"", "_n", "_ns", "_lava", "_lava_n", "_lava_ns"};

    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        registerAllToiletModels(event, "wooden_toilet", ToiletType.Category.WOOD, false);
        registerAllToiletModels(event, "hard_toilet", ToiletType.Category.HARD, true);
        registerFlyItemModels(event);
    }

    private static void registerAllToiletModels(ModelEvent.RegisterAdditional event, String blockPath, ToiletType.Category category, boolean hasLava) {
        String[] suffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;
        // 注册模板模型（无类型后缀，作为兜底）
        for (String suffix : suffixes) {
            event.register(PoopSky.loc("block/" + blockPath + suffix));
        }
        // 注册每种材质的变种模型
        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            String typeSuffix = "_" + type.id();
            for (String suffix : suffixes) {
                event.register(PoopSky.loc("block/" + blockPath + typeSuffix + suffix));
            }
        }
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        wrapToiletModels(models, PoBlocks.WOODEN_TOILET.getId(), "wooden_toilet", ToiletType.Category.WOOD, false);
        wrapToiletModels(models, PoBlocks.HARD_TOILET.getId(), "hard_toilet", ToiletType.Category.HARD, true);
        wrapToiletItemModel(models, PoBlocks.WOODEN_TOILET.get(), "wooden_toilet", ToiletType.Category.WOOD);
        wrapToiletItemModel(models, PoBlocks.HARD_TOILET.get(), "hard_toilet", ToiletType.Category.HARD);
        wrapFlyItemModel(models);
    }

    private static void registerFlyItemModels(ModelEvent.RegisterAdditional event) {
        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            String flyId = id.equals(FlyTypes.NORMAL.id()) ? "fly" : "fly_" + id;
            event.register(PoopSky.loc("item/" + flyId));
        }
    }

    private static void wrapToiletModels(Map<ResourceLocation, BakedModel> models, ResourceLocation blockId, String blockPath, ToiletType.Category category, boolean hasLava) {
        String[] stateSuffixes = hasLava ? LAVA_SUFFIXES : WOOD_SUFFIXES;

        Map<ToiletType, BakedModel[]> variantModels = new HashMap<>();
        Map<ToiletType, ResourceLocation> variantTextures = new HashMap<>();
        BakedModel[] templateModels = new BakedModel[stateSuffixes.length];
        for (int i = 0; i < stateSuffixes.length; i++) {
            ResourceLocation modelLoc = PoopSky.loc("block/" + blockPath + stateSuffixes[i]);
            ResourceLocation mrl = modelLoc;
            templateModels[i] = models.get(mrl);
        }

        for (ToiletType type : ToiletType.getByCategory(category).values()) {
            String typeSuffix = "_" + type.id();
            BakedModel[] typeModels = new BakedModel[stateSuffixes.length];
            for (int i = 0; i < stateSuffixes.length; i++) {
                ResourceLocation modelLoc = PoopSky.loc("block/" + blockPath + typeSuffix + stateSuffixes[i]);
                ResourceLocation mrl = modelLoc;
                typeModels[i] = models.get(mrl);
            }
            variantModels.put(type, typeModels);
            variantTextures.put(type, toiletTexture(type));
        }

        List<ResourceLocation> toWrap = new ArrayList<>();
        String blockIdStr = blockId.toString();
        for (var entry : models.keySet()) {
            String entryStr = entry.toString();
            if (entryStr.startsWith(blockIdStr + "#") || entryStr.equals(blockIdStr)) {
                toWrap.add(entry);
            }
        }

        for (ResourceLocation key : toWrap) {
            models.compute(key, (k, original) -> new ToiletBakedModel(original, templateModels, variantModels, variantTextures, hasLava));
        }
    }

    private static ResourceLocation toiletTexture(ToiletType toiletType) {
        String tex = toiletType.texture();
        if (tex != null) {
            String namespace = toiletType.sourceBlock() != null
                    ? blockKey(toiletType.sourceBlock()).getNamespace()
                    : PoopSky.MOD_ID;
            return new ResourceLocation(namespace, "block/" + tex);
        }

        Block sourceBlock = toiletType.sourceBlock();
        ResourceLocation key = blockKey(sourceBlock);
        return new ResourceLocation(key.getNamespace(), "block/" + key.getPath());
    }

    private static void wrapFlyItemModel(Map<ResourceLocation, BakedModel> models) {
        Map<String, BakedModel> flyModels = FlyItemBakedModel.collectFlyModels(models);

        if (flyModels.isEmpty()) return;

        ResourceLocation flyModelLoc = PoopSky.loc("item/fly");
        BakedModel defaultFlyModel = models.get(flyModelLoc);
        if (defaultFlyModel == null) return;

        FlyItemBakedModel wrapper = new FlyItemBakedModel(defaultFlyModel, flyModels);

        List<ResourceLocation> toWrap = new ArrayList<>();
        String flyIdStr = BuiltInRegistries.ITEM.getKey(PoItems.FLY.get()).toString();
        for (var entry : models.keySet()) {
            String entryStr = entry.toString();
            if (entryStr.startsWith(flyIdStr + "#") || entryStr.equals(flyIdStr)) {
                toWrap.add(entry);
            }
        }

        for (ResourceLocation key : toWrap) {
            models.put(key, wrapper);
        }
    }

    private static void wrapToiletItemModel(Map<ResourceLocation, BakedModel> models, Block block, String blockPath, ToiletType.Category category) {
        Map<String, BakedModel> typeModels = new LinkedHashMap<>();
        for (var entry : ToiletType.getByCategory(category).entrySet()) {
            String typeId = entry.getKey();
            ResourceLocation variantKey = PoopSky.loc("block/" + blockPath + "_" + typeId);
            BakedModel variantModel = models.get(variantKey);
            if (variantModel != null) {
                typeModels.put(typeId, variantModel);
            }
        }
        if (typeModels.isEmpty()) return;

        ResourceLocation defaultKey = PoopSky.loc("block/" + blockPath);
        BakedModel defaultModel = models.get(defaultKey);
        if (defaultModel == null) return;

        ToiletItemBakedModel wrapper = new ToiletItemBakedModel(defaultModel, typeModels);

        String itemIdStr = BuiltInRegistries.ITEM.getKey(block.asItem()).toString();
        List<ResourceLocation> toWrap = new ArrayList<>();
        for (var entry : models.keySet()) {
            String entryStr = entry.toString();
            if (entryStr.startsWith(itemIdStr + "#inventory")) {
                toWrap.add(entry);
            }
        }
        for (ResourceLocation key : toWrap) {
            models.put(key, wrapper);
        }
    }

    private static ResourceLocation blockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(BakedModelEventHandler::onRegisterAdditional);
        modEventBus.addListener(BakedModelEventHandler::onModifyBakingResult);
    }
}
