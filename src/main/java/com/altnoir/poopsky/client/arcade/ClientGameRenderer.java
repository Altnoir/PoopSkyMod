package com.altnoir.poopsky.client.arcade;

import com.altnoir.poopsky.client.games.gamediscs.BlocktrisGame;
import com.altnoir.poopsky.client.games.gamediscs.FlappyBirdGame;
import com.altnoir.poopsky.client.games.gamediscs.PongGame;
import com.altnoir.poopsky.client.games.gamediscs.SlimeGame;
import com.altnoir.poopsky.client.games.util.Game;
import net.minecraft.nbt.CompoundTag;

public final class ClientGameRenderer {
    private ClientGameRenderer() {
    }

    public static void apply(Game game, CompoundTag snapshot) {
        if (game == null || snapshot == null) {
            return;
        }

        game.applySnapshot(snapshot);
        if (game instanceof SlimeGame slime) {
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
