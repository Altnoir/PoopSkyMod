package com.altnoir.poopsky.content.entity.p;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class PoBoatEntity extends Boat {
    private final Item dropItem;

    public PoBoatEntity(Item dropItem,EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
        this.dropItem = dropItem;
    }

    @Override
    public Item getDropItem() {
        return dropItem != null ? dropItem : super.getDropItem();
    }
}
