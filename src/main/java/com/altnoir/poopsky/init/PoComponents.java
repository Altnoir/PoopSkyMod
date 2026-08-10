package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.ToiletComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PoComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PoopSky.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToiletComponent>> TOILET_COMPONENT =
            COMPONENTS.registerComponentType("toilet_component", builder -> builder
                    .persistent(ToiletComponent.BASIC_CODEC)
                    .networkSynchronized(ToiletComponent.STREAM_CODEC)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FLY_TYPE =
            COMPONENTS.registerComponentType("fly_type", builder -> builder
                    .persistent(ExtraCodecs.NON_EMPTY_STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> GACHA_ENTITY =
            COMPONENTS.registerComponentType("gacha_entity", builder -> builder
                    .persistent(ExtraCodecs.NON_EMPTY_STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToiletType>> TOILET_TYPE =
            COMPONENTS.registerComponentType("toilet_type", builder -> builder
                    .persistent(ToiletType.CODEC)
                    .networkSynchronized(ToiletType.STREAM_CODEC)
            );

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
