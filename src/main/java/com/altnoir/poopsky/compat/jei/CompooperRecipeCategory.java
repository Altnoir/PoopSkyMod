package com.altnoir.poopsky.compat.jei;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.recipe.CompooperRecipe;
import com.altnoir.poopsky.init.PoBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CompooperRecipeCategory implements IRecipeCategory<RecipeHolder<CompooperRecipe>> {
    public static final RecipeType<RecipeHolder<CompooperRecipe>> TYPE =
            RecipeType.createRecipeHolderType(PoopSky.loc("compooper"));

    private static final int WIDTH = 140;
    private static final int HEIGHT = 48;

    private final IDrawable icon;
    private final Component title;
    private final IDrawable arrow;
    private final IDrawable slot;

    private final IModIdHelper modIdHelper;

    public CompooperRecipeCategory(IJeiHelpers helpers, IDrawable arrow) {
        var helper = helpers.getGuiHelper();

        this.icon = helper.createDrawableItemStack(new ItemStack(PoBlocks.COMPOOPER.get()));
        this.title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".compooper");
        this.arrow = arrow;
        this.slot = helper.getSlotDrawable();
        this.modIdHelper = helpers.getModIdHelper();
    }

    @Override
    public RecipeType<RecipeHolder<CompooperRecipe>> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CompooperRecipe> recipeHolder, IFocusGroup focuses) {
        CompooperRecipe recipe = recipeHolder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 18).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18).addItemStack(recipe.output());
    }

    @Override
    public void draw(RecipeHolder<CompooperRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        CompooperRecipe recipe = recipeHolder.value();
        BlockState state = getFluidBlockState(recipe.fluidType());

        this.slot.draw(guiGraphics, 3, 17);
        this.arrow.draw(guiGraphics, 28, 18);
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(54.0F, 10.0F, 0.0F);
        poseStack.scale(2.0F, 2.0F, 1.0F);
        guiGraphics.renderItem(new ItemStack(state.getBlock()), 0, 0);
        poseStack.popPose();
        this.arrow.draw(guiGraphics, 90, 18);
        this.slot.draw(guiGraphics, 119, 17);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<CompooperRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        CompooperRecipe recipe = recipeHolder.value();
        if (54 < mouseX && mouseX < 86 && 10 < mouseY && mouseY < 42) {
            BlockState state = getFluidBlockState(recipe.fluidType());
            Block block = state.getBlock();
            String modId = BuiltInRegistries.BLOCK.getKey(block).getNamespace();

            tooltip.add(Component.translatable(block.getDescriptionId()));
            tooltip.add(this.modIdHelper.getFormattedModNameComponentForModId(modId));
        }
    }

    private static BlockState getFluidBlockState(String fluidType) {
        return switch (fluidType) {
            case "water" -> PoBlocks.WATER_COMPOOPER.get().defaultBlockState();
            case "lava" -> PoBlocks.LAVA_COMPOOPER.get().defaultBlockState();
            case "powder_snow" -> PoBlocks.POWDER_SNOW_COMPOOPER.get().defaultBlockState();
            case "urine" -> PoBlocks.URINE_COMPOOPER.get().defaultBlockState();
            default -> Blocks.COMPOSTER.defaultBlockState();
        };
    }
}
