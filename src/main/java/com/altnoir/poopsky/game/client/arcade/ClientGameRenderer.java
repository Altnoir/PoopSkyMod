package com.altnoir.poopsky.game.client.arcade;

import com.altnoir.poopsky.game.client.gamediscs.*;
import com.altnoir.poopsky.game.client.ClientGame;
import net.minecraft.nbt.CompoundTag;

public final class ClientGameRenderer {
    private ClientGameRenderer() {
    }

    public static void apply(ClientGame clientGame, CompoundTag snapshot) {
        if (clientGame == null || snapshot == null) {
            return;
        }

        clientGame.applySnapshot(snapshot);
        if (clientGame instanceof ClientRoundwormGame slime) {
            slime.applySnapshot(snapshot);
        } else if (clientGame instanceof ClientPongGame pong) {
            pong.applySnapshot(snapshot);
        } else if (clientGame instanceof ClientFlappyBirdGame flappy) {
            flappy.applySnapshot(snapshot);
        } else if (clientGame instanceof ClientBlocktrisGame blocktris) {
            blocktris.applySnapshot(snapshot);
        }
    }
}
