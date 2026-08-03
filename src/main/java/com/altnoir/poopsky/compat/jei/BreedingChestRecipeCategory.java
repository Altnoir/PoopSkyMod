package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.BreedingChestRecipe;
import com.altnoir.poopsky.init.PoBlocks;
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

public class BreedingChestRecipeCategory implements IRecipeCategory<RecipeHolder<BreedingChestRecipe>> {
    public static final RecipeType<RecipeHolder<BreedingChestRecipe>> TYPE = RecipeType.createRecipeHolderType(PoopSky.loc("breeding_chest"));

    private static final int HEIGHT = 36;
    private static final int INPUT_X = 1;
    private static final int INPUT_Y = 1;
    private static final int INPUT2_Y = INPUT_Y + 18;
    private static final int ARROW_X = INPUT_X + 24;
    private static final int ARROW_Y = INPUT_Y + 9;
    private static final int OUTPUT_X = ARROW_X + 31;
    private static final int OUTPUT_Y = ARROW_Y;
    private static final int WIDTH = OUTPUT_X + 17;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    public BreedingChestRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PoBlocks.BREEDING_CHEST.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".breeding_chest");
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<RecipeHolder<BreedingChestRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BreedingChestRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y).addItemStack(FlyItem.withType(FlyType.byId(recipe.parent1())));
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT2_Y).addItemStack(FlyItem.withType(FlyType.byId(recipe.parent2())));

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(FlyItem.withType(FlyType.byId(recipe.result())))
                .addRichTooltipCallback((view, tooltip) -> {
                    var chance = recipe.chance() * 100.0F < 1.0F ? "<1" : String.format("%.2f", recipe.chance() * 100.0F).replaceAll("\\.?0+$", "");
                    tooltip.add(Component.translatable("jei.poopsky.breeding_chest_chance", chance).withStyle(ChatFormatting.GOLD));
                });
    }

    @Override
    public void draw(RecipeHolder<BreedingChestRecipe> recipeHolder, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.slot.draw(graphics, INPUT_X - 1, INPUT_Y - 1);
        this.slot.draw(graphics, INPUT_X - 1, INPUT2_Y - 1);
        this.arrow.draw(graphics, ARROW_X, ARROW_Y);
        this.slot.draw(graphics, OUTPUT_X - 1, OUTPUT_Y - 1);
    }
}