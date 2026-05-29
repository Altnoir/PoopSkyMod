package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CompooperRecipeCategory implements IRecipeCategory<CompooperRecipe> {
    public static final RecipeType<CompooperRecipe> TYPE = RecipeType.create(PoopSky.MOD_ID, "compooper", CompooperRecipe.class);

    private static final ResourceLocation BURN_PROGRESS_SPRITE =
            ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    private static final int WIDTH = 80;
    private static final int HEIGHT = 32;

    private final IDrawable icon;
    private final Component title;

    public CompooperRecipeCategory(IDrawable icon) {
        this.icon = icon;
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".compooper");
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
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 15)
                .addIngredients(recipe.input());

        builder.addSlot(RecipeIngredientRole.CATALYST, 30, 0)
                .addItemStack(recipe.catalyst());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 15)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(CompooperRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blitSprite(BURN_PROGRESS_SPRITE, 30, 15, 24, 16);
    }
}
