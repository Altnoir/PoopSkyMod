package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoItems;
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

public class LiquidInteractionRecipeCategory implements IRecipeCategory<LiquidInteractionRecipe> {
    public static final RecipeType<LiquidInteractionRecipe> TYPE = RecipeType.create(PoopSky.MOD_ID, "liquid_interaction", LiquidInteractionRecipe.class);

    private final IDrawable icon;
    private final Component title;
    private final LiquidRecipeLayout layout;
    private final long bucketVolume;

    public LiquidInteractionRecipeCategory(IJeiHelpers helpers, IDrawable arrow, IDrawable plus) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PoItems.URINE_BUCKET.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".liquid_interaction");
        this.layout = new LiquidRecipeLayout(guiHelper.getSlotDrawable(), arrow, plus);
        this.bucketVolume = helpers.getPlatformFluidHelper().bucketVolume();
    }

    @Override
    public RecipeType<LiquidInteractionRecipe> getRecipeType() {
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
        return LiquidRecipeLayout.WIDTH;
    }

    @Override
    public int getHeight() {
        return LiquidRecipeLayout.HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LiquidInteractionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, LiquidRecipeLayout.FIRST_X, LiquidRecipeLayout.INPUT_Y)
                .setFluidRenderer(bucketVolume, false, 16, 16)
                .addFluidStack(recipe.leftFluid(), bucketVolume);

        var right = builder.addSlot(RecipeIngredientRole.INPUT, LiquidRecipeLayout.SECOND_X, LiquidRecipeLayout.INPUT_Y);
        if (recipe.isFluidFluid()) {
            right.setFluidRenderer(bucketVolume, false, 16, 16)
                    .addFluidStack(recipe.rightFluid(), bucketVolume);
        } else {
            right.addItemStack(new ItemStack(recipe.rightBlock()));
        }
        if (recipe.hasContext()) {
            builder.addSlot(RecipeIngredientRole.INPUT, LiquidRecipeLayout.BELOW_X, LiquidRecipeLayout.BELOW_Y)
                    .addItemStack(new ItemStack(recipe.contextBlock()));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, LiquidRecipeLayout.OUTPUT_X, LiquidRecipeLayout.INPUT_Y)
                .addItemStacks(recipe.outputs().stream().map(ItemStack::new).toList());
    }

    @Override
    public void draw(LiquidInteractionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        layout.draw(guiGraphics, recipe.hasContext());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, LiquidInteractionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (!layout.isOverArrow(mouseX, mouseY)) {
            return;
        }

        if (recipe.isFluidFluid()) {
            tooltip.add(Component.translatable("jei.poopsky.liquid_interaction.source_flowing").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("jei.poopsky.liquid_interaction.replace_left").withStyle(ChatFormatting.GRAY));
        } else if (recipe.hasContext()) {
            tooltip.add(Component.translatable("jei.poopsky.liquid_interaction.below", recipe.contextBlock().getName().withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY));
        }
    }
}
