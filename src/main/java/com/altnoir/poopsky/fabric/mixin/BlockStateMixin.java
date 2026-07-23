package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.extension.IBlockStateExtension;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public class BlockStateMixin implements IBlockStateExtension {
}
