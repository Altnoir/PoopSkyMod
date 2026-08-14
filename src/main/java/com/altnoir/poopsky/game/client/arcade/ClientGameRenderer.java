package com.altnoir.poopsky.game.client.arcade;

import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.gamediscs.ClientBlocktrisGame;
import com.altnoir.poopsky.game.client.gamediscs.ClientPongGame;
import com.altnoir.poopsky.game.client.gamediscs.ClientRoundwormGame;
import net.minecraft.nbt.CompoundTag;

public final class ClientGameRenderer {
    private ClientGameRenderer() {
    }

    public static void apply(ClientGame clientGame, CompoundTag snapshot) {
        if (clientGame == null || snapshot == null) {
            return;
        }

        clientGame.applySnapshot(snapshot);
        switch (clientGame) {
            case ClientRoundwormGame slime -> slime.applySnapshot(snapshot);
            case ClientPongGame pong -> pong.applySnapshot(snapshot);
            case ClientBlocktrisGame blocktris -> blocktris.applySnapshot(snapshot);
            default -> {
            }
        }
    }
}