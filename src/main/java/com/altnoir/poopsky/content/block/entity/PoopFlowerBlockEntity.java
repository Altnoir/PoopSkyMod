package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PoopFlowerBlockEntity extends BlockEntity {
    private ItemStack flower = ItemStack.EMPTY;

    public PoopFlowerBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.POOP_FLOWER.get(), pos, state);
    }

    public ItemStack getFlower() {
        return flower;
    }

    public void setFlower(ItemStack stack) {
        flower = stack;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!flower.isEmpty()) tag.put("Flower", flower.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        flower = ItemStack.parseOptional(registries, tag.getCompound("Flower"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
