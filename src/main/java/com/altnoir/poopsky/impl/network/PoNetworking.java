package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PoNetworking {
    private static final String VERSION = "1";

    public static void registerNetworking(RegisterPayloadHandlersEvent event) {
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
        registrar.playToServer(
                FlushToiletCartInputPayload.TYPE,
                FlushToiletCartInputPayload.CODEC,
                FlushToiletCartInputPayload::handle
        );
        registrar.playToClient(
                TimeBellFreezePayload.TYPE,
                TimeBellFreezePayload.CODEC,
                TimeBellFreezePayload::handle
        );
        registrar.playToClient(
                ReturnTotemActivationPayload.TYPE,
                ReturnTotemActivationPayload.CODEC,
                ReturnTotemActivationPayload::handle
        );
        registrar.playToClient(
                PlayAnimationPayload.TYPE,
                PlayAnimationPayload.CODEC,
                PlayAnimationPayload::handle
        );
        registrar.playToClient(
                PlayAnimationAndWaitPayload.TYPE,
                PlayAnimationAndWaitPayload.CODEC,
                PlayAnimationAndWaitPayload::handle
        );
        registrar.playToServer(
                AnimationFinishedPayload.TYPE,
                AnimationFinishedPayload.CODEC,
                AnimationFinishedPayload::handle
        );
        registrar.playToServer(
                ArcadeInputPacket.TYPE,
                ArcadeInputPacket.STREAM_CODEC,
                ArcadeInputPacket::handle
        );
        registrar.playToServer(
                ArcadeResetPacket.TYPE,
                ArcadeResetPacket.STREAM_CODEC,
                ArcadeResetPacket::handle
        );
        registrar.playToClient(
                ArcadeGameSnapshotPacket.TYPE,
                ArcadeGameSnapshotPacket.STREAM_CODEC,
                ArcadeGameSnapshotPacket::handle
        );
        IntroHandshake.registerPayloads(registrar);
    }

    public static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
        IntroHandshake.registerTask(event);
    }
}
