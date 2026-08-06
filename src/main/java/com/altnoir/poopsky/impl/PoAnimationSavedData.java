package com.altnoir.poopsky.impl;

import com.altnoir.poopsky.impl.network.PoAnimation;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PoAnimationSavedData extends SavedData {
    private static final String DATA_NAME = "poopsky_intro";
    private static final String PLAYED_PLAYERS_TAG = "played_players";
    private static final String PLAYED_PLAYER_NAMES_TAG = "played_player_names";
    private static final Factory<PoAnimationSavedData> FACTORY = new Factory<>(PoAnimationSavedData::new, PoAnimationSavedData::load);

    private final Set<UUID> playedPlayers = new HashSet<>();
    private final Set<String> playedPlayerNames = new HashSet<>();
    private final Set<UUID> poemPlayedPlayers = new HashSet<>();
    private final Set<String> poemPlayedPlayerNames = new HashSet<>();

    public static PoAnimationSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static PoAnimationSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PoAnimationSavedData data = new PoAnimationSavedData();
        readUuids(tag, PLAYED_PLAYERS_TAG, data.playedPlayers);
        readNames(tag, PLAYED_PLAYER_NAMES_TAG, data.playedPlayerNames);
        readUuids(tag, tagName(PoAnimation.POEM, PLAYED_PLAYERS_TAG), data.poemPlayedPlayers);
        readNames(tag, tagName(PoAnimation.POEM, PLAYED_PLAYER_NAMES_TAG), data.poemPlayedPlayerNames);
        return data;
    }

    public boolean markPlayed(PoAnimation animation, UUID playerId, String playerName) {
        Set<UUID> players = this.players(animation);
        Set<String> playerNames = this.playerNames(animation);
        boolean played = players.contains(playerId) || playerNames.contains(playerName);
        boolean changed = players.add(playerId);
        changed |= playerNames.add(playerName);
        if (changed) {
            this.setDirty();
        }
        return !played;
    }

    public boolean hasPlayed(PoAnimation animation, UUID playerId, String playerName) {
        return this.players(animation).contains(playerId) || this.playerNames(animation).contains(playerName);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(PLAYED_PLAYERS_TAG, toList(this.playedPlayers));
        tag.put(PLAYED_PLAYER_NAMES_TAG, toList(this.playedPlayerNames));
        tag.put(tagName(PoAnimation.POEM, PLAYED_PLAYERS_TAG), toList(this.poemPlayedPlayers));
        tag.put(tagName(PoAnimation.POEM, PLAYED_PLAYER_NAMES_TAG), toList(this.poemPlayedPlayerNames));
        return tag;
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

    private static void readUuids(CompoundTag tag, String tagName, Set<UUID> values) {
        ListTag entries = tag.getList(tagName, Tag.TAG_STRING);
        for (int index = 0; index < entries.size(); index++) {
            try {
                values.add(UUID.fromString(entries.getString(index)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static void readNames(CompoundTag tag, String tagName, Set<String> values) {
        ListTag entries = tag.getList(tagName, Tag.TAG_STRING);
        for (int index = 0; index < entries.size(); index++) {
            values.add(entries.getString(index));
        }
    }

    private static ListTag toList(Iterable<?> values) {
        ListTag list = new ListTag();
        values.forEach(value -> list.add(StringTag.valueOf(value.toString())));
        return list;
    }
}
