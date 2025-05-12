package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.block.PSBlocks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {
    @Unique
    private BlockPattern poopsky$villagerPattern;
    @Unique
    private BlockPattern poopsky$villagerDispenserPattern;

    @Shadow
    private static void spawnEntity(World world, BlockPattern.Result patternResult, Entity entity, BlockPos pos) {}

    @Shadow
    private static final Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE = state -> state != null
            && (state.isOf(Blocks.CARVED_PUMPKIN) || state.isOf(Blocks.JACK_O_LANTERN));

    @Unique
    private BlockPattern poopsky$getVillagerPattern() {
        if (this.poopsky$villagerPattern == null) {
            this.poopsky$villagerPattern = BlockPatternBuilder.start()
                    .aisle("^", "#")
                    .where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(PSBlocks.POOP_BLOCK)))
                    .build();
        }
        return this.poopsky$villagerPattern;
    }
    @Unique
    private BlockPattern poopsky$getVillagerDispenserPattern() {
        if (this.poopsky$villagerDispenserPattern == null) {
            this.poopsky$villagerDispenserPattern = BlockPatternBuilder.start()
                    .aisle(" ", "#")
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(PSBlocks.POOP_BLOCK)))
                    .build();
        }
        return this.poopsky$villagerDispenserPattern;
    }

    @Inject(method = "trySpawnEntity", at = @At("TAIL"))
    private void injectVillagerSpawn(World world, BlockPos pos, CallbackInfo ci) {
        BlockPattern.Result result = this.poopsky$getVillagerPattern().searchAround(world, pos);
        if (result != null) {
            VillagerEntity villager = EntityType.VILLAGER.create(world);
            if (villager != null) {
                villager.setBaby(true);
                spawnEntity(world, result, villager, result.translate(0, 1, 0).getBlockPos());
            }
        }
    }
    @ModifyReturnValue(method = "canDispense", at = @At("RETURN"))
    private boolean modifyCanDispense(boolean original, WorldView world, BlockPos pos) {
        return original || this.poopsky$getVillagerDispenserPattern().searchAround(world, pos) != null;
    }
}
