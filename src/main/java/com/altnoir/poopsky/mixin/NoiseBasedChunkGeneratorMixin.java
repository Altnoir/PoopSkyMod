package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.Config;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
    @ModifyReturnValue(method = "createFluidPicker", at = @At("RETURN"))
    private static Aquifer.FluidPicker poopsky$createFluidPicker(Aquifer.FluidPicker original, NoiseGeneratorSettings settings) {
        if (!Config.lavaFluid) {
            return original;
        }
        Aquifer.FluidStatus fluidStatus = new Aquifer.FluidStatus(settings.seaLevel(), settings.defaultFluid());
        return (x, y, z) -> fluidStatus;
    }
}
