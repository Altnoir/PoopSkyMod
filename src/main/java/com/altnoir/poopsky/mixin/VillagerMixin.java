package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(value = Villager.class)
public class VillagerMixin {
    @WrapOperation(
            method = "eatUntilFull",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false, ordinal = 0)
    )
    private Object poopsky$foodPointsGet(Map<Item, Integer> map, Object item, Operation<Object> original) {
        Object value = original.call(map, item);
        if (value == null && map == Villager.FOOD_POINTS && item == PoItems.POOP.get()) return 1;
        return value;
    }

    @WrapOperation(
            method = "countFoodPointsInInventory",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", remap = false, ordinal = 0)
    )
    private Set<Map.Entry<Item, Integer>> poopsky$foodPointsEntrySet(Map<Item, Integer> item,
                                                                    Operation<Set<Map.Entry<Item, Integer>>> original) {
        if (item == Villager.FOOD_POINTS) {
            Map<Item, Integer> foodPoints = new HashMap<>();
            original.call(item).forEach(entry -> foodPoints.put(entry.getKey(), entry.getValue()));
            foodPoints.putIfAbsent(PoItems.POOP.get(), 1);
            return foodPoints.entrySet();
        }
        return original.call(item);
    }

    @WrapOperation(
            method = "wantsToPickUp",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z", remap = false, ordinal = 0)
    )
    private boolean poopsky$wantsToPickUp(Set<Item> set, Object item, Operation<Boolean> original) {
        return original.call(set, item) || item == PoItems.POOP.get() || item == PoItems.MAGGOTS_SEEDS.get();
    }
}
