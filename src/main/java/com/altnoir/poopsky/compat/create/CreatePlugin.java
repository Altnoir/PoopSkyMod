package com.altnoir.poopsky.compat.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.create.content.kinetics.fan.processing.DigestingFanProcessingType;
import com.altnoir.poopsky.compat.create.content.logistics.PSItemAttributes;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class CreatePlugin {
    public static RegistryObject<DigestingFanProcessingType> FAN_DIGESTING;

    public static void register(IEventBus modEventBus) {
        var TYPES = DeferredRegister.create(CreateRegistries.FAN_PROCESSING_TYPE, PoopSky.MOD_ID);

        FAN_DIGESTING = TYPES.register("fan_digesting", DigestingFanProcessingType::new);

        PSRecipeTypes.register(modEventBus);
        PSItemAttributes.register(modEventBus);
        TYPES.register(modEventBus);
    }
}