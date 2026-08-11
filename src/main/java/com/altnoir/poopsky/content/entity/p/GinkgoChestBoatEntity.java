package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.level.Level;

public class GinkgoChestBoatEntity extends ChestBoat {

    public GinkgoChestBoatEntity(EntityType<? extends ChestBoat> entityType, Level level) {
        super(entityType, level, PoItems.GINKGO_CHEST_BOAT::get);
    }
}
