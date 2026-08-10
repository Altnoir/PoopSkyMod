package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.fabric.FabricatedNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PoNetworking {
    private static final String VERSION = "1";

    public static void registerNetworking() {
        FabricatedNetworking.registerC2SPacket(
                PlugActionPayload.TYPE,
                PlugActionPayload.CODEC,
                PlugActionPayload::handle
        );
        FabricatedNetworking.registerC2SPacket(
                PlugDismountPayload.TYPE,
                PlugDismountPayload.CODEC,
                PlugDismountPayload::handle
        );
        FabricatedNetworking.registerC2SPacket(
                PlugInputPayload.TYPE,
                PlugInputPayload.CODEC,
                PlugInputPayload::handle
        );
        FabricatedNetworking.registerC2SPacket(
                FlushToiletCartInputPayload.TYPE,
                FlushToiletCartInputPayload.CODEC,
                FlushToiletCartInputPayload::handle
        );
        FabricatedNetworking.registerS2CPacket(
                TimeBellFreezePayload.TYPE,
                TimeBellFreezePayload.CODEC
        );
        FabricatedNetworking.registerS2CPacket(
                ReturnTotemActivationPayload.TYPE,
                ReturnTotemActivationPayload.CODEC
        );
        FabricatedNetworking.registerS2CPacket(
                PlayAnimationPayload.TYPE,
                PlayAnimationPayload.CODEC
        );
        FabricatedNetworking.registerS2CPacket(
                PlayAnimationAndWaitPayload.TYPE,
                PlayAnimationAndWaitPayload.CODEC
        );
        FabricatedNetworking.registerC2SPacket(
                AnimationFinishedPayload.TYPE,
                AnimationFinishedPayload.CODEC,
                AnimationFinishedPayload::handle
        );
        IntroHandshake.register();
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(TimeBellFreezePayload.TYPE, TimeBellFreezePayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(ReturnTotemActivationPayload.TYPE, ReturnTotemActivationPayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(PlayAnimationPayload.TYPE, PlayAnimationPayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(PlayAnimationAndWaitPayload.TYPE, PlayAnimationAndWaitPayload::handle);
        IntroHandshake.registerClient();
    }
}
