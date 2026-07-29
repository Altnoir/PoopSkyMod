package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.impl.util.ClientUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CompooperDisplayRecipeCategory implements IRecipeCategory<CompooperDisplayRecipe> {
    public static final RecipeType<CompooperDisplayRecipe> TYPE =
            RecipeType.create(PoopSky.MOD_ID, "compooper_display", CompooperDisplayRecipe.class);

    private static final int WIDTH = 140;
    private static final int HEIGHT = 48;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    private final IModIdHelper modIdHelper;

    public CompooperDisplayRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var helper = helpers.getGuiHelper();

        this.icon = helper.createDrawableItemStack(new ItemStack(PoBlocks.COMPOOPER.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".compooper");
        this.arrow = arrow;
        this.slot = helper.getSlotDrawable();
        this.modIdHelper = helpers.getModIdHelper();
    }

    @Override
    public RecipeType<CompooperDisplayRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompooperDisplayRecipe recipe, IFocusGroup focuses) {
        if (!recipe.input().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 4, 18).addItemStack(recipe.input());
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18).addItemStack(recipe.output());
    }

    @Override
    public void draw(CompooperDisplayRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        BlockState state = recipe.compooperState();

        if (!recipe.input().isEmpty()) {
            this.slot.draw(guiGraphics, 3, 17);
            this.arrow.draw(guiGraphics, 28, 18);
        }
        ClientUtil.renderBlock(guiGraphics, state, 70, 18, 10, 20f);
        this.arrow.draw(guiGraphics, 90, 18);
        this.slot.draw(guiGraphics, 119, 17);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CompooperDisplayRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (54 < mouseX && mouseX < 86 && 10 < mouseY && mouseY < 42) {
            BlockState state = recipe.compooperState();
            Block block = state.getBlock();
            String modId = BuiltInRegistries.BLOCK.getKey(block).getNamespace();

            tooltip.add(Component.translatable(block.getDescriptionId()));
            tooltip.add(Component.literal(this.modIdHelper.getFormattedModNameForModId(modId)));
        }
    }
}
