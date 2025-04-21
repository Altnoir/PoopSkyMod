package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.block.PSBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {
    @Redirect(
            method = "isBlockSticky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    private static boolean modifyStickyCheck(BlockState state, Block block) {
        return state.isOf(block) || state.isOf(PSBlocks.POOP_BLOCK);
    }
    @Inject(
            method = "isAdjacentBlockStuck",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/BlockState;)Z",
                    ordinal = 0
            ),
            cancellable = true
    )
    private static void preventPoopStick(BlockState state, BlockState adjacentState, CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(PSBlocks.POOP_BLOCK) && adjacentState.isOf(Blocks.SLIME_BLOCK)) {
            cir.setReturnValue(false);
        } else if (state.isOf(PSBlocks.POOP_BLOCK) && adjacentState.isOf(Blocks.HONEY_BLOCK)) {
            cir.setReturnValue(false);
        } else if (state.isOf(Blocks.SLIME_BLOCK) && adjacentState.isOf(PSBlocks.POOP_BLOCK)) {
            cir.setReturnValue(false);
        } else if (state.isOf(Blocks.HONEY_BLOCK) && adjacentState.isOf(PSBlocks.POOP_BLOCK)) {
            cir.setReturnValue(false);
        }
    }
}
