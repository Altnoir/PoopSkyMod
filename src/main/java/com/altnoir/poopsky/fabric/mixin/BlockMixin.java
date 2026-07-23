package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.extension.IBlockExtension;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public class BlockMixin implements IBlockExtension {
}
