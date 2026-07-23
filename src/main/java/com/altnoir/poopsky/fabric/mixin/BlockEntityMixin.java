package com.altnoir.poopsky.fabric.mixin;

import com.altnoir.poopsky.fabric.port.extension.IBlockEntityExtension;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements IBlockEntityExtension {
}
