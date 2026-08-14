package com.altnoir.poopsky.client;

import com.altnoir.poopsky.client.screen.GamingConsoleScreen;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.game.client.ClientGame;
import com.altnoir.poopsky.game.client.gamediscs.ClientBlocktrisGame;
import com.altnoir.poopsky.game.client.gamediscs.ClientPongGame;
import com.altnoir.poopsky.game.client.gamediscs.ClientRoundwormGame;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ClientUtils {
    private static final Map<GameDiscItem, Supplier<ClientGame>> GAMES = new IdentityHashMap<>();

    private ClientUtils() {
    }

    public static void openArcadeScreen(BlockPos machinePos, GameDiscItem cartridge) {
        Minecraft.getInstance().setScreen(new GamingConsoleScreen(
                Component.translatable("gui.gamingconsole.title"),
                machinePos,
                cartridge
        ));
    }

    public static int getArcadeBestScore(BlockPos machinePos, String game) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (minecraft.level == null || player == null) {
            return 0;
        }

        if (minecraft.level.getBlockEntity(machinePos) instanceof ArcadeBlockEntity arcade) {
            return arcade.getBestScore(player.getUUID(), game);
        }

        return 0;
    }

    private static void registerGames() {
        GAMES.put(PoItems.GAME_DISC_SLIME.get(), ClientRoundwormGame::new);
        GAMES.put(PoItems.GAME_DISC_BLOCKTRIS.get(), ClientBlocktrisGame::new);
        GAMES.put(PoItems.GAME_DISC_PONG.get(), ClientPongGame::new);
    }

    public static ClientGame newGameFor(GameDiscItem item) {
        if (GAMES.isEmpty()) {
            registerGames();
        }

        Supplier<ClientGame> supplier = GAMES.get(item);
        if (supplier == null) {
            throw new IllegalArgumentException("No game specified for " + item);
        }
        return supplier.get();
    }
}
