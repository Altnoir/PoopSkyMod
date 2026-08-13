package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.init.PoItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(value = Villager.class)
public class VillagerMixin {
    @Redirect(
            method = "eatUntilFull",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false, ordinal = 0)
    )
    private static Object redirectFoodPointsGet(Map<Item, Integer> map, Object item) {
        if (map == Villager.FOOD_POINTS && item == PoItems.POOP.get()) return 1;
        return item instanceof Item key ? map.get(key) : null;
    }

    @Redirect(
            method = "countFoodPointsInInventory",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", remap = false, ordinal = 0)
    )
    private static Set<Map.Entry<Item, Integer>> redirectEntrySet(Map<Item, Integer> item) {
        if (item == Villager.FOOD_POINTS) {
            Map<Item, Integer> FoodPoints = new HashMap<>(item);
            FoodPoints.put(PoItems.POOP.get(), 1);
            return FoodPoints.entrySet();
        }
        return item.entrySet();
    }

    @Inject(method = "wantsToPickUp", at = @At("HEAD"), cancellable = true)
    private void poopsky$acceptPoopAndMaggots(ServerLevel level, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if ((itemStack.is(PoItems.POOP) || itemStack.is(PoItems.MAGGOTS_SEEDS))
                && ((Villager) (Object) this).getInventory().canAddItem(itemStack)) {
            cir.setReturnValue(true);
        }
    }
}
