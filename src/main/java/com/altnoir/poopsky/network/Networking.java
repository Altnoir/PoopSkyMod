package com.altnoir.poopsky.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {
    public static final String VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar(VERSION);

        registrar.playBidirectional(
                RidePacket.TYPE,
                RidePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        RidePacketHandler::handleDataOnMain,
                        RidePacketHandler::handleDataOnMain
                )
        );

        registrar.playBidirectional(
                SummonBroomPacket.TYPE,
                SummonBroomPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        SummonBroomPacketHandler::handleDataOnMain,
                        SummonBroomPacketHandler::handleDataOnMain
                )
        );
    }
}
