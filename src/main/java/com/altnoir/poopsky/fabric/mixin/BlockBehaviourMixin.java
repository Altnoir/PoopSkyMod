package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.extension.IBlockExtension;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @ModifyExpressionValue(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropFromExplosion(Lnet/minecraft/world/level/Explosion;)Z"))
    public boolean injected(boolean original, BlockState state, Level level, BlockPos pos, Explosion explosion) {
        return state.canDropFromExplosion(level, pos, explosion);
    }

    @Mixin(BlockBehaviour.BlockStateBase.class)
    public static abstract class BlockStateBaseMixin {
        @Shadow
        public abstract Block getBlock();

        @Shadow
        protected abstract BlockState asState();

        @Shadow
        @Final
        private MapColor mapColor;

        @WrapOperation(method = "getMapColor", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;mapColor:Lnet/minecraft/world/level/material/MapColor;", opcode = Opcodes.GETFIELD))
        public MapColor patchGetMapColor(BlockBehaviour.BlockStateBase instance, Operation<MapColor> original, BlockGetter level, BlockPos pos) {
            if (getBlock() instanceof IBlockExtension extension) {
                return extension.getMapColor(asState(), level, pos, this.mapColor);
            } else {
                return original.call(instance);
            }
        }
    }
}
