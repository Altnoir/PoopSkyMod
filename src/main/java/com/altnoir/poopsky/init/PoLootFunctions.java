package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.SetToiletTypeFunction;
import com.altnoir.poopsky.impl.registrate.PoRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class PoLootFunctions {
    private static final PoRegistrate REGISTRATE = PoopSky.registrate();

    public static final RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<SetToiletTypeFunction>> SET_TOILET_TYPE = REGISTRATE.simple("set_toilet_type", Registries.LOOT_FUNCTION_TYPE,
            () -> new LootItemFunctionType<>(SetToiletTypeFunction.CODEC));

    public static void register() {
    }
}
