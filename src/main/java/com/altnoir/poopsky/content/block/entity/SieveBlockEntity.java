package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.p.SieveBlock;
import com.altnoir.poopsky.content.recipe.SieveRecipe;
import com.altnoir.poopsky.fabric.port.itemhandler.ItemStackHandler;
import com.altnoir.poopsky.fabric.port.itemhandler.RangedWrapper;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SieveBlockEntity extends BlockEntity {
    public static final int INPUT_SLOT = 0;
    private static final int MANUAL_PROGRESS_PER_CLICK = 20;

    private int progress = 0;
    private int maxProgress = MANUAL_PROGRESS_PER_CLICK;
    private boolean autoMode = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            syncToClient();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return hasRecipe(stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };
    private final RangedWrapper topSideHandler = new RangedWrapper(itemHandler, 0, 1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return hasRecipe(stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };
    private final RangedWrapper bottomHandler = new RangedWrapper(itemHandler, 0, 1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };

    public SieveBlockEntity(BlockPos pos, BlockState blockState) {
        super(PoBlockEntityType.SIEVE_BLOCK_ENTITY.get(), pos, blockState);
    }
    public boolean tryInsertInput(ItemStack stack) {
        if (level == null || level.isClientSide) return false;
        if (!itemHandler.getStackInSlot(INPUT_SLOT).isEmpty()) return false;
        if (!itemHandler.isItemValid(INPUT_SLOT, stack)) return false;

        ItemStack toInsert = stack.copyWithCount(1);
        ItemStack remainder = itemHandler.insertItem(INPUT_SLOT, toInsert, false);
        if (remainder.isEmpty()) {
            playInsertEffects(toInsert);
            resetProgress();
            return true;
        }
        return false;
    }

    public void progressManually(Player player) {
        if (level == null || level.isClientSide) return;

        playManualProgressSound();
        advanceProgress(MANUAL_PROGRESS_PER_CLICK, false);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SieveBlockEntity be) {
        if (level.isClientSide) return;

        boolean powered = state.getValue(SieveBlock.POWERED);
        be.autoMode = powered;
        if (!powered) return;

        be.advanceProgress(1, true);
    }

    private void completeRecipe() {
        if (level == null || level.isClientSide) return;

        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return;

        Optional<SieveRecipe> recipeOpt = findRecipe(input);
        if (recipeOpt.isEmpty()) return;

        SieveRecipe recipe = recipeOpt.get();
        playCompletionEffects(input);
        consumeInputForRecipe();

        for (ItemStack output : recipe.rollOutputs(level.random)) {
            ItemEntity itemEntity = new ItemEntity(
                    level,
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D,
                    output.copy());
            itemEntity.setDeltaMovement(0.0D, 0.0D, 0.0D);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }

        resetProgress();
    }

    private void advanceProgress(int amount, boolean automatic) {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        Optional<SieveRecipe> recipe = findRecipe(input);
        if (recipe.isEmpty()) {
            if (progress != 0 || automatic != autoMode) {
                resetProgress();
            }
            return;
        }

        autoMode = automatic;
        maxProgress = recipe.get().processingTime();
        progress = Math.min(progress + amount, maxProgress);

        if (progress >= maxProgress) {
            completeRecipe();
            return;
        }

        setChanged();
        syncToClient();
    }

    private boolean hasRecipe(ItemStack stack) {
        return findRecipe(stack).isPresent();
    }

    private void playInsertEffects(ItemStack stack) {
        BlockState blockState = getInputBlockState(stack);
        if (blockState == null || level == null) {
            return;
        }

        SoundType soundType = blockState.getSoundType(level, worldPosition, null);
        level.playSound(null, worldPosition, soundType.getPlaceSound(), SoundSource.BLOCKS, 1.0F, soundType.getPitch());
    }

    private void playManualProgressSound() {
        if (level == null) {
            return;
        }

        BlockState blockState = getInputBlockState(itemHandler.getStackInSlot(INPUT_SLOT));
        if (blockState == null) {
            return;
        }

        SoundType soundType = blockState.getSoundType(level, worldPosition, null);
        level.playSound(null, worldPosition, soundType.getHitSound(), SoundSource.BLOCKS, 0.5F, soundType.getPitch());
    }

    private void playCompletionEffects(ItemStack stack) {
        BlockState blockState = getInputBlockState(stack);
        if (blockState == null || level == null) {
            return;
        }

        SoundType soundType = blockState.getSoundType(level, worldPosition, null);
        level.playSound(null, worldPosition, soundType.getBreakSound(), SoundSource.BLOCKS, 1.0F, soundType.getPitch());

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.75D,
                    worldPosition.getZ() + 0.5D,
                    12,
                    0.2D,
                    0.2D,
                    0.2D,
                    0.05D
            );
        }
    }

    @Nullable
    private BlockState getInputBlockState(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        return blockItem.getBlock().defaultBlockState();
    }

    private Optional<SieveRecipe> findRecipe(ItemStack stack) {
        if (level == null || stack.isEmpty()) return Optional.empty();
        return level.getRecipeManager()
                .getRecipeFor(PoRecipes.SIEVE.type().get(), new SingleRecipeInput(stack), level)
                .map(RecipeHolder::value);
    }

    private void consumeInputForRecipe() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) {
            return;
        }

        ItemStack remaining = input.copy();
        remaining.shrink(1);
        itemHandler.setStackInSlot(INPUT_SLOT, remaining);
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = MANUAL_PROGRESS_PER_CLICK;
        autoMode = false;
        setChanged();
        syncToClient();
    }

    public void dropContents() {
        if (level != null) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemHandler.getStackInSlot(i));
            }
        }
    }

    public ItemStack getRenderStack() {
        return itemHandler.getStackInSlot(INPUT_SLOT);
    }

    public float getRenderProgress(float partialTick) {
        if (maxProgress <= 0 || getRenderStack().isEmpty()) {
            return 0.0F;
        }

        float interpolatedProgress = progress;
        if (autoMode) {
            interpolatedProgress = Math.min(progress + partialTick, maxProgress);
        }

        return Math.clamp(interpolatedProgress / (float) maxProgress, 0.0F, 1.0F);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public RangedWrapper getTopSideHandler() {
        return topSideHandler;
    }

    public RangedWrapper getBottomHandler() {
        return bottomHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putBoolean("autoMode", autoMode);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        autoMode = tag.getBoolean("autoMode");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient() {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }
}
