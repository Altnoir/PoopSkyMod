package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.OminousAltarBlockEntity;
import com.altnoir.poopsky.content.recipe.AltarRecipe;
import com.altnoir.poopsky.init.PoBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AltarRecipeCategory implements IRecipeCategory<RecipeHolder<AltarRecipe>> {
    public static final RecipeType<RecipeHolder<AltarRecipe>> TYPE = RecipeType.createRecipeHolderType(PoopSky.loc("ominous_altar"));
    private static final int POSITION_SCALE = 10;
    private static final int CENTER_X = 31;
    private static final int CENTER_Y = 31;
    private static final int ARROW_X = CENTER_X + 51;
    private static final int OUTPUT_X = ARROW_X + 27;
    private static final int OUTPUT_Y = CENTER_Y;
    private static final int FLUID_X = OUTPUT_X;
    private static final int FLUID_Y = CENTER_Y + 30;
    private static final int WIDTH = 126;
    private static final int HEIGHT = 78;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable arrow;
    private final IDrawable commandIcon;
    private final long bucketVolume;

    public AltarRecipeCategory(IJeiHelpers helpers, IDrawable arrow, IDrawable commandIcon) {
        var gui = helpers.getGuiHelper();
        icon = gui.createDrawableItemStack(new ItemStack(PoBlocks.OMINOUS_ALTAR.get()));
        slot = gui.getSlotDrawable();
        this.arrow = arrow;
        this.commandIcon = commandIcon;
        bucketVolume = helpers.getPlatformFluidHelper().bucketVolume();
    }

    @Override
    public RecipeType<RecipeHolder<AltarRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return PoBlocks.OMINOUS_ALTAR.get().getName();
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AltarRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, CENTER_X, CENTER_Y).setBackground(slot, -1, -1).addIngredients(recipe.input());
        for (int i = 0; i < recipe.pedestals().size(); i++) {
            BlockPos offset = OminousAltarBlockEntity.PEDESTAL_OFFSETS.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, CENTER_X + offset.getX() * POSITION_SCALE,
                            CENTER_Y + offset.getZ() * POSITION_SCALE)
                    .setBackground(slot, -1, -1)
                    .addIngredients(recipe.pedestals().get(i));
        }
        builder.addSlot(RecipeIngredientRole.INPUT, FLUID_X, FLUID_Y)
                .setBackground(slot, -1, -1)
                .setFluidRenderer(Math.max(bucketVolume, recipe.fluid().getAmount()), false, 16, 16)
                .addFluidStack(recipe.fluid().getFluid(), recipe.fluid().getAmount());
        if (!recipe.result().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                    .setBackground(slot, -1, -1)
                    .addItemStack(recipe.result().copy());
        }
    }

    @Override
    public void draw(RecipeHolder<AltarRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, ARROW_X, CENTER_Y + 1);
        var font = Minecraft.getInstance().font;
        Component duration = Component.translatable("jei.poopsky.altar_duration", holder.value().duration());
        graphics.drawString(font, duration, getWidth() - font.width(duration), 2, 0x404040, false);
        if (!holder.value().description().isEmpty()) {
            if (holder.value().result().isEmpty()) {
                commandIcon.draw(graphics, OUTPUT_X, OUTPUT_Y + 1);
            }
            Component description = Component.translatable(holder.value().description());
            int labelWidth = font.width(description);
            int labelX = Mth.clamp(OUTPUT_X + (16 - labelWidth) / 2, 1, getWidth() - labelWidth - 1);
            graphics.drawString(font, description, labelX, OUTPUT_Y + 18, 0x404040, false);
        }
    }
}
