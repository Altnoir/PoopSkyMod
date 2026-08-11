package com.altnoir.poopsky.compat.create.content.kinetics.fan.processing;

import com.altnoir.poopsky.compat.create.PSRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigestingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {

    public DigestingRecipe(ProcessingRecipeParams params) {
        super(PSRecipeTypes.DIGESTING, params);
    }

    @Override
    public boolean matches(SingleRecipeInput inv, Level worldIn) {
        return !inv.isEmpty() && ingredients.getFirst().test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    public static Builder<DigestingRecipe> builder(Identifier id) {
        return new Builder<>(DigestingRecipe::new, id);
    }
}