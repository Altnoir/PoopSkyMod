package com.altnoir.poopsky.game.util;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.game.client.ArcadeControlSession;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public final class GameUtils {
    private GameUtils() {
    }

    public static void enterArcadeControl(BlockPos machinePos) {
        ArcadeControlSession.enter(machinePos);
    }

    public static boolean exitArcadeControl(BlockPos machinePos) {
        return ArcadeControlSession.exitAt(machinePos);
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
}