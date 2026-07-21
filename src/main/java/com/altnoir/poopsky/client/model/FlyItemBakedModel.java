package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlyItemBakedModel implements BakedModel {
    private final BakedModel defaultModel;
    private final FlyItemOverrides overrides;

    public FlyItemBakedModel(BakedModel defaultModel, Map<String, BakedModel> flyModels) {
        this.defaultModel = defaultModel;
        this.overrides = new FlyItemOverrides(defaultModel, flyModels);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random, ModelData modelData, @Nullable RenderType renderType) {
        return defaultModel.getQuads(state, face, random, modelData, renderType);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource randomSource) {
        return defaultModel.getQuads(state, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return defaultModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return defaultModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return defaultModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        return defaultModel.getParticleIcon(modelData);
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
        for (String id : FlyType.FLY_TYPES) {
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
