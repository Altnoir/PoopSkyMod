package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.content.entity.p.BasiliskEntity;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoFluids;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrogspawnBlock.class)
public class FrogspawnBlockMixin {
    @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
    private boolean poopsky$surviveOnUrine(boolean original, BlockState state, LevelReader level, BlockPos pos) {
        return original
                || level.getFluidState(pos.below()).is(PoFluids.URINE.get())
                || level.getFluidState(pos.below()).is(PoFluids.FLOWING_URINE.get());
    }

    @WrapMethod(method = "tick")
    private void poopsky$hatchBasilisks(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                        Operation<Void> original) {
        if (!level.getFluidState(pos.below()).is(PoFluids.URINE.get())
                && !level.getFluidState(pos.below()).is(PoFluids.FLOWING_URINE.get())) {
            original.call(state, level, pos, random);
            return;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        for (int i = 0; i < random.nextInt(2) + 2; i++) {
            BasiliskEntity basilisk = PoEntityType.BASILISK.get().create(level);
            if (basilisk != null) {
                basilisk.moveTo(pos.getX() + 0.2 + random.nextDouble() * 0.6, pos.getY(),
                        pos.getZ() + 0.2 + random.nextDouble() * 0.6, random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(basilisk);
            }
        }
    }
}
