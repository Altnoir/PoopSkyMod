package com.altnoir.poopsky.component;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PSComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PoopSky.MOD_ID);

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }
}
