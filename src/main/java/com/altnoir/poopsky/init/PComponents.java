package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.ToiletComponent;
import com.altnoir.poopsky.init.PFlyTypes.FlyType;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToiletComponent>> TOILET_COMPONENT = REGISTRAR.registerComponentType(
            "toilet_component",
            builder -> builder
                    .persistent(ToiletComponent.BASIC_CODEC)
                    .networkSynchronized(ToiletComponent.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FLY_TYPE = REGISTRAR.registerComponentType(
            "fly_type",
            builder -> builder
                    .persistent(ExtraCodecs.NON_EMPTY_STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
    );

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }
}
