package com.altnoir.poopsky.game;

import com.altnoir.poopsky.content.item.p.GameDiskItem;
import com.altnoir.poopsky.game.gamediscs.ServerBlocktrisGame;
import com.altnoir.poopsky.game.gamediscs.ServerPongGame;
import com.altnoir.poopsky.game.gamediscs.ServerRoundwormGame;
import com.altnoir.poopsky.game.gamediscs.ServerTouhouGame;
import com.altnoir.poopsky.init.PoItems;

import java.util.List;

public final class GameDefinitions {
    public static final GameDefinition ROUNDWORM = new GameDefinition(
            "roundworm",
            "RoundwormGame",
            PoItems.GAME_DISK_ROUNDWORM,
            ServerRoundwormGame::new,
            GameSyncMode.SNAPSHOT
    );
    public static final GameDefinition BLOCKTRIS = new GameDefinition(
            "blocktris",
            "BlocktrisGame",
            PoItems.GAME_DISK_BLOCKTRIS,
            ServerBlocktrisGame::new,
            GameSyncMode.SNAPSHOT
    );
    public static final GameDefinition PONG = new GameDefinition(
            "pong",
            "PongGame",
            PoItems.GAME_DISK_PONG,
            ServerPongGame::new,
            GameSyncMode.SNAPSHOT
    );
    public static final GameDefinition TOUHOU = new GameDefinition(
            "touhou",
            "TouhouGame",
            PoItems.GAME_DISK_TOUHOU,
            ServerTouhouGame::new,
            GameSyncMode.SNAPSHOT
    );

    public static final List<GameDefinition> ALL = List.of(
            ROUNDWORM,
            BLOCKTRIS,
            PONG,
            TOUHOU
    );

    private GameDefinitions() {
    }

    public static GameDefinition byDiscItem(GameDiskItem disc) {
        for (GameDefinition definition : ALL) {
            if (definition.discItem() == disc) {
                return definition;
            }
        }
        throw new IllegalArgumentException("Unknown arcade game " + disc);
    }
}
