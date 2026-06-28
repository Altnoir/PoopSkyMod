package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PItems;
import com.altnoir.poopsky.recipe.AnalPressingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AnalPressingRecipeCategory implements IRecipeCategory<RecipeHolder<AnalPressingRecipe>> {
    public static final RecipeType<RecipeHolder<AnalPressingRecipe>> TYPE = RecipeType.createRecipeHolderType(PoopSky.loc("anal_pressing"));

    private static final int HEIGHT = 18;
    private static final int INPUT1_X = 1;
    private static final int INPUT1_Y = 1;
    private static final int PLUS_X = INPUT1_X + 24;
    private static final int PLUS_Y = INPUT1_Y + 2;
    private static final int INPUT2_X = PLUS_X + 22;
    private static final int INPUT2_Y = INPUT1_Y;
    private static final int ARROW_X = INPUT2_X + 24;
    private static final int ARROW_Y = INPUT2_Y + 1;
    private static final int OUTPUT_X = ARROW_X + 31;
    private static final int OUTPUT_Y = INPUT1_Y;
    private static final int WIDTH = OUTPUT_X + 17;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable plus;
    private final IDrawable slot;

    public AnalPressingRecipeCategory(IJeiHelpers helpers, IDrawable arrow, IDrawable plus) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PItems.KING_OF_DRAGON_FRUIT.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".anal_pressing");
        this.plus = plus;
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<RecipeHolder<AnalPressingRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AnalPressingRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        int count = (2 * recipe.radius() + 1) * (2 * recipe.radius() + 1);
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT1_X, INPUT1_Y)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT2_X, INPUT2_Y)
                .addItemStack(new ItemStack(recipe.replaceTarget(), count));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(new ItemStack(recipe.output()));
    }

    @Override
    public void draw(RecipeHolder<AnalPressingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics, INPUT1_X - 1, INPUT1_Y - 1);
        this.plus.draw(guiGraphics, PLUS_X, PLUS_Y);
        this.slot.draw(guiGraphics, INPUT2_X - 1, INPUT2_Y - 1);
        this.arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
        this.slot.draw(guiGraphics, OUTPUT_X - 1, OUTPUT_Y - 1);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<AnalPressingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        var recipe = recipeHolder.value();
        int count = (2 * recipe.radius() + 1) * (2 * recipe.radius() + 1);
        if (mouseX >= ARROW_X && mouseX < ARROW_X + 22
                && mouseY >= ARROW_Y && mouseY < ARROW_Y + 15) {
            tooltip.add(Component.translatable("jei.poopsky.anal_pressing_replace", count).withStyle(ChatFormatting.GRAY));
        }
    }
}