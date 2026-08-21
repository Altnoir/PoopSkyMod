package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.impl.util.PoopTntUtil;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PopTntMinecartEntity extends MinecartTNT {
    public PopTntMinecartEntity(EntityType<? extends PopTntMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return PoBlocks.POOP_TNT.get().defaultBlockState();
    }

    @Override
    protected void explode(double velocity) {
        if (!this.level().isClientSide) {
            PoopTntUtil.triggerExplosion(this, 5 + Math.min(4, (int) (velocity * 2.0)));
        }
        this.discard();
    }
}
