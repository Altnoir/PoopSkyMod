package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoBlocks;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FungusBlock.class)
public class FungusBlockMixin {
    @Shadow
    @Final
    private Block requiredBlock;

    @Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
    private void poopsky$isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockstate = level.getBlockState(pos.below());
        if (blockstate.is(PoBlocks.CHILI_POOP_BLOCK.get())) {
            cir.setReturnValue(true);
        }
    }

    @WrapMethod(method = "performBonemeal")
    private void poopsky$performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state,
                                         Operation<Void> original) {
        BlockPos belowPos = pos.below();
        if (!level.getBlockState(belowPos).is(PoBlocks.CHILI_POOP_BLOCK.get())) {
            original.call(level, random, pos, state);
            return;
        }

        level.setBlock(belowPos, this.requiredBlock.defaultBlockState(), 3);
        try {
            original.call(level, random, pos, state);
        } finally {
            level.setBlock(belowPos, PoBlocks.CHILI_POOP_BLOCK.get().defaultBlockState(), 3);
        }
    }
}
