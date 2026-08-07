package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.extension.IBlockExtension;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {
    @Inject(method = "isSticky", at = @At("HEAD"), cancellable = true)
    private static void poopsky$isSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (((IBlockExtension) state.getBlock()).isStickyBlock(state)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
    private static void poopsky$canStickToEachOther(BlockState state, BlockState other,
                                                    CallbackInfoReturnable<Boolean> cir) {
        IBlockExtension block = (IBlockExtension) state.getBlock();
        IBlockExtension otherBlock = (IBlockExtension) other.getBlock();
        if (!block.canStickTo(state, other) || !otherBlock.canStickTo(other, state)) {
            cir.setReturnValue(false);
        } else if (block.isStickyBlock(state) || otherBlock.isStickyBlock(other)) {
            cir.setReturnValue(true);
        }
    }
}
