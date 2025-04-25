package com.altnoir.poopsky.keybinding;

import com.altnoir.poopsky.item.PSItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class KeyUtil {
    public static final int[] MOVEMENT_KEYS = {
            GLFW.GLFW_KEY_W,
            GLFW.GLFW_KEY_A,
            GLFW.GLFW_KEY_S,
            GLFW.GLFW_KEY_D
    };
    public static String getKey(int key) {
        return switch(key) {
            case GLFW.GLFW_KEY_W -> "↑";
            case GLFW.GLFW_KEY_A -> "←";
            case GLFW.GLFW_KEY_S -> "↓";
            case GLFW.GLFW_KEY_D -> "→";
            default -> "?";
        };
    }
    public static int[] parseSequence(String seq) {
        return seq.toUpperCase().chars()
                .map(c -> switch(c) {
                    case 'W' -> GLFW.GLFW_KEY_W;
                    case 'A' -> GLFW.GLFW_KEY_A;
                    case 'S' -> GLFW.GLFW_KEY_S;
                    case 'D' -> GLFW.GLFW_KEY_D;
                    default -> -1;
                })
                .toArray();
    }

    public static boolean isCtrlHeld() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        return InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }
    public static boolean isHoldingPoopBall(PlayerEntity player) {
        return player.getMainHandStack().getItem() == PSItems.POOP_BALL || player.getOffHandStack().getItem() == PSItems.POOP_BALL;
    }
}
