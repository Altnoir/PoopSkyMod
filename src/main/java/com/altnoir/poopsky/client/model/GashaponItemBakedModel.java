package com.altnoir.poopsky.client.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.item.p.GashaponItem;
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

public class GashaponItemBakedModel extends BakedModelWrapper<BakedModel> {
    private static final String[] COLORS = {
            GashaponItem.YELLOW,
            GashaponItem.RED,
            GashaponItem.GREEN,
            GashaponItem.BLUE
    };

    private final GashaponItemOverrides overrides;

    public GashaponItemBakedModel(BakedModel defaultModel, Map<String, BakedModel> colorModels) {
        super(defaultModel);
        this.overrides = new GashaponItemOverrides(defaultModel, colorModels);
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    public static Map<String, BakedModel> collectGashaponModels(Map<ModelResourceLocation, BakedModel> models) {
        Map<String, BakedModel> colorModels = new LinkedHashMap<>();
        for (String color : COLORS) {
            ModelResourceLocation modelLoc = new ModelResourceLocation(
                    PoopSky.loc("item/gashapon_" + color),
                    ModelResourceLocation.STANDALONE_VARIANT
            );
            BakedModel baked = models.get(modelLoc);
            if (baked != null) {
                colorModels.put(color, baked);
            }
        }
        return colorModels;
    }

    private static class GashaponItemOverrides extends ItemOverrides {
        private final BakedModel defaultModel;
        private final Map<String, BakedModel> colorModels;

        GashaponItemOverrides(BakedModel defaultModel, Map<String, BakedModel> colorModels) {
            this.defaultModel = defaultModel;
            this.colorModels = colorModels;
        }

        @Override
        public @Nullable BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            String color = stack.get(PoComponents.GASHAPON_COLOR.get());
            if (color != null) {
                BakedModel variant = colorModels.get(color);
                if (variant != null) {
                    return variant;
                }
            }
            return defaultModel;
        }
    }
}