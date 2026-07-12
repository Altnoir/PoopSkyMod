package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.network.PlugActionPayload;
import com.altnoir.poopsky.impl.network.PlugDismountPayload;
import com.altnoir.poopsky.impl.network.PlugInputPayload;
import com.altnoir.poopsky.impl.network.TimeBellFreezePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PoNetworking {
    private static final String VERSION = "1";

    @SubscribeEvent
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