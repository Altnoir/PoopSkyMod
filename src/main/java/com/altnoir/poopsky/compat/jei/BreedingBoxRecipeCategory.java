package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PFlyTypes;
import com.altnoir.poopsky.item.p.FlyItem;
import com.altnoir.poopsky.recipe.BreedingBoxRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
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

public class BreedingBoxRecipeCategory implements IRecipeCategory<RecipeHolder<BreedingBoxRecipe>> {
    public static final RecipeType<RecipeHolder<BreedingBoxRecipe>> TYPE = RecipeType.createRecipeHolderType(PoopSky.loc("breeding_box"));

    private static final int WIDTH = 77;
    private static final int HEIGHT = 37;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    public BreedingBoxRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PBlocks.BREEDING_BOX.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".breeding_box");
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<RecipeHolder<BreedingBoxRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BreedingBoxRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).addItemStack(FlyItem.withType(PFlyTypes.byId(recipe.parent1())));
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 20).addItemStack(FlyItem.withType(PFlyTypes.byId(recipe.parent2())));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 58, 11)
                .addItemStack(FlyItem.withType(PFlyTypes.byId(recipe.result())))
                .addRichTooltipCallback((view, tooltip) -> {
                    var chance = recipe.chance() * 100.0F < 1.0F ? "<1" : String.format("%.2f", recipe.chance() * 100.0F).replaceAll("\\.?0+$", "");
                    tooltip.add(Component.translatable("jei.poopsky.breeding_box_chance", chance).withStyle(ChatFormatting.GOLD));
                });
    }

    @Override
    public void draw(RecipeHolder<BreedingBoxRecipe> recipeHolder, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.slot.draw(graphics, 1, 1);
        this.slot.draw(graphics, 1, 19);
        this.arrow.draw(graphics, 25, 11);
        this.slot.draw(graphics, 57, 11);
    }
}