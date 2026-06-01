package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.utill.ClientUtil;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;

public class CompooperRecipeCategory implements IRecipeCategory<CompooperRecipe> {
    public static final RecipeType<CompooperRecipe> TYPE = RecipeType.create(PoopSky.MOD_ID, "compooper", CompooperRecipe.class);

    private static final int WIDTH = 140;
    private static final int HEIGHT = 48;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    private final IModIdHelper modIdHelper;

    public CompooperRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var helper = helpers.getGuiHelper();

        this.icon = helper.createDrawableItemStack(new ItemStack(PSBlocks.COMPOOPER.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".compooper");
        this.arrow = arrow;
        this.slot = helper.getSlotDrawable();
        this.modIdHelper = helpers.getModIdHelper();
    }

    @Override
    public RecipeType<CompooperRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, CompooperRecipe recipe, IFocusGroup focuses) {
        if (recipe.input() != Ingredient.EMPTY) {
            builder.addSlot(RecipeIngredientRole.INPUT, 4, 18).addIngredients(recipe.input());
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18).addItemStack(recipe.output());
    }

    @Override
    public void draw(CompooperRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        BlockState state = recipe.states();

        if (recipe.input() != Ingredient.EMPTY) {
            this.slot.draw(guiGraphics, 3, 17);
            this.arrow.draw(guiGraphics, 28, 18);
        }
        ClientUtil.renderBlock(guiGraphics, state, 70, 18, 10, 20f);
        this.arrow.draw(guiGraphics, 90, 18);
        this.slot.draw(guiGraphics, 119, 17);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CompooperRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (54 < mouseX && mouseX < 86 && 10 < mouseY && mouseY < 42) {
            var block = recipe.states().getBlock();
            var modId = BuiltInRegistries.BLOCK.getKey(block).getNamespace();

            tooltip.add(Component.translatable(block.getDescriptionId()));
            tooltip.add(Component.literal(this.modIdHelper.getFormattedModNameForModId(modId)));
        }
    }
}
