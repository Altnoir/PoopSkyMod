package com.altnoir.poopsky.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GlassBottleItem.class)
public interface GlassBottleItemAccessor {
    @Invoker("fill")
    ItemStack invokeFill(ItemStack stack, PlayerEntity player, ItemStack outputStack);
}