package com.altnoir.poopsky.compat.create;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.compat.create.content.kinetics.fan.processing.DigestingFanProcessingType;
import com.altnoir.poopsky.compat.create.content.logistics.PSItemAttributes;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PFanProcessingTypes {
    public static DeferredHolder<FanProcessingType, DigestingFanProcessingType> DIGESTING;

    public static void register(IEventBus modEventBus) {
        PSRecipeTypes.register(modEventBus);
        PSItemAttributes.register(modEventBus);

        var TYPES = DeferredRegister.create(CreateRegistries.FAN_PROCESSING_TYPE, PoopSky.MOD_ID);

        DIGESTING = TYPES.register("digesting", DigestingFanProcessingType::new);

        TYPES.register(modEventBus);
    }
}