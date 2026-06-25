package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.recipe.POPExplosionRecipe;
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

public class POPExplosionRecipeCategory implements IRecipeCategory<RecipeHolder<POPExplosionRecipe>> {
    public static final RecipeType<RecipeHolder<POPExplosionRecipe>> TYPE = RecipeType.createRecipeHolderType(PoopSky.loc("pop_explosion"));

    private static final int WIDTH = 73;
    private static final int HEIGHT = 18;
    private static final int INPUT_X = 1;
    private static final int INPUT_Y = 1;
    private static final int ARROW_X = INPUT_X + 24;
    private static final int ARROW_Y = INPUT_Y + 1;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 16;
    private static final int OUTPUT_X = ARROW_X + 31;
    private static final int OUTPUT_Y = 1;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    public POPExplosionRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PBlocks.POOP_TNT.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".pop_explosion");
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<RecipeHolder<POPExplosionRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<POPExplosionRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(recipe.output().toItemStack());
    }

    @Override
    public void draw(RecipeHolder<POPExplosionRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics, INPUT_X - 1, INPUT_Y - 1);
        this.arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
        this.slot.draw(guiGraphics, OUTPUT_X - 1, OUTPUT_Y - 1);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<POPExplosionRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        var recipe = recipeHolder.value();
        if (recipe.radius() <= 0) return;

        if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_WIDTH
                && mouseY >= ARROW_Y && mouseY < ARROW_Y + ARROW_HEIGHT) {
            tooltip.add(Component.translatable("jei.poopsky.pop_explosion_radius", recipe.radius()).withStyle(ChatFormatting.GOLD));
        }
    }
}