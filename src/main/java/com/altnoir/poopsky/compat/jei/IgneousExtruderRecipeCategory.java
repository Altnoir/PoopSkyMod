package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.IgneousExtruderRecipe;
import com.altnoir.poopsky.init.PoBlocks;
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

public class IgneousExtruderRecipeCategory implements IRecipeCategory<RecipeHolder<IgneousExtruderRecipe>> {
    public static final RecipeType<RecipeHolder<IgneousExtruderRecipe>> TYPE =
            RecipeType.createRecipeHolderType(PoopSky.loc("igneous_extruder"));
    private static final Component SOURCE = Component.translatable("jei.poopsky.igneous_extruder.source");
    private final IDrawable icon;
    private final LiquidRecipeLayout layout;
    private final long bucketVolume;

    public IgneousExtruderRecipeCategory(IJeiHelpers helpers, IDrawable arrow, IDrawable plus) {
        var guiHelper = helpers.getGuiHelper();
        icon = guiHelper.createDrawableItemStack(new ItemStack(PoBlocks.IGNEOUS_EXTRUDER.get()));
        layout = new LiquidRecipeLayout(guiHelper.getSlotDrawable(), arrow, plus);
        bucketVolume = helpers.getPlatformFluidHelper().bucketVolume();
    }

    @Override
    public RecipeType<RecipeHolder<IgneousExtruderRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return PoBlocks.IGNEOUS_EXTRUDER.get().getName();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return LiquidRecipeLayout.WIDTH;
    }

    @Override
    public int getHeight() {
        return LiquidRecipeLayout.HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<IgneousExtruderRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        addSide(builder, LiquidRecipeLayout.FIRST_X, recipe.inputs().get(0));
        addSide(builder, LiquidRecipeLayout.SECOND_X, recipe.inputs().get(1));
        recipe.below().ifPresent(block -> builder.addSlot(RecipeIngredientRole.INPUT, LiquidRecipeLayout.BELOW_X, LiquidRecipeLayout.BELOW_Y)
                .addItemStack(new ItemStack(block)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, LiquidRecipeLayout.OUTPUT_X, LiquidRecipeLayout.INPUT_Y)
                .addItemStack(recipe.result().copy());
    }

    private void addSide(IRecipeLayoutBuilder builder, int x, IgneousExtruderRecipe.SideInput input) {
        var ingredient = builder.addSlot(RecipeIngredientRole.INPUT, x, LiquidRecipeLayout.INPUT_Y);
        if (input.fluid().isPresent()) {
            ingredient.setFluidRenderer(bucketVolume, false, 16, 16)
                    .addFluidStack(input.fluid().get(), bucketVolume)
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(SOURCE));
        } else {
            ingredient.addItemStack(new ItemStack(input.block().orElseThrow()));
        }
    }

    @Override
    public void draw(RecipeHolder<IgneousExtruderRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        layout.draw(graphics, holder.value().below().isPresent());
    }
}
