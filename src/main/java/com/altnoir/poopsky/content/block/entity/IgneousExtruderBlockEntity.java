package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.recipe.IgneousExtruderRecipe;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IgneousExtruderBlockEntity extends BlockEntity implements Overclockable {
    private static final int TICKS_PER_OPERATION = 30;
    private static final String DISPLAY_STACK_TAG = "display_stack";
    private static final String PENDING_OUTPUT_TAG = "pending_output";
    private static final Comparator<RecipeHolder<IgneousExtruderRecipe>> RECIPE_ORDER = Comparator
            .comparing((RecipeHolder<IgneousExtruderRecipe> recipe) -> recipe.value().below().isEmpty())
            .thenComparing(recipe -> recipe.id().toString());

    private final OverclockState overclockState = new OverclockState();
    private @Nullable RecipeHolder<IgneousExtruderRecipe> activeRecipe;
    private boolean recipeInputsDirty = true;
    private ItemStack displayStack = ItemStack.EMPTY;
    private ItemStack pendingOutput = ItemStack.EMPTY;
    private int progress;

    @Override
    public OverclockState getOverclockState() {
        return overclockState;
    }

    public IgneousExtruderBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.IGNEOUS_EXTRUDER.get(), pos, state);
    }

    public static void tick(Level level, IgneousExtruderBlockEntity blockEntity) {
        blockEntity.tick(level);
    }

    private static @Nullable Match findMatch(RecipeManager recipes, IgneousExtruderRecipe.Input input) {
        Match best = null;
        for (var recipe : recipes.getAllRecipesFor(PoRecipes.IGNEOUS_EXTRUDER.type().get())) {
            if (best != null && RECIPE_ORDER.compare(recipe, best.recipe()) >= 0) {
                continue;
            }
            int pairs = recipe.value().matchingPairs(input);
            if (pairs > 0) {
                best = new Match(recipe, pairs);
            }
        }
        return best;
    }

    private static boolean hasSpace(IItemHandler output) {
        for (int slot = 0; slot < output.getSlots(); slot++) {
            ItemStack stack = output.getStackInSlot(slot);
            int limit = output.getSlotLimit(slot);
            if (stack.getCount() >= limit) {
                continue;
            }
            if (stack.isEmpty() || output.insertItem(slot, stack.copyWithCount(1), true).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean canInsert(IItemHandler output, ItemStack result) {
        return ItemHandlerHelper.insertItem(output, result, true).isEmpty();
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public void invalidateRecipeInputs() {
        recipeInputsDirty = true;
    }

    public void dropPendingOutput(Level level) {
        if (pendingOutput.isEmpty()) {
            return;
        }
        ItemStack stack = pendingOutput;
        pendingOutput = ItemStack.EMPTY;
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put(DISPLAY_STACK_TAG, displayStack.saveOptional(registries));
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        displayStack = ItemStack.parseOptional(registries, tag.getCompound(DISPLAY_STACK_TAG));
        pendingOutput = ItemStack.parseOptional(registries, tag.getCompound(PENDING_OUTPUT_TAG));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!pendingOutput.isEmpty()) {
            tag.put(PENDING_OUTPUT_TAG, pendingOutput.save(registries));
        }
    }

    private void updateDisplayStack(Level level, ItemStack result) {
        if (ItemStack.matches(displayStack, result)) {
            return;
        }
        displayStack = result.copy();
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
    }

    private void tick(Level level) {
        BlockPos above = worldPosition.above();
        IItemHandler output = null;
        if (!level.getBlockState(above).isAir()) {
            output = level.getCapability(Capabilities.ItemHandler.BLOCK, above, Direction.DOWN);
            if (output == null) {
                updateDisplayStack(level, ItemStack.EMPTY);
                return;
            }
        }
        if (!pendingOutput.isEmpty()) {
            flushPendingOutput(level, output);
            return;
        }

        RecipeManager recipes = level.getRecipeManager();
        ItemStack checkedResult = ItemStack.EMPTY;
        if (output != null) {
            if (!recipeInputsDirty && activeRecipe != null
                    && recipes.byKey(activeRecipe.id()).orElse(null) == activeRecipe) {
                checkedResult = activeRecipe.value().result();
                if (!canInsert(output, checkedResult)) {
                    updateDisplayStack(level, ItemStack.EMPTY);
                    return;
                }
            } else if (!hasSpace(output)) {
                updateDisplayStack(level, ItemStack.EMPTY);
                return;
            }
        }

        var input = readInput(level);
        if (input == null) {
            recipeInputsDirty = true;
            updateDisplayStack(level, ItemStack.EMPTY);
            return;
        }
        recipeInputsDirty = false;
        var match = findMatch(recipes, input);
        var recipe = match == null ? null : match.recipe();
        if (activeRecipe != recipe) {
            activeRecipe = recipe;
            progress = 0;
        }
        if (match == null) {
            updateDisplayStack(level, ItemStack.EMPTY);
            return;
        }
        ItemStack result = match.recipe().value().result();
        if (output != null && result != checkedResult && !canInsert(output, result)) {
            updateDisplayStack(level, ItemStack.EMPTY);
            return;
        }

        updateDisplayStack(level, result);
        int overclock = updateOverclock(level, worldPosition);
        long work = progress + (long) overclock * match.pairs();
        long operations = work / TICKS_PER_OPERATION;
        progress = (int) (work % TICKS_PER_OPERATION);
        if (operations == 0) {
            return;
        }
        if (output == null) {
            eject(level, result, operations * result.getCount());
        } else {
            outputToContainer(output, result, operations);
        }
    }

    private @Nullable IgneousExtruderRecipe.Input readInput(Level level) {
        List<BlockState> sides = new ArrayList<>(4);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = worldPosition.relative(direction);
            if (!level.isLoaded(side)) {
                return null;
            }
            sides.add(level.getBlockState(side));
        }
        return new IgneousExtruderRecipe.Input(sides, level.getBlockState(worldPosition.below()));
    }

    private void outputToContainer(IItemHandler output, ItemStack result, long operations) {
        int resultCount = result.getCount();
        int operationsPerBatch = Math.max(1, result.getMaxStackSize() / resultCount);
        while (operations > 0) {
            int count = (int) Math.min(operations, operationsPerBatch) * resultCount;
            ItemStack batch = result.copyWithCount(count);
            int accepted = count - ItemHandlerHelper.insertItem(output, batch, true).getCount();
            int fittingOperations = accepted / resultCount;
            if (fittingOperations == 0) {
                return;
            }
            ItemStack remaining = ItemHandlerHelper.insertItem(output, batch.copyWithCount(fittingOperations * resultCount), false);
            if (!remaining.isEmpty()) {
                // Some handlers share capacity across slots, so a real insert can accept less than simulated.
                pendingOutput = remaining.copy();
                setChanged();
                return;
            }
            if (accepted < count) {
                return;
            }
            operations -= fittingOperations;
        }
    }

    private void flushPendingOutput(Level level, @Nullable IItemHandler output) {
        updateDisplayStack(level, ItemStack.EMPTY);
        ItemStack previous = pendingOutput;
        if (output == null) {
            eject(level, previous, previous.getCount());
            pendingOutput = ItemStack.EMPTY;
        } else {
            pendingOutput = ItemHandlerHelper.insertItem(output, previous, false);
        }
        if (pendingOutput.getCount() != previous.getCount()) {
            setChanged();
        }
    }

    private void eject(Level level, ItemStack result, long count) {
        while (count > 0) {
            int batch = (int) Math.min(count, result.getMaxStackSize());
            ItemStack stack = result.copyWithCount(batch);
            count -= batch;
            DefaultDispenseItemBehavior.spawnItem(level, stack, 6, Direction.UP, worldPosition.getCenter().add(0.0D, 0.7D, 0.0D));
        }
    }

    private record Match(RecipeHolder<IgneousExtruderRecipe> recipe, int pairs) {
    }
}
