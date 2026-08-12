package com.altnoir.poopsky.content.item.p;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.BoatItem;

public class GinkgoBoatItem extends BoatItem {
    public GinkgoBoatItem(EntityType<? extends AbstractBoat> entityType, Properties properties) {
        super(entityType, properties);
    }
}
