package com.altnoir.poopsky.content.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public interface PoProjectileItem {
    Projectile asProjectile(Level level, Position position, ItemStack stack, Direction direction);
    static void registerDispense(ItemLike item) {
        DispenserBlock.registerBehavior(item, new AbstractProjectileDispenseBehavior() {
            @Override protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                return ((PoProjectileItem) item.asItem()).asProjectile(level, position, stack, Direction.NORTH);
            }
        });
    }
}
