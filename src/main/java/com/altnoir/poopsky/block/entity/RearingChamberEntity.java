package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.block.PSBlockEntities;
import com.altnoir.poopsky.recipe.PSRecipes;
import com.altnoir.poopsky.recipe.RearingChamberRecipe;
import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ProgressBar;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.appliedenergistics.yoga.*;

import java.util.Optional;

public class RearingChamberEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {
    private final FieldManagedStorage syncStorge = new FieldManagedStorage(this);

    @Persisted
    public final ItemStackHandler inputSlot = new ItemStackHandler(1);
    @Persisted
    public final ItemStackHandler outputSlot = new ItemStackHandler(1);

    @Persisted
    public int processingTime = 0;
    @Persisted
    public int maxProcessingTime = 0;
    @Persisted
    public boolean isProcessing = false;

    private RearingChamberRecipe currentRecipe = null;

    public RearingChamberEntity(BlockPos pos, BlockState blockState) {
        super(PSBlockEntities.REARING_CHAMBER.get(), pos, blockState);
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorge;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RearingChamberEntity blockEntity) {
        // 检查是否有输入物品
        ItemStack input = blockEntity.inputSlot.getStackInSlot(0);

        // 如果没有输入物品且不在处理中，重置状态
        if (input.isEmpty() && !blockEntity.isProcessing) {
            blockEntity.processingTime = 0;
            blockEntity.maxProcessingTime = 0;
            blockEntity.currentRecipe = null;
            return;
        }

        // 如果不在处理中，尝试开始新的处理
        if (!blockEntity.isProcessing) {
            // 查找匹配的配方
            Optional<RearingChamberRecipe> recipe = blockEntity.getRecipeForInput(input, level);

            if (recipe.isPresent()) {
                // 检查输出槽是否有空间
                ItemStack output = recipe.get().getResultItem(level.registryAccess());
                if (blockEntity.canInsertIntoOutput(output)) {
                    blockEntity.currentRecipe = recipe.get();
                    blockEntity.maxProcessingTime = recipe.get().processingTime();
                    blockEntity.processingTime = 0;
                    blockEntity.isProcessing = true;
                }
            }
        } else {
            blockEntity.processingTime++;

            // 检查是否完成处理
            if (blockEntity.processingTime >= blockEntity.maxProcessingTime) {
                // 完成处理，将结果放入输出槽
                if (blockEntity.currentRecipe != null && !input.isEmpty()) {
                    ItemStack result = blockEntity.currentRecipe.getResultItem(level.registryAccess());

                    // 消耗输入物品
                    input.shrink(1);

                    // 将结果放入输出槽
                    blockEntity.outputSlot.insertItem(0, result, false);
                }

                // 重置状态
                blockEntity.isProcessing = false;
                blockEntity.processingTime = 0;
                blockEntity.maxProcessingTime = 0;
                blockEntity.currentRecipe = null;
            }
        }
    }

    private Optional<RearingChamberRecipe> getRecipeForInput(ItemStack input, Level level) {
        RecipeManager recipeManager = level.getRecipeManager();
        return recipeManager.getRecipeFor(PSRecipes.REARING_CHAMBER_TYPE.get(),
                new RearingChamberRecipe.Input(new ItemStack[]{input}), level).map(RecipeHolder::value);
    }

    private boolean canInsertIntoOutput(ItemStack output) {
        ItemStack existingOutput = outputSlot.getStackInSlot(0);
        if (existingOutput.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(existingOutput, output) &&
                existingOutput.getCount() + output.getCount() <= existingOutput.getMaxStackSize();
    }

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        var root = new UIElement().addClass("panel_bg");

        root.style(basicStyle -> basicStyle.background(Sprites.TAB))
                .layout(layoutStyle -> layoutStyle
                        .setGap(YogaGutter.ALL, 2)
                        .setPadding(YogaEdge.ALL, 4));

        var container = new UIElement();
        var left = new UIElement();
        var middle = new UIElement();
        var right = new UIElement();

        container.addChildren(left, middle, right).layout(layoutStyle -> layoutStyle
                .flexDirection(YogaFlexDirection.ROW)
                .setPadding(YogaEdge.ALL, 4)
                .setGap(YogaGutter.ALL, 8)
                .setJustifyContent(YogaJustify.CENTER)
                .setAlignItems(YogaAlign.CENTER)      // 垂直居中 (让左中右三块内容在中间对齐)
        );

        left.addChildren(new ItemSlot().bind(inputSlot, 0));

        middle.addChildren(
                new ProgressBar().setProgress(maxProcessingTime > 0 ? (float) processingTime / maxProcessingTime : 0)
                        .label(label -> label.setText("进度条"))
                        .layout(layout -> layout.setWidth(58).setHeight(16))
        );

        right.addChildren(new ItemSlot().bind(outputSlot, 0));

        root.addChildren(container);

        var invs = new UIElement().addChildren(new InventorySlots());
        root.addChild(invs);

        return new ModularUI(UI.of(root), holder.player);
    }
}