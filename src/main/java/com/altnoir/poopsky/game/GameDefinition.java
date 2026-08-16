package com.altnoir.poopsky.game;

import com.altnoir.poopsky.content.item.p.GameDiscItem;

import java.util.function.Supplier;

public record GameDefinition(
        String id,
        String gameName,
        Supplier<GameDiscItem> disc,
        Supplier<ServerGame> server
) {
    public GameDiscItem discItem() {
        return disc.get();
    }

    public ServerGame newServerGame() {
        ServerGame game = server.get();
        game.setGameDefinition(this);
        return game;
    }
}
