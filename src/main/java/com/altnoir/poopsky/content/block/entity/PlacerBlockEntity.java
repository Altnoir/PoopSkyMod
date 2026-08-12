package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.client.inventory.PlacerMenu;
import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PlacerBlockEntity extends DispenserBlockEntity {
    private NonNullList<ItemStack> items;

    protected PlacerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
    }

    public PlacerBlockEntity(BlockPos pos, BlockState blockState) {
        this(PoBlockEntityType.PLACER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public int getContainerSize() {
        return 9;
    }

    public int getRandomSlot(RandomSource random) {
        this.unpackLootTable(null);
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.items.size(); ++k) {
            if (!this.items.get(k).isEmpty() && random.nextInt(j++) == 0) {
                i = k;
            }
        }
        return i;
    }

    public ItemStack insertItem(ItemStack stack) {
        int i = this.getMaxStackSize(stack);

        for (int j = 0; j < this.items.size(); ++j) {
            ItemStack itemstack = this.items.get(j);
            if (itemstack.isEmpty() || ItemStack.isSameItemSameComponents(stack, itemstack)) {
                int k = Math.min(stack.getCount(), i - itemstack.getCount());
                if (k > 0) {
                    if (itemstack.isEmpty()) {
                        this.setItem(j, stack.split(k));
                    } else {
                        stack.shrink(k);
                        itemstack.grow(k);
                    }
                }

                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        return stack;
    }

    protected Component getDefaultName() {
        return Component.translatable("container.placer");
    }

    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }
    }

    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }
    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new PlacerMenu(id, player, this);
    }
}
