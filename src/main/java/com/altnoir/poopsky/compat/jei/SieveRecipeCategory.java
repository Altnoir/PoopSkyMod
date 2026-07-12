package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.content.recipe.SieveRecipe;
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

public class SieveRecipeCategory implements IRecipeCategory<RecipeHolder<SieveRecipe>> {
    public static final RecipeType<RecipeHolder<SieveRecipe>> TYPE = RecipeType.createRecipeHolderType(PoopSky.loc("sieve"));

    private static final int WIDTH = 145;
    private static final int HEIGHT = 36;
    private static final int INPUT_X = 1;
    private static final int INPUT_Y = 9;
    private static final int ARROW_X = INPUT_X + 24;
    private static final int ARROW_Y = INPUT_Y + 1;
    private static final int OUTPUT_START_X = ARROW_X + 31;
    private static final int OUTPUT_START_Y = 1;
    private static final int OUTPUT_COLUMNS = 5;
    private static final int OUTPUT_ROWS = 2;
    private static final int SLOT_SPACING = 18;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    public SieveRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PoBlocks.SIEVE.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".sieve");
        this.arrow = arrow;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<RecipeHolder<SieveRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SieveRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
                .addIngredients(recipe.input());

        for (int index = 0; index < OUTPUT_COLUMNS * OUTPUT_ROWS; index++) {
            int x = OUTPUT_START_X + (index % OUTPUT_COLUMNS) * SLOT_SPACING;
            int y = OUTPUT_START_Y + (index / OUTPUT_COLUMNS) * SLOT_SPACING;
            var slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);

            if (index < recipe.outputs().size()) {
                var chanceItem = recipe.outputs().get(index);
                var chance = chanceItem.chance() * 100.0F < 1.0F ? "<1" : String.format("%.2f", chanceItem.chance() * 100.0F).replaceAll("\\.?0+$", "");
                slotBuilder.addItemStack(chanceItem.stack().copy()).addRichTooltipCallback(
                        (slotView, tooltip) -> tooltip.add(Component.translatable("jei.poopsky.sieve_chance", chance).withStyle(ChatFormatting.GOLD)));
            }
        }
    }

    @Override
    public void draw(RecipeHolder<SieveRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slot.draw(guiGraphics, INPUT_X - 1, INPUT_Y - 1);
        this.arrow.draw(guiGraphics, ARROW_X, ARROW_Y);

        for (int index = 0; index < OUTPUT_COLUMNS * OUTPUT_ROWS; index++) {
            int x = OUTPUT_START_X + (index % OUTPUT_COLUMNS) * SLOT_SPACING;
            int y = OUTPUT_START_Y + (index / OUTPUT_COLUMNS) * SLOT_SPACING;
            this.slot.draw(guiGraphics, x - 1, y - 1);
        }
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }


}
