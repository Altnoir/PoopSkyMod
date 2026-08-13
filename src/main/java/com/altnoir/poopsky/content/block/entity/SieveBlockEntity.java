package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.p.SieveBlock;
import com.altnoir.poopsky.content.recipe.SieveRecipe;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SieveBlockEntity extends BlockEntity {
    private static final int INPUT_SLOT = 0;
    private static final int MANUAL_PROGRESS_PER_CLICK = 20;

    private int progress = 0;
    private int maxProgress = MANUAL_PROGRESS_PER_CLICK;
    private boolean autoMode = false;

    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            syncToClient();
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return !resource.isEmpty() && hasRecipe(resource.toStack(1));
        }

        @Override
        protected int getCapacity(int slot, ItemResource resource) {
            return 1;
        }

        @Override
        public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }
    };
    private final ResourceHandler<ItemResource> topSideHandler = new RangedResourceHandler<>(itemHandler, 0, 1) {
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return !resource.isEmpty() && hasRecipe(resource.toStack(1));
        }

        @Override
        public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }
    };
    private final ResourceHandler<ItemResource> bottomHandler = new RangedResourceHandler<>(itemHandler, 0, 1) {
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return false;
        }

        @Override
        public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }
    };

    public SieveBlockEntity(BlockPos pos, BlockState blockState) {
        super(PoBlockEntityType.SIEVE_BLOCK_ENTITY.get(), pos, blockState);
    }
    public boolean tryInsertInput(ItemStack stack) {
        if (level == null || level.isClientSide()) return false;
        if (!getStackInSlot(INPUT_SLOT).isEmpty()) return false;
        if (!isItemValid(INPUT_SLOT, stack)) return false;

        ItemStack toInsert = stack.copyWithCount(1);
        ItemStack remainder = insertItem(INPUT_SLOT, toInsert, false);
        if (remainder.isEmpty()) {
            playInsertEffects(toInsert);
            resetProgress();
            return true;
        }
        return false;
    }

    public void progressManually(Player player) {
        if (level == null || level.isClientSide()) return;

        playManualProgressSound();
        advanceProgress(MANUAL_PROGRESS_PER_CLICK, false);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SieveBlockEntity be) {
        if (level.isClientSide()) return;

        boolean powered = state.getValue(SieveBlock.POWERED);
        be.autoMode = powered;
        if (!powered) return;

        be.advanceProgress(1, true);
    }

    private void completeRecipe() {
        if (level == null || level.isClientSide()) return;

        ItemStack input = getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return;

        Optional<SieveRecipe> recipeOpt = findRecipe(input);
        if (recipeOpt.isEmpty()) return;

        SieveRecipe recipe = recipeOpt.get();
        playCompletionEffects(input);
        consumeInputForRecipe();

        for (ItemStack output : recipe.rollOutputs(level.getRandom())) {
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
        ItemStack input = getStackInSlot(INPUT_SLOT);
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

        BlockState blockState = getInputBlockState(getStackInSlot(INPUT_SLOT));
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
    private static BlockState getInputBlockState(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        return blockItem.getBlock().defaultBlockState();
    }

    private Optional<SieveRecipe> findRecipe(ItemStack stack) {
        if (level == null || stack.isEmpty()) return Optional.empty();
        return level.getServer().getRecipeManager()
                .getRecipeFor(PoRecipes.SIEVE.type().get(), new SingleRecipeInput(stack), level)
                .map(RecipeHolder::value);
    }

    private void consumeInputForRecipe() {
        ItemStack input = getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) {
            return;
        }

        ItemStack remaining = input.copy();
        remaining.shrink(1);
        setStackInSlot(INPUT_SLOT, remaining);
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
            for (int i = 0; i < itemHandler.size(); i++) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getStackInSlot(i));
            }
        }
    }

    public ItemStack getRenderStack() {
        return getStackInSlot(INPUT_SLOT);
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

    public ResourceHandler<ItemResource> getTopSideHandler() {
        return topSideHandler;
    }

    public ResourceHandler<ItemResource> getBottomHandler() {
        return bottomHandler;
    }

    public ItemStack getStackInSlot(int slot) {
        ItemResource resource = itemHandler.getResource(slot);
        long amount = itemHandler.getAmountAsLong(slot);
        return resource.isEmpty() || amount <= 0 ? ItemStack.EMPTY : resource.toStack((int) amount);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            itemHandler.set(slot, ItemResource.EMPTY, 0);
        } else {
            itemHandler.set(slot, ItemResource.of(stack), stack.getCount());
        }
    }

    private ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemResource resource = ItemResource.of(stack);
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = itemHandler.insert(slot, resource, stack.getCount(), tx);
            if (!simulate) tx.commit();
            if (inserted <= 0) return stack;
            ItemStack remainder = stack.copy();
            remainder.shrink(inserted);
            return remainder;
        }
    }

    private boolean isItemValid(int slot, ItemStack stack) {
        return itemHandler.isValid(slot, ItemResource.of(stack));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output.child("inventory"));
        output.putInt("progress", progress);
        output.putInt("maxProgress", maxProgress);
        output.putBoolean("autoMode", autoMode);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("inventory").ifPresent(itemHandler::deserialize);
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getIntOr("maxProgress", MANUAL_PROGRESS_PER_CLICK);
        autoMode = input.getBooleanOr("autoMode", false);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient() {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }
}
