package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
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

public class BreedingBoxRecipeCategory implements IRecipeCategory<BreedingBoxJeiRecipe> {
    public static final RecipeType<BreedingBoxJeiRecipe> TYPE = RecipeType.create(PoopSky.MOD_ID, "breeding_box", BreedingBoxJeiRecipe.class);

    private static final int WIDTH = 76;
    private static final int HEIGHT = 36;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    public BreedingBoxRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PSBlocks.BREEDING_BOX.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".breeding_box");
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override public RecipeType<BreedingBoxJeiRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BreedingBoxJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(recipe.flyInput1());
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).addItemStack(recipe.flyInput2());

        // 变异成功产物
        builder.addSlot(RecipeIngredientRole.OUTPUT, 57, 10)
                .addItemStack(recipe.resultFly())
                .addRichTooltipCallback((view, tooltip) -> {
                    float chance = recipe.chance();
                    tooltip.add(Component.translatable("jei.poopsky.breeding_box_chance",
                            String.format("%.0f", chance * 100)).withStyle(ChatFormatting.GRAY));
                });
    }

    @Override
    public void draw(BreedingBoxJeiRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.slot.draw(graphics, 0, 0);
        this.slot.draw(graphics, 0, 18);
        this.arrow.draw(graphics, 24, 10);
        this.slot.draw(graphics, 56, 10);
    }
}
