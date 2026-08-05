package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class GinkgoChestBoatEntity extends ChestBoat {
    public GinkgoChestBoatEntity(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Item getDropItem() {
        return PoItems.GINKGO_CHEST_BOAT.get();
    }
}
