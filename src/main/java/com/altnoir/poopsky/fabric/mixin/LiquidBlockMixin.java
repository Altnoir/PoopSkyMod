package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.fluidhandler.FluidInteractionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {
    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void poopsky$applyFluidInteractions(
            Level level,
            BlockPos pos,
            BlockState state,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (FluidInteractionRegistry.canInteract(level, pos)) {
            callback.setReturnValue(false);
        }
    }
}
