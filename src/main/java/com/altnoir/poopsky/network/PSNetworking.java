package com.altnoir.poopsky.network;

import com.altnoir.poopsky.PoopSky;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

public class PSNetworking {
    private static final String VERSION = "1";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event){
        var registrar = event.registrar(PoopSky.MOD_ID).versioned(VERSION);
        registrar.playBidirectional(
                PlugActionPayload.TYPE,
                PlugActionPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        PlugActionPayload::handle,
                        PlugActionPayload::handle
                )
        );
        registrar.playBidirectional(
                PlugDismountPayload.TYPE,
                PlugDismountPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        PlugDismountPayload::handle,
                        PlugDismountPayload::handle
                )
        );
        registrar.playBidirectional(
                PlugInputPayload.TYPE,
                PlugInputPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        PlugInputPayload::handle,
                        PlugInputPayload::handle
                )
        );
    }
}
