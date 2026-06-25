package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FlyNestRecipeCategory implements IRecipeCategory<FlyNestJeiRecipe> {
    public static final RecipeType<FlyNestJeiRecipe> TYPE = RecipeType.create(PoopSky.MOD_ID, "fly_nest", FlyNestJeiRecipe.class);

    private static final int WIDTH = 83;
    private static final int HEIGHT = 37;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    public FlyNestRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PBlocks.FLY_NEST.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".fly_nest");
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override public RecipeType<FlyNestJeiRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FlyNestJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 10).addItemStack(recipe.flyInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 58, 10).addItemStack(recipe.product());
    }

    @Override
    public void draw(FlyNestJeiRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.slot.draw(graphics, 1, 9);
        this.arrow.draw(graphics, 25, 11);
        this.slot.draw(graphics, 57, 9);
    }
}
