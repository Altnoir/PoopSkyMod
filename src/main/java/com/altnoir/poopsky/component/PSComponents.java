package com.altnoir.poopsky.component;

import com.altnoir.poopsky.PoopSky;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class PSComponents {
    public static final ComponentType<ToiletComponent> TOILET_COMPONENT =
            register("toilet_data", builder -> builder.codec(ToiletComponent.CODEC));

    public record ToiletComponent(String world1,String world2, int x1, int y1, int z1, int x2, int y2, int z2) {
        public static final Codec<ToiletComponent> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(
                    Codec.STRING.fieldOf("world1").forGetter(ToiletComponent::world1),
                    Codec.STRING.fieldOf("world2").forGetter(ToiletComponent::world2),
                    Codec.INT.fieldOf("x1").forGetter(ToiletComponent::x1),
                    Codec.INT.fieldOf("y1").forGetter(ToiletComponent::y1),
                    Codec.INT.fieldOf("z1").forGetter(ToiletComponent::z1),
                    Codec.INT.fieldOf("x2").forGetter(ToiletComponent::x2),
                    Codec.INT.fieldOf("y2").forGetter(ToiletComponent::y2),
                    Codec.INT.fieldOf("z2").forGetter(ToiletComponent::z2)
            ).apply(builder, ToiletComponent::new);
        });
    }

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>>  builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(PoopSky.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }
    public static void registerComponents() {
        PoopSky.LOGGER.info("Registering Components for " + PoopSky.MOD_ID);
    }

}
