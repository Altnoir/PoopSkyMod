package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.PigstyPart;
import com.altnoir.poopsky.content.block.p.PigstyBlock;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class PigstyBlockEntity extends BlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && (stack.is(PoItems.POOP.get()) || stack.is(PoItems.CHILI_POOP.get()));
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final IItemHandler output = new IItemHandler() {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            PigstyBlockEntity target = getTarget(slot);
            return target == null ? ItemStack.EMPTY : target.inventory.getStackInSlot(0);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            PigstyBlockEntity target = getTarget(slot);
            return target == null ? ItemStack.EMPTY : target.inventory.extractItem(0, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return Item.ABSOLUTE_MAX_STACK_SIZE;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };

    public PigstyBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.PIGSTY.get(), pos, state);
    }

    public static boolean insertProduct(PigstyBlockEntity[] pair, ItemLike item) {
        ItemStack product = new ItemStack(item);
        for (int pass = 0; pass < 2; pass++) {
            for (PigstyBlockEntity part : pair) {
                ItemStack stored = part.inventory.getStackInSlot(0);
                if (stored.isEmpty() == (pass == 0)) continue;
                if (part.inventory.insertItem(0, product, false).isEmpty()) return true;
            }
        }
        return false;
    }

    public IItemHandler getOutputHandler() {
        return output;
    }

    private @Nullable PigstyBlockEntity getTarget(int slot) {
        if (slot < 0 || slot >= 2) throw new IndexOutOfBoundsException("Pigsty slot: " + slot);
        PigstyBlockEntity[] pair = getPair();
        return pair == null ? null : pair[slot];
    }

    public @Nullable PigstyBlockEntity[] getPair() {
        if (level == null || isRemoved() || !level.hasChunkAt(worldPosition)) return null;
        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof PigstyBlock)) return null;
        BlockPos backPos = PigstyBlock.mainPos(state, worldPosition);
        BlockPos frontPos = backPos.relative(state.getValue(PigstyBlock.FACING));
        if (!level.hasChunkAt(backPos) || !level.hasChunkAt(frontPos)) return null;
        BlockState backState = level.getBlockState(backPos);
        BlockState frontState = level.getBlockState(frontPos);
        if (!(backState.getBlock() instanceof PigstyBlock) || !frontState.is(backState.getBlock())
                || backState.getValue(PigstyBlock.PART) != PigstyPart.BACK
                || frontState.getValue(PigstyBlock.PART) != PigstyPart.FRONT
                || backState.getValue(PigstyBlock.FACING) != frontState.getValue(PigstyBlock.FACING)) return null;
        if (!(level.getBlockEntity(backPos) instanceof PigstyBlockEntity back)
                || !(level.getBlockEntity(frontPos) instanceof PigstyBlockEntity front)
                || back.isRemoved() || front.isRemoved()) return null;
        return new PigstyBlockEntity[]{back, front};
    }

    public void dropContents(Level level, BlockPos pos) {
        ItemStack stack = inventory.extractItem(0, Integer.MAX_VALUE, false);
        if (!stack.isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }
}
