package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import com.altnoir.poopsky.worldgen.PoVoidChunkGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class IntroHandshake {
    private static final ConfigurationTask.Type TASK_TYPE = new ConfigurationTask.Type(PoopSky.loc("poopsky_intro"));
    private static final CustomPacketPayload.Type<Payload> PAYLOAD_TYPE = new CustomPacketPayload.Type<>(PoopSky.loc("poopsky_intro"));
    private static final StreamCodec<FriendlyByteBuf, Payload> PAYLOAD_CODEC = StreamCodec.unit(new Payload());
    private static final CustomPacketPayload.Type<FinishedPayload> FINISHED_PAYLOAD_TYPE =
            new CustomPacketPayload.Type<>(PoopSky.loc("poopsky_intro_finished"));
    private static final StreamCodec<FriendlyByteBuf, FinishedPayload> FINISHED_PAYLOAD_CODEC =
            StreamCodec.unit(new FinishedPayload());

    private IntroHandshake() {
    }

    public static void registerPayload(PayloadRegistrar registrar) {
        registrar.configurationToClient(PAYLOAD_TYPE, PAYLOAD_CODEC, Payload::handle);
        registrar.configurationToServer(FINISHED_PAYLOAD_TYPE, FINISHED_PAYLOAD_CODEC, FinishedPayload::handle);
    }

    public static void registerTask(RegisterConfigurationTasksEvent event) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        if (!(server.overworld().getChunkSource().getGenerator() instanceof PoVoidChunkGenerator)) return;
        if (!(event.getListener() instanceof ServerConfigurationPacketListenerImpl listener)) return;

        UUID playerId = listener.getOwner().getId();
        if (playerId == null || !Data.get(server.overworld()).markPlayed(playerId)) return;

        event.register(new Task());
    }

    private record Payload() implements CustomPacketPayload {
        private static void handle(Payload payload, IPayloadContext context) {
            context.enqueueWork(() -> IntroController.start(
                    () -> context.reply(new FinishedPayload())
            ));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return PAYLOAD_TYPE;
        }
    }

    private record FinishedPayload() implements CustomPacketPayload {
        private static void handle(FinishedPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> context.finishCurrentTask(TASK_TYPE));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return FINISHED_PAYLOAD_TYPE;
        }
    }

    private record Task() implements ICustomConfigurationTask {
        @Override
        public void run(Consumer<CustomPacketPayload> sender) {
            sender.accept(new Payload());
        }

        @Override
        public ConfigurationTask.Type type() {
            return TASK_TYPE;
        }
    }

    private static final class Data extends SavedData {
        private static final String DATA_NAME = "poopsky_intro";
        private static final String PLAYED_PLAYERS_TAG = "played_players";
        private static final Factory<Data> FACTORY = new Factory<>(Data::new, Data::load);

        private final Set<UUID> playedPlayers = new HashSet<>();

        private static Data get(net.minecraft.server.level.ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
        }

        private static Data load(CompoundTag tag, HolderLookup.Provider registries) {
            Data data = new Data();
            ListTag players = tag.getList(PLAYED_PLAYERS_TAG, Tag.TAG_STRING);
            for (int index = 0; index < players.size(); index++) {
                try {
                    data.playedPlayers.add(UUID.fromString(players.getString(index)));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return data;
        }

        private boolean markPlayed(UUID playerId) {
            if (!this.playedPlayers.add(playerId)) return false;

            this.setDirty();
            return true;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            ListTag players = new ListTag();
            this.playedPlayers.stream()
                    .map(UUID::toString)
                    .sorted()
                    .map(StringTag::valueOf)
                    .forEach(players::add);
            tag.put(PLAYED_PLAYERS_TAG, players);
            return tag;
        }
    }
}
