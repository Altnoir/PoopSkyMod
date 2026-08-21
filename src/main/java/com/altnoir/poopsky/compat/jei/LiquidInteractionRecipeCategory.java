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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class LiquidInteractionRecipeCategory implements IRecipeCategory<LiquidInteractionRecipe> {
    public static final RecipeType<LiquidInteractionRecipe> TYPE = RecipeType.create(PoopSky.MOD_ID, "liquid_interaction", LiquidInteractionRecipe.class);

    private static final int HEIGHT = 18;
    private static final int SLOT_X = 1;
    private static final int SLOT_Y = 1;
    private static final int PLUS_X = SLOT_X + 24;
    private static final int PLUS_Y = SLOT_Y + 2;
    private static final int RIGHT_X = PLUS_X + 22;
    private static final int CONTEXT_X = RIGHT_X + 18;
    private static final int ARROW_X = CONTEXT_X + 24;
    private static final int ARROW_Y = SLOT_Y + 1;
    private static final int OUTPUT_X = ARROW_X + 31;
    private static final int WIDTH = OUTPUT_X + 17;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable plus;
    private final IDrawable slot;
    private final long bucketVolume;

    public LiquidInteractionRecipeCategory(IJeiHelpers helpers, IDrawable arrow, IDrawable plus) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PoItems.URINE_BUCKET.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".liquid_interaction");
        this.arrow = arrow;
        this.plus = plus;
        this.slot = guiHelper.getSlotDrawable();
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
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LiquidInteractionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X, SLOT_Y)
                .setFluidRenderer(bucketVolume, false, 16, 16)
                .addFluidStack(recipe.leftFluid(), bucketVolume);

        if (recipe.isFluidFluid()) {
            builder.addSlot(RecipeIngredientRole.INPUT, RIGHT_X + 9, SLOT_Y)
                    .setFluidRenderer(bucketVolume, false, 16, 16)
                    .addFluidStack(recipe.rightFluid(), bucketVolume);
        } else if (recipe.hasContext()) {
            builder.addSlot(RecipeIngredientRole.INPUT, RIGHT_X, SLOT_Y)
                    .addItemStack(new ItemStack(recipe.rightBlock()));
            builder.addSlot(RecipeIngredientRole.INPUT, CONTEXT_X, SLOT_Y)
                    .addItemStack(new ItemStack(recipe.contextBlock()));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, RIGHT_X + 9, SLOT_Y)
                    .addItemStack(new ItemStack(recipe.rightBlock()));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                .addItemStacks(recipe.outputs().stream().map(ItemStack::new).toList());
    }

    @Override
    public void draw(LiquidInteractionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics, SLOT_X - 1, SLOT_Y - 1);
        if (recipe.hasContext()) {
            this.plus.draw(guiGraphics, PLUS_X, PLUS_Y);
            this.slot.draw(guiGraphics, RIGHT_X - 1, SLOT_Y - 1);
            this.slot.draw(guiGraphics, CONTEXT_X - 1, SLOT_Y - 1);
        } else {
            this.plus.draw(guiGraphics, PLUS_X + 4, PLUS_Y);
            this.slot.draw(guiGraphics, RIGHT_X + 8, SLOT_Y - 1);
        }
        this.arrow.draw(guiGraphics, ARROW_X, ARROW_Y);
        this.slot.draw(guiGraphics, OUTPUT_X - 1, SLOT_Y - 1);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, LiquidInteractionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        int arrowX = ARROW_X;
        boolean overArrow = mouseX >= arrowX && mouseX < arrowX + 22
                && mouseY >= ARROW_Y && mouseY < ARROW_Y + 15;
        if (!overArrow) {
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