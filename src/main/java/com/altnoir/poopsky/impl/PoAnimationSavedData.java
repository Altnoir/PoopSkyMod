package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.network.PoAnimation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class PoAnimationSavedData extends SavedData {
    private static final String PLAYED_PLAYERS_TAG = "played_players";
    private static final String PLAYED_PLAYER_NAMES_TAG = "played_player_names";
    private static final Codec<PoAnimationSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().optionalFieldOf(PLAYED_PLAYERS_TAG, List.of()).forGetter(data -> uuidStrings(data.playedPlayers)),
            Codec.STRING.listOf().optionalFieldOf(PLAYED_PLAYER_NAMES_TAG, List.of()).forGetter(data -> List.copyOf(data.playedPlayerNames)),
            Codec.STRING.listOf().optionalFieldOf(tagName(PoAnimation.POEM, PLAYED_PLAYERS_TAG), List.of()).forGetter(data -> uuidStrings(data.poemPlayedPlayers)),
            Codec.STRING.listOf().optionalFieldOf(tagName(PoAnimation.POEM, PLAYED_PLAYER_NAMES_TAG), List.of()).forGetter(data -> List.copyOf(data.poemPlayedPlayerNames))
    ).apply(instance, PoAnimationSavedData::new));
    private static final SavedDataType<PoAnimationSavedData> TYPE = new SavedDataType<>(
            PoopSky.loc("intro"),
            PoAnimationSavedData::new,
            CODEC
    );

    private final Set<UUID> playedPlayers = new HashSet<>();
    private final Set<String> playedPlayerNames = new HashSet<>();
    private final Set<UUID> poemPlayedPlayers = new HashSet<>();
    private final Set<String> poemPlayedPlayerNames = new HashSet<>();

    private PoAnimationSavedData() {
    }

    private PoAnimationSavedData(List<String> playedPlayers, List<String> playedPlayerNames,
                                 List<String> poemPlayedPlayers, List<String> poemPlayedPlayerNames) {
        this.playedPlayers.addAll(parseUuids(playedPlayers));
        this.playedPlayerNames.addAll(playedPlayerNames);
        this.poemPlayedPlayers.addAll(parseUuids(poemPlayedPlayers));
        this.poemPlayedPlayerNames.addAll(poemPlayedPlayerNames);
    }

    public static PoAnimationSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean hasPlayed(PoAnimation animation, UUID playerId, String playerName) {
        return this.players(animation).contains(playerId) || this.playerNames(animation).contains(playerName);
    }

    private Set<UUID> players(PoAnimation animation) {
        return animation == PoAnimation.POEM ? this.poemPlayedPlayers : this.playedPlayers;
    }

    private Set<String> playerNames(PoAnimation animation) {
        return animation == PoAnimation.POEM ? this.poemPlayedPlayerNames : this.playedPlayerNames;
    }

    private static String tagName(PoAnimation animation, String suffix) {
        return animation.serializedName() + "_" + suffix;
    }

    private static Set<UUID> parseUuids(Iterable<String> entries) {
        Set<UUID> values = new HashSet<>();
        for (String entry : entries) {
            try {
                values.add(UUID.fromString(entry));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return values;
    }

    private static List<String> uuidStrings(Iterable<UUID> values) {
        List<String> result = new java.util.ArrayList<>();
        values.forEach(value -> result.add(value.toString()));
        return result;
    }
}