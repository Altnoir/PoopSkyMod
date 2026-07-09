package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.FlyBarrelRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;

public class FlyBarrelRecipeCategory implements IRecipeCategory<RecipeHolder<FlyBarrelRecipe>> {
    public static final RecipeType<RecipeHolder<FlyBarrelRecipe>> TYPE = RecipeType.createRecipeHolderType(PoopSky.loc("fly_barrel"));

    private static final int HEIGHT = 18;
    private static final int INPUT_X = 1;
    private static final int INPUT_Y = 1;
    private static final int ARROW_X = INPUT_X + 24;
    private static final int ARROW_Y = INPUT_Y + 1;
    private static final int OUTPUT_X = ARROW_X + 31;
    private static final int WIDTH = OUTPUT_X + 17;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    public FlyBarrelRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PBlocks.FLY_BARREL.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".fly_barrel");
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<RecipeHolder<FlyBarrelRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FlyBarrelRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y).addItemStack(FlyItem.withType(FlyType.byId(recipe.flyTypeId())));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, INPUT_Y).addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<FlyBarrelRecipe> recipeHolder, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.slot.draw(graphics, INPUT_X - 1, INPUT_Y - 1);
        this.arrow.draw(graphics, ARROW_X, ARROW_Y);
        this.slot.draw(graphics, OUTPUT_X - 1, INPUT_Y - 1);
    }
}