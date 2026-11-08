package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.POPExplosionRecipe;
import com.altnoir.poopsky.init.PoBlocks;
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

public class POPExplosionRecipeCategory implements IRecipeCategory<POPExplosionRecipe> {
    public static final RecipeType<POPExplosionRecipe> TYPE = new RecipeType<>(PoopSky.loc("pop_explosion"), POPExplosionRecipe.class);

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
    private final IDrawable arrow2;
    private final IDrawable slot;

    public POPExplosionRecipeCategory(IJeiHelpers helpers, IDrawable arrow, IDrawable arrow2) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PoBlocks.POP.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".pop_explosion");
        this.arrow = arrow;
        this.arrow2 = arrow2;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<POPExplosionRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public net.minecraft.resources.ResourceLocation getRegistryName(POPExplosionRecipe recipe) { return recipe.getId(); }

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
    public void setRecipe(IRecipeLayoutBuilder builder, POPExplosionRecipe recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder;
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(recipe.output().toItemStack());
    }

    @Override
    public void draw(POPExplosionRecipe recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics, INPUT_X - 1, INPUT_Y - 1);
        IDrawable arrow = recipeHolder.radius() > 0 ? this.arrow2 : this.arrow;
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
        this.slot.draw(guiGraphics, OUTPUT_X - 1, OUTPUT_Y - 1);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, POPExplosionRecipe recipeHolder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        var recipe = recipeHolder;
        if (recipe.radius() <= 0) return;

        if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_WIDTH
                && mouseY >= ARROW_Y && mouseY < ARROW_Y + ARROW_HEIGHT) {
            tooltip.add(Component.translatable("jei.poopsky.pop_explosion_radius", recipe.radius()).withStyle(ChatFormatting.GOLD));
        }
    }
}