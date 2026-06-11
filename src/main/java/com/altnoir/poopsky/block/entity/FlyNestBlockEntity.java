package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.entity.p.FlyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FlyNestBlockEntity extends BeehiveBlockEntity {
    public FlyNestBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    public void addOccupant(Entity occupant) {
        if (occupant instanceof FlyEntity) {
            super.addOccupant(occupant);
        }
    }
}
