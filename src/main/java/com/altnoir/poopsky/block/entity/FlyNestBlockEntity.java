package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.entity.p.FlyEntity;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class FlyNestBlockEntity extends BeehiveBlockEntity {
    public FlyNestBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    public void addOccupant(Entity occupant) {
        if (occupant instanceof FlyEntity) {
            occupant.stopRiding();
            occupant.ejectPassengers();
            this.storeBee(BeehiveBlockEntity.Occupant.of(occupant));
            if (this.level != null) {
                BlockPos pos = this.getBlockPos();
                this.level.playSound(null, pos, PSoundEvents.BLOCK_FLY_NEST_ENTER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(occupant, this.getBlockState()));
            }

            occupant.discard();
            super.setChanged();
        }
    }
}
