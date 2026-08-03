package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.init.PoComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ToiletItemBakedModel extends DelegatingBakedModel {
    private final ToiletItemOverrides overrides;

    public ToiletItemBakedModel(BakedModel defaultModel, Map<String, BakedModel> typeModels) {
        super(defaultModel);
        this.overrides = new ToiletItemOverrides(defaultModel, typeModels);
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
