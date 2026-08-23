package com.altnoir.poopsky.game.client;

import com.altnoir.poopsky.content.item.p.GameDiskItem;
import com.altnoir.poopsky.game.GameDefinition;
import com.altnoir.poopsky.game.GameDefinitions;
import com.altnoir.poopsky.game.render.ClientBlocktrisGame;
import com.altnoir.poopsky.game.render.ClientPongGame;
import com.altnoir.poopsky.game.render.ClientRoundwormGame;
import com.altnoir.poopsky.game.render.ClientTouhouGame;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ClientGameTypes {
    private static final Map<GameDiskItem, Supplier<ClientGame>> GAMES = new IdentityHashMap<>();

    static {
        register(GameDefinitions.ROUNDWORM, ClientRoundwormGame::new);
        register(GameDefinitions.BLOCKTRIS, ClientBlocktrisGame::new);
        register(GameDefinitions.PONG, ClientPongGame::new);
        register(GameDefinitions.TOUHOU, ClientTouhouGame::new);
    }

    private ClientGameTypes() {
    }

    public static void register(GameDefinition definition, Supplier<ClientGame> factory) {
        GAMES.put(definition.discItem(), factory);
    }

    public static ClientGame newGameFor(GameDiskItem item) {
        Supplier<ClientGame> supplier = GAMES.get(item);
        if (supplier == null) {
            throw new IllegalArgumentException("No client game specified for " + item);
        }

        GameDefinition definition = GameDefinitions.byDiscItem(item);
        ClientGame game = supplier.get();
        game.setGameDefinition(definition);
        return game;
    }
}