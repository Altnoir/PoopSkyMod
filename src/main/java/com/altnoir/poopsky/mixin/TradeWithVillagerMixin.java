package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(value = TradeWithVillager.class)
public class TradeWithVillagerMixin {
    @Redirect(
            method = "tick*",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", remap = false, ordinal = 0)
    )
    private Set<Item> redirectFoodPointsKeySet(Map<Item, Integer> item) {
        if (item == Villager.FOOD_POINTS) {
            Set<Item> originalKeys = new HashSet<>(item.keySet());
            originalKeys.add(PoItems.POOP.get());
            return originalKeys;
        }
        return item.keySet();
    }
}
