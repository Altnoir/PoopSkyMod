package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CropBlock.class, StemBlock.class, AttachedStemBlock.class, PitcherCropBlock.class})
public class FarmlandPlantBlockMixin {
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void poopsky$allowPoopFarmland(BlockState state, BlockGetter level, BlockPos pos,
                                           CallbackInfoReturnable<Boolean> cir) {
        if (state.is(PoBlocks.POOP_FARMLAND.get())) {
            cir.setReturnValue(true);
        }
    }
}
