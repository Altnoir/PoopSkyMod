package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.common.SetToiletTypeFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PLootFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, PoopSky.MOD_ID);

    public static final Supplier<LootItemFunctionType<SetToiletTypeFunction>> SET_TOILET_TYPE = LOOT_FUNCTIONS.register("set_toilet_type",
            () -> new LootItemFunctionType<>(SetToiletTypeFunction.CODEC));

    public static void register(IEventBus eventBus) {
        LOOT_FUNCTIONS.register(eventBus);
    }
}
