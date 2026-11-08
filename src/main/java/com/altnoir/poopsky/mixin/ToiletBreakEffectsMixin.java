package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.client.ToiletClientBlockExtensions;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class ToiletBreakEffectsMixin {
    @Inject(method = "levelEvent", at = @At("HEAD"))
    private void poopsky$cacheMaterial(int event, BlockPos pos, int data, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (event == 2001 && level != null) {
            var state = Block.stateById(data);
            if (state.getBlock() instanceof AbstractToiletBlock) ToiletClientBlockExtensions.INSTANCE.playBreakSound(state, level, pos);
        }
    }
}
