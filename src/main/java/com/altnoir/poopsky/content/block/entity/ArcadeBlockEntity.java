package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.p.ArcadeBlock;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.data.ArcadeLootGen;
import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.game.GameStage;
import com.altnoir.poopsky.game.ServerGame;
import com.altnoir.poopsky.impl.network.ArcadeGameSnapshotPacket;
import com.altnoir.poopsky.impl.util.DispenseUtil;
import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArcadeBlockEntity extends BlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            if (level != null && !level.isClientSide) {
                refreshSession();
                updateGameState();
                markStatusChanged();
                sendGameSnapshot();
            }
        }
    };
    private final Map<UUID, Map<String, Integer>> bestScores = new HashMap<>();
    private int rewardCount;
    private ServerGame game;
    private int snapshotCooldown;
    private UUID activePlayer;
    private boolean scoreSettled;
    private boolean serverInitialized;

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
        if (game == null) {
            return;
        }
        if (!canControl(player)) {
            return;
        }
        if (activePlayer == null || game.getStage() == GameStage.START) {
            activePlayer = player.getUUID();
            scoreSettled = false;
            markStatusChanged();
        }
        game.setButton(button, pressed);
        if (game.getStage() == GameStage.START) {
            scoreSettled = false;
        }
        if (pressed) {
            snapshotCooldown = 0;
        }
        sendGameSnapshot();
    }

    public void resetGame(ServerPlayer player) {
        if (game != null) {
            if (!canControl(player)) {
                return;
            }
            game.prepare();
            scoreSettled = false;
            snapshotCooldown = 0;
            sendGameSnapshot();
        }
    }

    public void startControl(ServerPlayer player) {
        if (!hasCartridge() || !canControl(player)) {
            return;
        }
        activePlayer = player.getUUID();
        scoreSettled = false;
        snapshotCooldown = 0;
        markStatusChanged();
        if (level != null) {
            level.playSound(null, getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 0.6F);
        }
    }

    public void stopControl(ServerPlayer player) {
        if (activePlayer == null || !activePlayer.equals(player.getUUID())) {
            return;
        }
        activePlayer = null;
        scoreSettled = false;
        markStatusChanged();
        if (level != null) {
            level.playSound(null, getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0F, 0.5F);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcadeBlockEntity arcade) {
        arcade.serverTickInternal();
    }

    private void serverTickInternal() {
        if (level instanceof ServerLevel serverLevel && activePlayer != null) {
            ServerPlayer controlling = serverLevel.getServer().getPlayerList().getPlayer(activePlayer);
            if (controlling != null) {
                controlling.setDeltaMovement(Vec3.ZERO);
            }
        }
        if (game == null) {
            return;
        }

        game.tick();
        if (!scoreSettled && (game.getStage() == GameStage.DIED || game.getStage() == GameStage.WON)) {
            settleActiveGame();
        }
        snapshotCooldown--;
        if (snapshotCooldown <= 0) {
            snapshotCooldown = 2;
            sendGameSnapshot();
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

    private boolean canControl(ServerPlayer player) {
        if (activePlayer == null || activePlayer.equals(player.getUUID())) {
            return true;
        }
        if (level instanceof ServerLevel serverLevel
                && serverLevel.getServer().getPlayerList().getPlayer(activePlayer) == null) {
            activePlayer = player.getUUID();
            markStatusChanged();
            return true;
        }
        return false;
    }

    public boolean isController(Player player) {
        if (activePlayer == null || activePlayer.equals(player.getUUID())) {
            return false;
        }
        return !(player instanceof ServerPlayer serverPlayer) || !canControl(serverPlayer);
    }

    private void initializeServer() {
        if (level == null || level.isClientSide || serverInitialized) {
            return;
        }
        serverInitialized = true;
        refreshSession();
        updateGameState();
        markStatusChanged();
        sendGameSnapshot();
    }

    private void settleActiveGame() {
        scoreSettled = true;
        if (!(level instanceof ServerLevel serverLevel) || activePlayer == null) {
            return;
        }
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(activePlayer);
        if (player != null) {
            settleGame(player, game.getGameName(), game.getScore());
        }
    }

    public int getBestScore(UUID player, String game) {
        return bestScores.getOrDefault(player, Map.of()).getOrDefault(game, 0);
    }

    public int getRewardCount() {
        return rewardCount;
    }

    private void settleGame(ServerPlayer player, String game, int score) {
        int currentScore = Math.max(0, score);

        Map<String, Integer> playerScores = bestScores.computeIfAbsent(player.getUUID(), ignored -> new HashMap<>());
        playerScores.merge(game, currentScore, Math::max);

        rewardCount += currentScore / 5;
        markStatusChanged();
    }

    public boolean claimReward(ServerPlayer player) {
        if (!(level instanceof ServerLevel) || rewardCount <= 0 || isController(player)) {
            return false;
        }

        List<ItemStack> rewards = rollRewards();
        rewardCount--;
        markStatusChanged();
        spawnRewards(rewards);
        return true;
    }

    private List<ItemStack> rollRewards() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }
        LootTable lootTable = serverLevel.getServer().reloadableRegistries()
                .getLootTable(ArcadeLootGen.lootTableKey(getBlockState().getBlock()));
        LootParams params = new LootParams.Builder(serverLevel).create(LootContextParamSets.EMPTY);
        return lootTable.getRandomItems(params).stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    private void spawnRewards(List<ItemStack> rewards) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Direction facing = getBlockState().getValue(ArcadeBlock.FACING);
        for (ItemStack stack : rewards) {
            DispenseUtil.spawnItem(serverLevel, stack, 0.1, facing, getBlockPos());
        }
    }

    private void markStatusChanged() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private void sendGameSnapshot() {
        if (!(level instanceof ServerLevel serverLevel) || game == null) {
            return;
        }
        PacketDistributor.sendToPlayersTrackingChunk(
                serverLevel,
                new ChunkPos(getBlockPos()),
                new ArcadeGameSnapshotPacket(
                        getBlockPos(),
                        game.writeSnapshot()
                )
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();
        initializeServer();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = saveWithoutMetadata(registries);
        if (activePlayer != null) {
            tag.putString("active_player", activePlayer.toString());
        }
        return tag;
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
        activePlayer = tag.contains("active_player") ? UUID.fromString(tag.getString("active_player")) : null;

        CompoundTag scores = tag.getCompound("best_scores");
        for (String playerId : scores.getAllKeys()) {
            CompoundTag playerScores = scores.getCompound(playerId);
            Map<String, Integer> scoresByGame = new HashMap<>();
            for (String game : playerScores.getAllKeys()) {
                scoresByGame.put(game, playerScores.getInt(game));
            }
            bestScores.put(UUID.fromString(playerId), scoresByGame);
        }
        initializeServer();
    }
}
