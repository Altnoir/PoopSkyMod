package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.extension.IBlockExtension;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/core/dispenser/DispenseItemBehavior$8")
public class DispenseItemBehaviorMixin {
    @Definition(id = "TntBlock", type = TntBlock.class)
    @Definition(id = "blockState", local = @Local(type = BlockState.class))
    @Definition(id = "getBlock", method = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;")
    @Expression("blockState.getBlock() instanceof TntBlock")
    @WrapOperation(method = "execute", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkFlammable(Object object, Operation<Boolean> original, BlockSource source, @Local BlockPos blockPos, @Local BlockState blockState) {
        IBlockExtension block = blockState.getBlock();
        return block.isFlammable(blockState, source.level(), blockPos, source.state().getValue(DispenserBlock.FACING).getOpposite());

    }

    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private void onCaughtFire(Level level, BlockPos pos, Operation<Void> original, BlockSource source, @Local BlockState blockState) {
        IBlockExtension block = blockState.getBlock();
        block.onCaughtFire(blockState, level, pos, source.state().getValue(DispenserBlock.FACING).getOpposite(), null);
    }
}
