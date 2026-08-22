package com.altnoir.poopsky.content.entity.p;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class PoChestBoatEntity extends ChestBoat {
    private final Item dropItem;

    public PoChestBoatEntity(Item dropItem, EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
        this.dropItem = dropItem;
    }

    @Override
    public Item getDropItem() {
        return dropItem != null ? dropItem : super.getDropItem();
    }
}
