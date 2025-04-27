package com.altnoir.poopsky.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ToiletBlockEntity extends BlockEntity{
    public ToiletBlockEntity(BlockPos pos, BlockState state) {
        super(PSEntities.TOILET_BLOCK_ENTITY, pos, state);
    }

    private BlockPos linkedPos;
    private String linkedDim;

    public String getLinkedDim() {
        return linkedDim;
    }
    public BlockPos getLinkedPos() {
        return linkedPos;
    }

    public void setLinkedPos(BlockPos pos, ServerWorld serverWorld) {
        this.linkedPos = pos;
        this.linkedDim = serverWorld.getRegistryKey().getValue().toString();
        this.markDirty();
    }


    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("LinkedPos")) {
            linkedPos = BlockPos.fromLong(nbt.getLong("LinkedPos"));
            linkedDim = nbt.getString("LinkedDim");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (linkedPos != null) {
            nbt.putLong("LinkedPos", linkedPos.asLong());
            nbt.putString("LinkedDim", linkedDim);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}

