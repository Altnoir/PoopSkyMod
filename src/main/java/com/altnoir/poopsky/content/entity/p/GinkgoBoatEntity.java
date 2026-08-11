package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;

public class GinkgoBoatEntity extends Boat {

    public GinkgoBoatEntity(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level, PoItems.GINKGO_BOAT::get);
    }
}
