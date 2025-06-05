package com.altnoir.poopsky.component;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PSComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PPlug>> PPLUG = REGISTRAR.registerComponentType(
            "pplug",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(PPlug.PLUG_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(PPlug.PLUG_STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }
}
