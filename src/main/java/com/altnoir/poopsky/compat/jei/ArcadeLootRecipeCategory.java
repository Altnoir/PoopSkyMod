package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Locale;

public class ArcadeLootRecipeCategory implements IRecipeCategory<ArcadeLootRecipe> {
    public static final RecipeType<ArcadeLootRecipe> TYPE = RecipeType.create(PoopSky.MOD_ID, "arcade", ArcadeLootRecipe.class);

    public static final int OUTPUT_COLUMNS = 7;
    public static final int OUTPUT_ROWS = 7;
    private static final int SLOT_SPACING = 18;
    private static final int PADDING = 1;
    private static final int INPUT_Y = PADDING;
    private static final int OUTPUT_START_Y = INPUT_Y + SLOT_SPACING + 2;
    private static final int WIDTH = OUTPUT_COLUMNS * SLOT_SPACING;
    private static final int HEIGHT = OUTPUT_START_Y + OUTPUT_ROWS * SLOT_SPACING - PADDING;
    private static final int INPUT_X = (WIDTH - 18) / 2 + 1;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable slot;

    public ArcadeLootRecipeCategory(IJeiHelpers helpers) {
        var guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(PoBlocks.RED_ARCADE.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".arcade");
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<ArcadeLootRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, ArcadeLootRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
                .addItemStack(recipe.input());

        for (int index = 0; index < ArcadeLootRecipe.OUTPUT_CAPACITY; index++) {
            int x = PADDING + (index % OUTPUT_COLUMNS) * SLOT_SPACING;
            int y = OUTPUT_START_Y + (index / OUTPUT_COLUMNS) * SLOT_SPACING;
            var slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);

            if (index >= recipe.outputs().size()) {
                continue;
            }

            ArcadeLootRecipe.Output output = recipe.outputs().get(index);
            if (output.tag() != null) {
                slotBuilder.addIngredients(Ingredient.of(output.tag()));
            } else {
                slotBuilder.addItemStack(output.item());
            }
            slotBuilder.addRichTooltipCallback((slotView, tooltip) -> {
                String chance = String.format(Locale.ROOT, "%.1f%%", output.chance());
                tooltip.add(Component.translatable("jei.poopsky.arcade_chance", chance).withStyle(ChatFormatting.GOLD));
                if (output.tag() != null) {
                    tooltip.add(Component.literal("#" + output.tag().location()).withStyle(ChatFormatting.GRAY));
                }
            });
        }
    }

    @Override
    public void draw(ArcadeLootRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        slot.draw(guiGraphics, INPUT_X - 1, INPUT_Y - 1);

        for (int index = 0; index < ArcadeLootRecipe.OUTPUT_CAPACITY; index++) {
            int x = PADDING + (index % OUTPUT_COLUMNS) * SLOT_SPACING;
            int y = OUTPUT_START_Y + (index / OUTPUT_COLUMNS) * SLOT_SPACING;
            slot.draw(guiGraphics, x - 1, y - 1);
        }
    }
}