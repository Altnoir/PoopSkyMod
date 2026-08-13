package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.data.ArcadeLootGen;
import com.altnoir.poopsky.impl.network.LightArcadeSyncPacket;
import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArcadeBlockEntity extends BlockEntity {
    private boolean suppressSync;
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!suppressSync) {
                syncToClients();
            }
        }
    };
    private final Map<UUID, Map<String, Integer>> bestScores = new HashMap<>();
    private int rewardCount;

    public ArcadeBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.ARCADE_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean hasCartridge() {
        return !getCartridge().isEmpty();
    }

    public ItemStack getCartridge() {
        return inventory.getStackInSlot(0);
    }

    public boolean insertCartridge(ItemStack stack) {
        if (hasCartridge() || stack.isEmpty() || !(stack.getItem() instanceof GameDiscItem)) {
            return false;
        }

        ItemStack cartridge = stack.copy();
        cartridge.setCount(1);
        inventory.setStackInSlot(0, cartridge);
        return true;
    }

    public ItemStack ejectCartridge() {
        ItemStack cartridge = getCartridge();
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        return cartridge;
    }

    public ItemStack takeCartridge() {
        ItemStack cartridge = getCartridge();
        suppressSync = true;
        try {
            inventory.setStackInSlot(0, ItemStack.EMPTY);
        } finally {
            suppressSync = false;
        }
        return cartridge;
    }

    public int getBestScore(UUID player, String game) {
        return bestScores.getOrDefault(player, Map.of()).getOrDefault(game, 0);
    }

    public int getRewardCount() {
        return rewardCount;
    }

    public void settleGame(ServerPlayer player, String game, int score) {
        int currentScore = Math.max(0, score);

        Map<String, Integer> playerScores = bestScores.computeIfAbsent(player.getUUID(), ignored -> new HashMap<>());
        playerScores.merge(game, currentScore, Math::max);

        rewardCount += currentScore / 5;
        setChanged();
        syncToClients();
    }

    public boolean claimReward(ServerPlayer player) {
        if (rewardCount <= 0) {
            return false;
        }

        rewardCount--;
        spawnReward(player);
        setChanged();
        syncToClients();
        return true;
    }

    private void spawnReward(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        LootTable lootTable = level.getServer().reloadableRegistries()
                .getLootTable(ArcadeLootGen.lootTableKey(getBlockState().getBlock()));

        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);

        Direction facing = getBlockState().getValue(ArcadeBlock.FACING);
        for (ItemStack stack : lootTable.getRandomItems(params)) {
            if (stack.isEmpty()) {
                continue;
            }

            Vec3 position = Vec3.atCenterOf(getBlockPos()).add(
                    facing.getStepX() * 0.7,
                    0.0,
                    facing.getStepZ() * 0.7
            );
            DefaultDispenseItemBehavior.spawnItem(level, stack, 6, facing, position);
        }
        level.playSound(null, getBlockPos(), SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void syncToClients() {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(
                    serverLevel,
                    new ChunkPos(getBlockPos()),
                    new LightArcadeSyncPacket(getBlockPos(), saveWithoutMetadata(serverLevel.registryAccess()))
            );
        }
    }

    public void applyClientData(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("cartridge", inventory.serializeNBT(registries));
        tag.putInt("reward_count", rewardCount);

        CompoundTag scores = new CompoundTag();
        for (Map.Entry<UUID, Map<String, Integer>> playerEntry : bestScores.entrySet()) {
            CompoundTag playerScores = new CompoundTag();
            playerEntry.getValue().forEach(playerScores::putInt);
            scores.put(playerEntry.getKey().toString(), playerScores);
        }
        tag.put("best_scores", scores);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("cartridge")) {
            inventory.deserializeNBT(registries, tag.getCompound("cartridge"));
        }

        rewardCount = tag.getInt("reward_count");
        bestScores.clear();

        CompoundTag scores = tag.getCompound("best_scores");
        for (String playerId : scores.getAllKeys()) {
            CompoundTag playerScores = scores.getCompound(playerId);
            Map<String, Integer> scoresByGame = new HashMap<>();
            for (String game : playerScores.getAllKeys()) {
                scoresByGame.put(game, playerScores.getInt(game));
            }
            bestScores.put(UUID.fromString(playerId), scoresByGame);
        }
    }
}
