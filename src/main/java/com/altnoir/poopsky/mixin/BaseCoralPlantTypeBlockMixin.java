package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.impl.PoTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {
    @ModifyReturnValue(method = "scanForWater", at = @At("RETURN"))
    private static boolean poopsky$scanForWater(boolean original, BlockState state, BlockGetter level, BlockPos pos) {
        if (original) return true;
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(PoTags.Blocks.WATER_BLOCK)) {
                return true;
            }
        }
        return false;
    }
}