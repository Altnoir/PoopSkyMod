package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.impl.util.PoopTntUtil;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class PopTntMinecartEntity extends MinecartTNT {
    public PopTntMinecartEntity(EntityType<? extends PopTntMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return PoBlocks.POOP_TNT.get().defaultBlockState();
    }

    @Override
    protected void explode(@Nullable DamageSource damageSource, double speedSqr) {
        if (!this.level().isClientSide()) {
            double speed = Math.sqrt(speedSqr);
            PoopTntUtil.triggerExplosion(this, 5 + Math.min(4, (int) (speed * 2.0)));
        }
        this.discard();
    }
}