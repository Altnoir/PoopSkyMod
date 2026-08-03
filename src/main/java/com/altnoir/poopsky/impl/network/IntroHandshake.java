package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.impl.IntroSavedData;
import com.altnoir.poopsky.worldgen.PoVoidChunkGenerator;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricServerConfigurationNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import java.util.function.Consumer;

public final class IntroHandshake {
    private static final ConfigurationTask.Type TASK_TYPE =
            new ConfigurationTask.Type("poopsky_intro");

    private IntroHandshake() {
    }

    public static void register() {
        PayloadTypeRegistry.configurationS2C().register(StartPayload.TYPE, StartPayload.CODEC);
        PayloadTypeRegistry.configurationC2S().register(FinishedPayload.TYPE, FinishedPayload.CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(FinishedPayload.TYPE, (payload, context) ->
                context.server().execute(() ->
                        ((FabricServerConfigurationNetworkHandler) context.networkHandler()).completeTask(TASK_TYPE)));
        ServerConfigurationConnectionEvents.CONFIGURE.register(IntroHandshake::configure);
    }

    public static void registerClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(StartPayload.TYPE, (payload, context) ->
                context.client().execute(() -> IntroController.start(() ->
                        context.responseSender().sendPacket(FinishedPayload.INSTANCE))));
    }

    private static void configure(ServerConfigurationPacketListenerImpl listener,
                                  net.minecraft.server.MinecraftServer server) {
        if (!(server.overworld().getChunkSource().getGenerator() instanceof PoVoidChunkGenerator)) {
            return;
        }

        GameProfile profile = listener.getOwner();
        if (profile.getId() == null
                || IntroSavedData.get(server.overworld()).hasPlayed(profile.getId(), profile.getName())) {
            return;
        }
        ((FabricServerConfigurationNetworkHandler) listener).addTask(new Task());
    }

    private record StartPayload() implements CustomPacketPayload {
        private static final StartPayload INSTANCE = new StartPayload();
        private static final Type<StartPayload> TYPE = new Type<>(PoopSky.loc("poopsky_intro"));
        private static final StreamCodec<FriendlyByteBuf, StartPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private record FinishedPayload() implements CustomPacketPayload {
        private static final FinishedPayload INSTANCE = new FinishedPayload();
        private static final Type<FinishedPayload> TYPE = new Type<>(PoopSky.loc("poopsky_intro_finished"));
        private static final StreamCodec<FriendlyByteBuf, FinishedPayload> CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private record Task() implements ConfigurationTask {
        @Override
        public void start(Consumer<Packet<?>> sender) {
            sender.accept(ServerConfigurationNetworking.createS2CPacket(StartPayload.INSTANCE));
        }

        @Override
        public Type type() {
            return TASK_TYPE;
        }
    }
}
