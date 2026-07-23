package com.altnoir.poopsky.impl;

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

public final class IntroSavedData extends SavedData {
    private static final String DATA_NAME = "poopsky_intro";
    private static final String PLAYED_PLAYERS_TAG = "played_players";
    private static final String PLAYED_PLAYER_NAMES_TAG = "played_player_names";
    private static final Factory<IntroSavedData> FACTORY =
            new Factory<>(IntroSavedData::new, IntroSavedData::load);

    private final Set<UUID> playedPlayers = new HashSet<>();
    private final Set<String> playedPlayerNames = new HashSet<>();

    public static IntroSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static IntroSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        IntroSavedData data = new IntroSavedData();
        ListTag players = tag.getList(PLAYED_PLAYERS_TAG, Tag.TAG_STRING);
        for (int index = 0; index < players.size(); index++) {
            try {
                data.playedPlayers.add(UUID.fromString(players.getString(index)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        ListTag names = tag.getList(PLAYED_PLAYER_NAMES_TAG, Tag.TAG_STRING);
        for (int index = 0; index < names.size(); index++) {
            data.playedPlayerNames.add(names.getString(index));
        }
        return data;
    }

    public boolean markPlayed(UUID playerId, String playerName) {
        if (this.playedPlayers.contains(playerId) || this.playedPlayerNames.contains(playerName)) return false;

        this.playedPlayers.add(playerId);
        this.playedPlayerNames.add(playerName);
        this.setDirty();
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(PLAYED_PLAYERS_TAG, toList(this.playedPlayers.stream().map(UUID::toString).sorted().toList()));
        tag.put(PLAYED_PLAYER_NAMES_TAG, toList(this.playedPlayerNames.stream().sorted().toList()));
        return tag;
    }

    private static ListTag toList(Iterable<String> values) {
        ListTag list = new ListTag();
        values.forEach(value -> list.add(StringTag.valueOf(value)));
        return list;
    }
}
