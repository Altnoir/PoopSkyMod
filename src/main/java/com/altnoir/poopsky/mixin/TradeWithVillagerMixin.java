package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(TradeWithVillager.class)
public class TradeWithVillagerMixin {
    @WrapOperation(
            method = "tick*",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", remap = false, ordinal = 0)
    )
    private Set<Item> poopsky$foodPointsKeySet(Map<Item, Integer> item, Operation<Set<Item>> original) {
        if (item == Villager.FOOD_POINTS) {
            Set<Item> originalKeys = new HashSet<>(original.call(item));
            originalKeys.add(PoItems.POOP.get());
            return originalKeys;
        }
        return original.call(item);
    }
}
