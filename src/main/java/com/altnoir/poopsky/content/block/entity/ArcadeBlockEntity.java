package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.data.ArcadeLootGen;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.game.controls.Button;
import com.altnoir.poopsky.game.util.GameStage;
import com.altnoir.poopsky.impl.network.ArcadeSnapshotPacket;
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
import net.minecraft.world.level.Level;
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
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                refreshSession();
                updateGameState();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final Map<UUID, Map<String, Integer>> bestScores = new HashMap<>();
    private int rewardCount;
    private ServerGame game;
    private int snapshotCooldown;
    private UUID activePlayer;
    private boolean scoreSettled;

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
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        return cartridge;
    }

    public void updateGameState() {
        if (level == null || level.isClientSide) {
            return;
        }
        boolean game = hasCartridge();
        updateGameStateAt(getBlockPos(), game);
        updateGameStateAt(getBlockPos().above(), game);
    }

    private void updateGameStateAt(BlockPos pos, boolean game) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof ArcadeBlock && state.getValue(ArcadeBlock.GAME) != game) {
            level.setBlock(pos, state.setValue(ArcadeBlock.GAME, game), 3);
        }
    }

    public void handleInput(ServerPlayer player, Button button, boolean pressed) {
        if (game == null) {
            refreshSession();
        }
        if (game != null && (activePlayer == null || game.stage == GameStage.START)) {
            activePlayer = player.getUUID();
            scoreSettled = false;
        }
        if (game != null) {
            game.setButton(button, pressed);
        }
    }

    public void resetGame() {
        if (game != null) {
            game.prepare();
            game.settledScore = 0;
            scoreSettled = false;
            snapshotCooldown = 0;
            broadcastSnapshot();
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcadeBlockEntity arcade) {
        arcade.serverTickInternal();
    }

    private void serverTickInternal() {
        if (game == null) {
            return;
        }

        game.tick();
        if (!scoreSettled && (game.stage == GameStage.DIED || game.stage == GameStage.WON)) {
            game.settledScore = game.score;
            settleActiveGame();
        }
        snapshotCooldown--;
        if (snapshotCooldown <= 0) {
            snapshotCooldown = 2;
            broadcastSnapshot();
        }
    }

    private void refreshSession() {
        if (!(getCartridge().getItem() instanceof GameDiscItem disc)) {
            game = null;
            activePlayer = null;
            scoreSettled = false;
            snapshotCooldown = 0;
            return;
        }
        game = ServerGame.create(disc);
        scoreSettled = false;
        game.setSoundEmitter((event, pitch, volume) -> {
            if (level != null) {
                level.playSound(null, getBlockPos().above(), event, SoundSource.BLOCKS, volume, pitch);
            }
        });
        snapshotCooldown = 0;
    }

    private void settleActiveGame() {
        scoreSettled = true;
        if (!(level instanceof ServerLevel serverLevel) || activePlayer == null) {
            return;
        }
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(activePlayer);
        if (player != null) {
            settleGame(player, game.getGameName(), game.score);
        }
    }

    private void broadcastSnapshot() {
        sendStatePacket();
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
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public boolean claimReward(ServerPlayer player) {
        if (rewardCount <= 0) {
            return false;
        }

        rewardCount--;
        spawnReward(player);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
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

    private void sendStatePacket() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        CompoundTag gameSnapshot = game != null ? game.writeSnapshot() : new CompoundTag();
        PacketDistributor.sendToPlayersTrackingChunk(
                serverLevel,
                new ChunkPos(getBlockPos()),
                new ArcadeSnapshotPacket(
                        getBlockPos(),
                        saveWithoutMetadata(serverLevel.registryAccess()),
                        gameSnapshot
                )
        );
    }

    public void applyClientData(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            refreshSession();
            updateGameState();
            broadcastSnapshot();
        }
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
        if (level != null && !level.isClientSide) {
            refreshSession();
            updateGameState();
            broadcastSnapshot();
        }
    }
}