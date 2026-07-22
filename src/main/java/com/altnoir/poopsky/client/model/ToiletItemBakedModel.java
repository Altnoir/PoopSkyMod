package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ToiletItemBakedModel implements BakedModel {
    private final BakedModel defaultModel;
    private final ToiletItemOverrides overrides;

    public ToiletItemBakedModel(BakedModel defaultModel, Map<String, BakedModel> typeModels) {
        this.defaultModel = defaultModel;
        this.overrides = new ToiletItemOverrides(defaultModel, typeModels);
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
        return defaultModel.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData modelData) {
        return defaultModel.getParticleIcon(modelData);
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    private static class ToiletItemOverrides extends ItemOverrides {
        private final BakedModel defaultModel;
        private final Map<String, BakedModel> typeModels;

        ToiletItemOverrides(BakedModel defaultModel, Map<String, BakedModel> typeModels) {
            this.defaultModel = defaultModel;
            this.typeModels = typeModels;
        }

        @Override
        public @Nullable BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            ToiletType type = stack.get(PoComponents.TOILET_TYPE.get());
            if (type != null) {
                BakedModel variant = typeModels.get(type.id());
                if (variant != null) {
                    return variant;
                }
            }
            return defaultModel;
        }
    }
}
