package com.altnoir.poopsky.compat.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record LiquidInteractionRecipe(
        ResourceLocation id,
        Fluid leftFluid,
        @Nullable Fluid rightFluid,
        @Nullable Block rightBlock,
        @Nullable Block contextBlock,
        List<Block> outputs
) {
    public boolean isFluidFluid() {
        return rightFluid != null;
    }

    public boolean hasContext() {
        return contextBlock != null;
    }
}