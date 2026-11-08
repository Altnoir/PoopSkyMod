package com.altnoir.poopsky.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class PoStackItemEntityMixin {
    @ModifyConstant(method = "merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            constant = @Constant(intValue = 64))
    private static int poopsky$mergeOriginalStacks(int limit, ItemEntity entity, ItemStack target, ItemStack source) {
        // 否则80+8会算出负数的可合并数量，导致错误拆分。
        return target.getMaxStackSize() == 88 ? 88 : limit;
    }
}
