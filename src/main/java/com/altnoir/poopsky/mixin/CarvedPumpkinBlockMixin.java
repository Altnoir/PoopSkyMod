package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.event.PumkinBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {
    @Shadow
    private static void spawnGolemInWorld(Level level, BlockPattern.BlockPatternMatch patternMatch, Entity golem, BlockPos pos) {
    }

    @Shadow
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = state -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN));

    @Inject(method = "trySpawnGolem", at = @At("TAIL"))
    private void poopsky$trySpawn(Level level, BlockPos pos, CallbackInfo ci) {
        PumkinBlockEvents.SpawnResult result = PumkinBlockEvents.trySpawn(level, pos, PUMPKINS_PREDICATE);
        if (result == null || result.entity() == null) return;
        switch (result.type()) {
            case VILLAGER, BLAZE -> spawnGolemInWorld(level, result.match(), result.entity(), result.spawnPos());
        }
    }

    @Inject(method = "canSpawnGolem", at = @At("RETURN"), cancellable = true)
    private void poopsky$canSpawn(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (PumkinBlockEvents.canSpawnCustomGolem(level, pos)) {
            cir.setReturnValue(true);
        }
    }
}
