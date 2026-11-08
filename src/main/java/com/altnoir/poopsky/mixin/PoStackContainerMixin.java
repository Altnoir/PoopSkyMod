package com.altnoir.poopsky.mixin;

import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/** 允许原版普通容器容纳原作的 88 堆叠；每种物品自身的上限仍然生效。 */
@Mixin(Container.class)
public interface PoStackContainerMixin {
    /**
     * @author AutoMods
     * @reason 1.20.1 默认容器上限为64，会截断原作允许的88堆叠。
     * 特殊容器和单件槽位重写的方法不受影响。
     */
    @Overwrite
    default int getMaxStackSize() {
        return 88;
    }
}
