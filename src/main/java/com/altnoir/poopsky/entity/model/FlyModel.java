package com.altnoir.poopsky.entity.model;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.entity.p.FlyEntity;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;

public class FlyModel<T extends FlyEntity> extends BeeModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(PoopSky.loc("fly"), "main");

    public FlyModel(ModelPart root) {
        super(root);
    }
}