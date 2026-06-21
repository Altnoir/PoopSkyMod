package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.PTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {
    @Inject(method = "scanForWater", at = @At("HEAD"), cancellable = true)
    private static void injectScanForWater(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(PTags.Blocks.WATER_BLOCK)) {
                cir.setReturnValue(true);
            }
        }
    }
}
