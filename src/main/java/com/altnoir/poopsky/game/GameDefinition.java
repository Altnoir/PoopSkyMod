package com.altnoir.poopsky.game;

import com.altnoir.poopsky.content.item.p.GameDiskItem;

import java.util.function.Supplier;

public record GameDefinition(
        String id,
        String gameName,
        Supplier<GameDiskItem> disc,
        Supplier<ServerGame> server,
        GameSyncMode syncMode
) {
    public GameDiskItem discItem() {
        return disc.get();
    }

    public ServerGame newServerGame() {
        ServerGame game = server.get();
        game.setGameDefinition(this);
        return game;
    }
}
