package com.altnoir.poopsky.component;

import com.altnoir.poopsky.PoopSky;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PSComponents {

    public static final ComponentType<ToiletComponent> TOILET_COMPONENT_1 = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(PoopSky.MOD_ID, "toilet_data_1"),
            ComponentType.<ToiletComponent>builder().codec(ToiletComponent.CODEC).build()
    );
    public static final ComponentType<ToiletComponent> TOILET_COMPONENT_2 = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(PoopSky.MOD_ID, "toilet_data_2"),
            ComponentType.<ToiletComponent>builder().codec(ToiletComponent.CODEC).build()
    );
    public static final ComponentType<Integer> POOP_BALL_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(PoopSky.MOD_ID, "poop_ball_component"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public record ToiletComponent(String world, int x, int y, int z) {
        public static final Codec<ToiletComponent> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(
                    Codec.STRING.fieldOf("world").forGetter(ToiletComponent::world),
                    Codec.INT.fieldOf("x").forGetter(ToiletComponent::x),
                    Codec.INT.fieldOf("y").forGetter(ToiletComponent::y),
                    Codec.INT.fieldOf("z").forGetter(ToiletComponent::z)
            ).apply(builder, ToiletComponent::new);
        });
    }

    public static void registerComponents() {
        PoopSky.LOGGER.info("Registering Components for " + PoopSky.MOD_ID);
    }

}
