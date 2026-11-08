package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class PoNetworking {
    private static final String VERSION = "forge-1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            PoopSky.loc("main"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static boolean registered;
    private static int nextId;

    public static void registerNetworking() {
        if (registered) return;
        registered = true;
        server(PlugActionPayload.class, PlugActionPayload.CODEC, PlugActionPayload::handle);
        server(PlugDismountPayload.class, PlugDismountPayload.CODEC, PlugDismountPayload::handle);
        server(PlugInputPayload.class, PlugInputPayload.CODEC, PlugInputPayload::handle);
        server(FlushToiletCartInputPayload.class, FlushToiletCartInputPayload.CODEC, FlushToiletCartInputPayload::handle);
        client(TimeBellFreezePayload.class, TimeBellFreezePayload.CODEC, TimeBellFreezePayload::handle);
        client(ReturnTotemActivationPayload.class, ReturnTotemActivationPayload.CODEC, ReturnTotemActivationPayload::handle);
        client(UnpoopingTotemActivationPayload.class, UnpoopingTotemActivationPayload.CODEC, UnpoopingTotemActivationPayload::handle);
        client(PlayAnimationPayload.class, PlayAnimationPayload.CODEC, PlayAnimationPayload::handle);
        client(PlayAnimationAndWaitPayload.class, PlayAnimationAndWaitPayload.CODEC, PlayAnimationAndWaitPayload::handle);
        server(AnimationFinishedPayload.class, AnimationFinishedPayload.CODEC, AnimationFinishedPayload::handle);
        server(ArcadeInputPacket.class, ArcadeInputPacket.STREAM_CODEC, ArcadeInputPacket::handle);
        server(ArcadeResetPacket.class, ArcadeResetPacket.STREAM_CODEC, ArcadeResetPacket::handle);
        server(ArcadeStopControlPacket.class, ArcadeStopControlPacket.STREAM_CODEC, ArcadeStopControlPacket::handle);
        client(ArcadeGameSnapshotPacket.class, ArcadeGameSnapshotPacket.STREAM_CODEC, ArcadeGameSnapshotPacket::handle);
    }

    private static <T> void server(Class<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, BiConsumer<T, PoPayloadContext> handler) {
        register(type, codec, handler, NetworkDirection.PLAY_TO_SERVER);
    }
    private static <T> void client(Class<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, BiConsumer<T, PoPayloadContext> handler) {
        register(type, codec, handler, NetworkDirection.PLAY_TO_CLIENT);
    }
    private static <T> void register(Class<T> type, StreamCodec<? super FriendlyByteBuf, T> codec,
            BiConsumer<T, PoPayloadContext> handler, NetworkDirection direction) {
        CHANNEL.registerMessage(nextId++, type, (message, buffer) -> codec.encode(buffer, message), codec::decode,
                (message, supplier) -> {
                    var context = supplier.get();
                    if (direction != NetworkDirection.PLAY_TO_SERVER || context.getSender() != null) {
                        handler.accept(message, new PoPayloadContext(context));
                    }
                    context.setPacketHandled(true);
                }, Optional.of(direction));
    }

    public static void sendToServer(PoPayload message) { CHANNEL.sendToServer(message); }
    public static void sendToPlayer(ServerPlayer player, PoPayload message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos pos, PoPayload message) {
        for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(pos, false)) {
            sendToPlayer(player, message);
        }
    }
}
