package com.altnoir.poopsky.compat.create.content.kinetics.fan.processing;

import com.altnoir.poopsky.compat.create.PSRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

public class DigestingRecipe extends ProcessingRecipe<Container> {
    public DigestingRecipe(ProcessingRecipeParams params) { super(PSRecipeTypes.DIGESTING, params); }
    @Override public boolean matches(Container inv, Level level) {
        return !inv.isEmpty() && ingredients.get(0).test(inv.getItem(0));
    }
    @Override protected int getMaxInputCount() { return 1; }
    @Override protected int getMaxOutputCount() { return 12; }
    public static ProcessingRecipeBuilder<DigestingRecipe> builder(ResourceLocation id) {
        return new ProcessingRecipeBuilder<>(DigestingRecipe::new, id);
    }
}
