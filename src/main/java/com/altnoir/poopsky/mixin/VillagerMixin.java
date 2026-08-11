package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(value = Villager.class)
public class VillagerMixin {
    @Redirect(
            method = "eatUntilFull",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false, ordinal = 0)
    )
    public Object redirectFoodPointsGet(Map<Item, Integer> map, Object item) {
        if (map == Villager.FOOD_POINTS && item == PoItems.POOP.get()) return 1;
        return map.get(item);
    }

    @Redirect(
            method = "countFoodPointsInInventory",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", remap = false, ordinal = 0)
    )
    public Set<Map.Entry<Item, Integer>> redirectEntrySet(Map<Item, Integer> item) {
        if (item == Villager.FOOD_POINTS) {
            Map<Item, Integer> FoodPoints = new HashMap<>(item);
            FoodPoints.put(PoItems.POOP.get(), 1);
            return FoodPoints.entrySet();
        }
        return item.entrySet();
    }

    @Redirect(
            method = "wantsToPickUp",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z", remap = false, ordinal = 0)
    )
    public boolean wantsToPickUp(Set<Item> set, Object item) {
        return set.contains(item) || item == PoItems.POOP.get() || item == PoItems.MAGGOTS_SEEDS.get();
    }
}