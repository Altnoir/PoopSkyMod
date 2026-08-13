package com.altnoir.poopsky.game.client.arcade;

import com.altnoir.poopsky.game.client.gamediscs.BlocktrisGame;
import com.altnoir.poopsky.game.client.gamediscs.FlappyBirdGame;
import com.altnoir.poopsky.game.client.gamediscs.PongGame;
import com.altnoir.poopsky.game.client.gamediscs.RoundwormGame;
import com.altnoir.poopsky.game.client.util.Game;
import net.minecraft.nbt.CompoundTag;

public final class ClientGameRenderer {
    private ClientGameRenderer() {
    }

    public static void apply(Game game, CompoundTag snapshot) {
        if (game == null || snapshot == null) {
            return;
        }

        game.applySnapshot(snapshot);
        if (game instanceof RoundwormGame slime) {
            slime.applySnapshot(snapshot);
        } else if (game instanceof PongGame pong) {
            pong.applySnapshot(snapshot);
        } else if (game instanceof FlappyBirdGame flappy) {
            flappy.applySnapshot(snapshot);
        } else if (game instanceof BlocktrisGame blocktris) {
            blocktris.applySnapshot(snapshot);
        }
    }
}
