package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.block.PBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(value = CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {
    @Unique
    private BlockPattern poopsky$villager;
    @Unique
    private BlockPattern poopsky$villagerDispenser;

    @Shadow
    private static void spawnGolemInWorld(Level level, BlockPattern.BlockPatternMatch patternMatch, Entity golem, BlockPos pos) {
    }

    @Shadow
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = state -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN));

    @Unique
    private BlockPattern poopsky$getVillager() {
        if (this.poopsky$villager == null) {
            this.poopsky$villager = BlockPatternBuilder.start()
                    .aisle("^", "#")
                    .where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE))
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.POOP_BLOCK.get())))
                    .build();
        }
        return this.poopsky$villager;
    }

    @Unique
    private BlockPattern poopsky$getVillagerDispenser() {
        if (this.poopsky$villagerDispenser == null) {
            this.poopsky$villagerDispenser = BlockPatternBuilder.start()
                    .aisle(" ", "#")
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(PBlocks.POOP_BLOCK.get())))
                    .build();
        }
        return this.poopsky$villagerDispenser;
    }

    @Inject(method = "trySpawnGolem", at = @At("TAIL"))
    private void poopsky$trySpawn(Level level, BlockPos pos, CallbackInfo ci) {
        BlockPattern.BlockPatternMatch patternMatch = poopsky$getVillager().find(level, pos);
        if (patternMatch != null) {
            Villager villager = EntityType.VILLAGER.create(level);
            if (villager != null) {
                villager.setBaby(true);
                spawnGolemInWorld(level, patternMatch, villager,
                        patternMatch.getBlock(0, 1, 0).getPos());
            }
        }
    }

    @Inject(method = "canSpawnGolem", at = @At("RETURN"), cancellable = true)
    private void poopsky$canSpawn(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (poopsky$getVillagerDispenser().find(level, pos) != null) {
            cir.setReturnValue(true);
        }
    }
}
