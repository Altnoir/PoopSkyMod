package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.item.PSItems;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    @Redirect(
            method = "canGather(Lnet/minecraft/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z",
                    ordinal = 0
            )
    )
    private boolean redirectCanGatherItems(Set<?> set, Object item) {
        return set.contains(item) || item == PSItems.MAGGOTS_SEEDS;
    }
}