package com.altnoir.poopsky.content.item.p;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class PoBoatItem extends BoatItem {
    private final Supplier<? extends EntityType<? extends Boat>> entityType;

    public PoBoatItem(boolean hasChest, Supplier<? extends EntityType<? extends Boat>> entityType, Properties properties) {
        super(hasChest, Boat.Type.OAK, properties);
        this.entityType = entityType;
    }

    @Override
    protected Boat getBoat(Level level, HitResult hitResult, ItemStack stack, Player player) {
        Boat boat = entityType.get().create(level);

        Vec3 location = hitResult.getLocation();
        boat.setPos(location.x, location.y, location.z);
        if (level instanceof ServerLevel serverLevel) {
            EntityType.<Boat>createDefaultStackConfig(serverLevel, stack, player).accept(boat);
        }
        return boat;
    }
}
