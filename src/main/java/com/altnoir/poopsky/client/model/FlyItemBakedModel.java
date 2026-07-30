package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyTypeManager;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlyItemBakedModel extends BakedModelWrapper<BakedModel> {
    private final FlyItemOverrides overrides;

    public FlyItemBakedModel(BakedModel defaultModel, Map<String, BakedModel> flyModels) {
        super(defaultModel);
        this.overrides = new FlyItemOverrides(defaultModel, flyModels);
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    private static class FlyItemOverrides extends ItemOverrides {

        private final BakedModel defaultModel;
        private final Map<String, BakedModel> flyModels;

        FlyItemOverrides(BakedModel defaultModel, Map<String, BakedModel> flyModels) {
            this.defaultModel = defaultModel;
            this.flyModels = flyModels;
        }

        @Override
        public @Nullable BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            String typeId = stack.get(PoComponents.FLY_TYPE.get());
            if (typeId != null) {
                BakedModel variant = flyModels.get(typeId);
                if (variant != null) {
                    return variant;
                }
            }
            return flyModels.getOrDefault(FlyTypes.NORMAL.id(), defaultModel);
        }
    }

    public static Map<String, BakedModel> collectFlyModels(Map<ModelResourceLocation, BakedModel> models) {
        Map<String, BakedModel> flyModels = new LinkedHashMap<>();
        for (String id : FlyTypeManager.INSTANCE.getFlyTypes()) {
            String flyId = id.equals(FlyTypes.NORMAL.id()) ? "fly" : "fly_" + id;
            ModelResourceLocation modelLoc = new ModelResourceLocation(PoopSky.loc("item/" + flyId), ModelResourceLocation.STANDALONE_VARIANT);
            BakedModel baked = models.get(modelLoc);
            if (baked != null) {
                flyModels.put(id, baked);
            }
        }
        return flyModels;
    }
}
