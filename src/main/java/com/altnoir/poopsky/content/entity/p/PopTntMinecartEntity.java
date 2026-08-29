package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.impl.util.PoopTntUtil;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PopTntMinecartEntity extends MinecartTNT {
    public PopTntMinecartEntity(EntityType<? extends PopTntMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return PoBlocks.POP.get().defaultBlockState();
    }

    @Override
    public Item getDropItem() {
        return PoItems.POP_TNT_MINECART.get();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    protected void explode(double velocity) {
        if (!this.level().isClientSide) {
            PoopTntUtil.triggerExplosion(this, 5 + Math.min(4, (int) (velocity * 2.0)));
        }
        this.discard();
    }
}
