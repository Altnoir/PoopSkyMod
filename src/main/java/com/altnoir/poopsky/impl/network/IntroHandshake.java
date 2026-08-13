package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.impl.PoAnimationSavedData;
import com.altnoir.poopsky.worldgen.PoVoidChunkGenerator;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.function.Consumer;

public final class IntroHandshake {
    private static final ConfigurationTask.Type TASK_TYPE = new ConfigurationTask.Type(PoopSky.loc("poopsky_intro"));

    private IntroHandshake() {
    }

    public static void registerPayloads(PayloadRegistrar registrar) {
        registrar.configurationToClient(StartPayload.TYPE, StartPayload.CODEC, StartPayload::handle);
        registrar.configurationToServer(FinishedPayload.TYPE, FinishedPayload.CODEC, FinishedPayload::handle);
    }

    public static void registerTask(RegisterConfigurationTasksEvent event) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !(server.overworld().getChunkSource().getGenerator() instanceof PoVoidChunkGenerator)) {
            return;
        }
        if (!(event.getListener() instanceof ServerConfigurationPacketListenerImpl listener)) return;

        GameProfile profile = listener.getOwner();
        if (profile.id() == null || PoAnimationSavedData.get(server.overworld()).hasPlayed(PoAnimation.INTRO, profile.id(), profile.name())) {
            return;
        }

        event.register(new Task());
    }

    private record StartPayload() implements CustomPacketPayload {
        private static final StartPayload INSTANCE = new StartPayload();
        private static final Type<StartPayload> TYPE = new Type<>(PoopSky.loc("poopsky_intro"));
        private static final StreamCodec<FriendlyByteBuf, StartPayload> CODEC = StreamCodec.unit(INSTANCE);

        private static void handle(StartPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> IntroController.start(() -> context.reply(FinishedPayload.INSTANCE)));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private record FinishedPayload() implements CustomPacketPayload {
        private static final FinishedPayload INSTANCE = new FinishedPayload();
        private static final Type<FinishedPayload> TYPE = new Type<>(PoopSky.loc("poopsky_intro_finished"));
        private static final StreamCodec<FriendlyByteBuf, FinishedPayload> CODEC = StreamCodec.unit(INSTANCE);

        private static void handle(FinishedPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> context.finishCurrentTask(TASK_TYPE));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private record Task() implements ICustomConfigurationTask {
        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            sender.accept(StartPayload.INSTANCE);
        }

        @Override
        public ConfigurationTask.Type type() {
            return TASK_TYPE;
        }
    }
}
