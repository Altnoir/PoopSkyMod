package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.GachaPool;
import com.altnoir.poopsky.content.block.p.GachaMachineBlock;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GachaMachineBlockEntity extends BlockEntity {
    public static final int ANIMATION_LENGTH = 50;

    private boolean active;
    private int animationTick;
    private String entityId = "";

    public GachaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.GACHA_MACHINE.get(), pos, state);
    }

    public boolean start() {
        if (this.active || this.level == null || this.level.isClientSide) {
            return false;
        }
        ResourceLocation selected = GachaPool.random(this.level.getRandom());
        this.entityId = selected.toString();
        this.animationTick = 0;
        this.active = true;
        setChanged();
        syncToClient();
        return true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GachaMachineBlockEntity blockEntity) {
        if (!blockEntity.active) {
            return;
        }
        blockEntity.animationTick++;
        if (!level.isClientSide && blockEntity.animationTick >= ANIMATION_LENGTH) {
            blockEntity.finish(level, state);
        }
    }

    private void finish(Level level, BlockState state) {
        ResourceLocation selected = ResourceLocation.tryParse(this.entityId);
        if (selected == null || !GachaPool.contains(selected)) {
            selected = GachaPool.random(level.getRandom());
        }
        ItemStack ball = new ItemStack(PoItems.GACHA_BALL.get());
        ball.set(PoComponents.GACHA_ENTITY.get(), selected.toString());
        Direction facing = state.getValue(GachaMachineBlock.FACING);
        ItemEntity itemEntity = new ItemEntity(level,
                this.worldPosition.getX() + 0.5D + facing.getStepX() * 0.7D,
                this.worldPosition.getY() + 0.45D,
                this.worldPosition.getZ() + 0.5D + facing.getStepZ() * 0.7D,
                ball);
        itemEntity.setDeltaMovement(facing.getStepX() * 0.14D, 0.12D, facing.getStepZ() * 0.14D);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
        this.active = false;
        this.animationTick = 0;
        this.entityId = "";
        setChanged();
        syncToClient();
    }

    public boolean isActive() {
        return this.active;
    }

    public float animationProgress(float partialTick) {
        return this.active ? Math.min((this.animationTick + partialTick) / ANIMATION_LENGTH, 1.0F) : 0.0F;
    }

    public ItemStack renderStack() {
        if (!this.active || this.entityId.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack ball = new ItemStack(PoItems.GACHA_BALL.get());
        ball.set(PoComponents.GACHA_ENTITY.get(), this.entityId);
        return ball;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("active", this.active);
        tag.putInt("animation_tick", this.animationTick);
        tag.putString("entity_id", this.entityId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.active = tag.getBoolean("active");
        this.animationTick = tag.getInt("animation_tick");
        this.entityId = tag.getString("entity_id");
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    private void syncToClient() {
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
        }
    }
}
