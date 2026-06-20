package com.altnoir.poopsky.compat.jei.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.create.PSRecipeTypes;
import com.altnoir.poopsky.compat.jei.create.animations.AnimatedUrineLiquid;
import com.altnoir.poopsky.compat.create.content.kinetics.fan.processing.DigestingRecipe;
import com.altnoir.poopsky.item.PItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class FanDigestingCategory extends ProcessingViaFanCategory.MultiOutput<DigestingRecipe> {

    public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<DigestingRecipe>> TYPE = mezz.jei.api.recipe.RecipeType.createRecipeHolderType(PSRecipeTypes.DIGESTING.getId());
    private final AnimatedUrineLiquid urineLiquid = new AnimatedUrineLiquid();

    public static FanDigestingCategory create() {
        var id = PoopSky.loc("fan_digesting");
        var title = Component.translatable("jei.category." + PoopSky.MOD_ID + ".digesting");
        var background = new EmptyBackground(178, 72);
        var icon = new DoubleItemIcon(AllItems.PROPELLER::asStack, () -> new ItemStack(PItems.URINE_BUCKET.get()));
        var catalyst = AllBlocks.ENCASED_FAN.asStack();
        catalyst.set(DataComponents.CUSTOM_NAME, Component.translatable("jei.category." + PoopSky.MOD_ID + ".digesting.fan").withStyle(style -> style.withItalic(false)));
        var info = new Info<>(TYPE, title, background, icon, FanDigestingCategory::getAllRecipes, List.of(() -> catalyst));
        return new FanDigestingCategory(info);
    }

    public FanDigestingCategory(Info<DigestingRecipe> info) {
        super(info);
    }

    @Override
    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_LIGHT;
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        urineLiquid.draw(graphics, 0, 0);
    }

    private static List<RecipeHolder<DigestingRecipe>> getAllRecipes() {
        var level = Minecraft.getInstance().level;
        if (level == null) return List.of();
        return level.getRecipeManager().getAllRecipesFor(PSRecipeTypes.DIGESTING.getType());
    }
}