package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherFungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetherFungusBlock.class)
public class FungusBlockMixin {
    @Shadow
    @Final
    private Block requiredBlock;

    @Unique
    private static boolean poopsky$shouldRestoreGround;

    @Unique
    private static BlockPos poopsky$groundPos;

    @Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
    private static void poopsky$isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockstate = level.getBlockState(pos.below());
        if (blockstate.is(PoBlocks.CHILI_POOP_BLOCK.get())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "performBonemeal", at = @At("HEAD"))
    private void poopsky$performBonemealHead(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        poopsky$shouldRestoreGround = false;
        poopsky$groundPos = null;
        BlockPos belowPos = pos.below();
        if (level.getBlockState(belowPos).is(PoBlocks.CHILI_POOP_BLOCK.get())) {
            level.setBlock(belowPos, this.requiredBlock.defaultBlockState(), 3);
            poopsky$shouldRestoreGround = true;
            poopsky$groundPos = belowPos;
        }
    }

    @Inject(method = "performBonemeal", at = @At("RETURN"))
    private static void poopsky$performBonemealReturn(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (poopsky$shouldRestoreGround && poopsky$groundPos != null) {
            level.setBlock(poopsky$groundPos, PoBlocks.CHILI_POOP_BLOCK.get().defaultBlockState(), 3);
            poopsky$shouldRestoreGround = false;
            poopsky$groundPos = null;
        }
    }
}
