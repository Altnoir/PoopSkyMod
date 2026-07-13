package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PoNetworking {
    private static final String VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PoopSky.MOD_ID).versioned(VERSION);
        registrar.playToServer(
                PlugActionPayload.TYPE,
                PlugActionPayload.CODEC,
                PlugActionPayload::handle
        );
        registrar.playToServer(
                PlugDismountPayload.TYPE,
                PlugDismountPayload.CODEC,
                PlugDismountPayload::handle
        );
        registrar.playToServer(
                PlugInputPayload.TYPE,
                PlugInputPayload.CODEC,
                PlugInputPayload::handle
        );
        registrar.playToClient(
                TimeBellFreezePayload.TYPE,
                TimeBellFreezePayload.CODEC,
                TimeBellFreezePayload::handle
        );
    }
}