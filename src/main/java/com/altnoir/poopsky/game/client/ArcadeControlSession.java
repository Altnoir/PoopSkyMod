package com.altnoir.poopsky.game.client;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.impl.event.PoKeyBoardInput;
import com.altnoir.poopsky.impl.network.ArcadeInputPacket;
import com.altnoir.poopsky.impl.network.ArcadeResetPacket;
import com.altnoir.poopsky.impl.network.ArcadeStopControlPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.Map;

public final class ArcadeControlSession {
    private static final ArcadeControlSession INSTANCE = new ArcadeControlSession();
    private static final Map<Button, Boolean> HELD_BUTTONS = new EnumMap<>(Button.class);

    @Nullable
    private BlockPos machinePos;

    private ArcadeControlSession() {
    }

    public static boolean isActive() {
        return INSTANCE.machinePos != null;
    }

    public static void enter(BlockPos pos) {
        clear();
        INSTANCE.machinePos = pos;
        HELD_BUTTONS.clear();
    }

    public static void onKeyInput(InputEvent.Key event) {
        if (INSTANCE.machinePos == null) {
            return;
        }
        if (event.getAction() == InputConstants.REPEAT) {
            return;
        }

        int key = event.getKey();
        Minecraft mc = Minecraft.getInstance();
        suppressVanillaMovement(mc.options);
        if (key == GLFW.GLFW_KEY_Q && event.getAction() == InputConstants.PRESS) {
            exit();
            return;
        }
        if (key == GLFW.GLFW_KEY_R && event.getAction() == InputConstants.PRESS) {
            PacketDistributor.sendToServer(new ArcadeResetPacket(INSTANCE.machinePos));
            return;
        }

        Button button = buttonFor(key, event.getScanCode());
        if (button == null) {
            return;
        }

        boolean pressed = event.getAction() == InputConstants.PRESS;
        if (pressed == HELD_BUTTONS.getOrDefault(button, false)) {
            return;
        }
        if (pressed && key == GLFW.GLFW_KEY_ENTER) {
            mc.options.keyChat.consumeClick();
            mc.options.keyCommand.consumeClick();
        }
        HELD_BUTTONS.put(button, pressed);
        PacketDistributor.sendToServer(new ArcadeInputPacket(INSTANCE.machinePos, button, pressed));
    }

    public static void tick(Minecraft mc) {
        if (INSTANCE.machinePos == null) {
            return;
        }
        if (mc.player == null || mc.level == null) {
            clear();
            return;
        }

        if (!(mc.level.getBlockEntity(INSTANCE.machinePos) instanceof ArcadeBlockEntity arcade)) {
            clear();
            return;
        }
        if (!arcade.hasCartridge()) {
            exit();
            return;
        }
        suppressVanillaMovement(mc.options);
    }

    public static void exit() {
        if (INSTANCE.machinePos == null) {
            return;
        }
        BlockPos pos = INSTANCE.machinePos;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            releaseHeldButtons(pos);
            PacketDistributor.sendToServer(new ArcadeStopControlPacket(pos));
        }
        clear();
    }

    public static void clear() {
        INSTANCE.machinePos = null;
        HELD_BUTTONS.clear();
    }

    private static void releaseHeldButtons(BlockPos pos) {
        for (Map.Entry<Button, Boolean> entry : HELD_BUTTONS.entrySet()) {
            if (entry.getValue()) {
                PacketDistributor.sendToServer(new ArcadeInputPacket(pos, entry.getKey(), false));
            }
        }
    }

    private static void suppressVanillaMovement(Options options) {
        options.keyUp.setDown(false);
        options.keyDown.setDown(false);
        options.keyLeft.setDown(false);
        options.keyRight.setDown(false);
        options.keyJump.setDown(false);
        options.keyJump.consumeClick();
        options.keySprint.setDown(false);
        options.keySprint.consumeClick();
        options.keyChat.setDown(false);
        options.keyChat.consumeClick();
        options.keyCommand.setDown(false);
        options.keyCommand.consumeClick();
    }

    private static Button buttonFor(int key, int scanCode) {
        if (PoKeyBoardInput.ARCADE_UP.matches(key, scanCode)) {
            return Button.UP;
        }
        if (PoKeyBoardInput.ARCADE_DOWN.matches(key, scanCode)) {
            return Button.DOWN;
        }
        if (PoKeyBoardInput.ARCADE_LEFT.matches(key, scanCode)) {
            return Button.LEFT;
        }
        if (PoKeyBoardInput.ARCADE_RIGHT.matches(key, scanCode)) {
            return Button.RIGHT;
        }
        if (PoKeyBoardInput.ARCADE_BUTTON1.matches(key, scanCode)) {
            return Button.BUTTON1;
        }
        if (PoKeyBoardInput.ARCADE_BUTTON2.matches(key, scanCode)) {
            return Button.BUTTON2;
        }
        return null;
    }
}