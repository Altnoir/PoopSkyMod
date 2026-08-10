package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.p.GachaMachineBlock;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class GachaMachineBlockEntity extends BlockEntity {
    public static final int ANIMATION_LENGTH = 50;
    private static final ResourceKey<LootTable> REWARD_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE, PoopSky.loc("gameplay/gacha_machine"));

    public enum StartResult {
        STARTED,
        BUSY,
        INVALID_REWARD
    }

    private boolean active;
    private int animationTick;
    private ItemStack rewardStack = ItemStack.EMPTY;

    public GachaMachineBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.GACHA_MACHINE.get(), pos, state);
    }

    public StartResult start(Player player) {
        if (this.active) {
            return StartResult.BUSY;
        }
        if (!(this.level instanceof ServerLevel level)) {
            return StartResult.INVALID_REWARD;
        }
        ItemStack reward = drawReward(level, player);
        if (!isValidReward(reward)) {
            return StartResult.INVALID_REWARD;
        }
        this.rewardStack = reward;
        this.animationTick = 0;
        this.active = true;
        setChanged();
        syncToClient();
        return StartResult.STARTED;
    }

    private ItemStack drawReward(ServerLevel level, Player player) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(REWARD_TABLE);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, this.worldPosition.getCenter())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.CHEST);
        for (ItemStack reward : table.getRandomItems(params)) {
            if (reward.is(PoItems.GACHA_BALL.get()) && isValidEntityId(reward.get(PoComponents.GACHA_ENTITY.get()))) {
                return reward.copyWithCount(1);
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean isValidEntityId(String id) {
        if (id == null) {
            return false;
        }
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return false;
        }
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(location).orElse(null);
        return entityType != null && entityType.canSummon();
    }

    private static boolean isValidReward(ItemStack reward) {
        return reward.is(PoItems.GACHA_BALL.get())
                && isValidEntityId(reward.get(PoComponents.GACHA_ENTITY.get()));
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
        if (!isValidReward(this.rewardStack)) {
            this.active = false;
            this.animationTick = 0;
            this.rewardStack = ItemStack.EMPTY;
            setChanged();
            syncToClient();
            return;
        }
        Direction facing = state.getValue(GachaMachineBlock.FACING);
        ItemEntity itemEntity = new ItemEntity(level,
                this.worldPosition.getX() + 0.5D + facing.getStepX() * 0.7D,
                this.worldPosition.getY() + 0.45D,
                this.worldPosition.getZ() + 0.5D + facing.getStepZ() * 0.7D,
                this.rewardStack.copyWithCount(1));
        itemEntity.setDeltaMovement(facing.getStepX() * 0.14D, 0.12D, facing.getStepZ() * 0.14D);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
        this.active = false;
        this.animationTick = 0;
        this.rewardStack = ItemStack.EMPTY;
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
        if (!this.active || this.rewardStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.rewardStack.copy();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("active", this.active);
        tag.putInt("animation_tick", this.animationTick);
        CompoundTag rewardTag = new CompoundTag();
        NonNullList<ItemStack> rewards = NonNullList.withSize(1, this.rewardStack);
        ContainerHelper.saveAllItems(rewardTag, rewards, registries);
        tag.put("reward", rewardTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.active = tag.getBoolean("active");
        this.animationTick = tag.getInt("animation_tick");
        NonNullList<ItemStack> rewards = NonNullList.withSize(1, ItemStack.EMPTY);
        if (tag.contains("reward")) {
            ContainerHelper.loadAllItems(tag.getCompound("reward"), rewards, registries);
        }
        this.rewardStack = rewards.get(0);
        if (this.rewardStack.isEmpty()) {
            String legacyEntityId = tag.getString("entity_id");
            if (!legacyEntityId.isEmpty() && isValidEntityId(legacyEntityId)) {
                this.rewardStack = new ItemStack(PoItems.GACHA_BALL.get());
                this.rewardStack.set(PoComponents.GACHA_ENTITY.get(), legacyEntityId);
            }
        }
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
