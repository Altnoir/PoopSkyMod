package com.altnoir.poopsky.content.entity.renderer;

import com.altnoir.poopsky.content.entity.p.FlushToiletEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class FlushToiletRenderer extends EntityRenderer<FlushToiletEntity> {
    public FlushToiletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(FlushToiletEntity entity) {
        return MissingTextureAtlasSprite.getLocation();
    }

    @Override
    public boolean shouldRender(FlushToiletEntity livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }
}
