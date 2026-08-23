package com.altnoir.poopsky.game.client;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.game.Button;
import com.altnoir.poopsky.impl.network.ArcadeInputPacket;
import com.altnoir.poopsky.impl.network.ArcadeResetPacket;
import com.altnoir.poopsky.impl.network.ArcadeStopControlPacket;
import com.altnoir.poopsky.init.PoKeyBoardInput;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.EnumSet;

public final class ArcadeControlSession {
    private static final EnumSet<Button> HELD_BUTTONS = EnumSet.noneOf(Button.class);

    @Nullable
    private static BlockPos machinePos;

    private ArcadeControlSession() {
    }

    public static boolean isActive() {
        return machinePos != null;
    }

    public static float getFovMultiplier() {
        return isActive() ? 0.8F : 1.0F;
    }

    public static void enter(BlockPos pos) {
        clear();
        machinePos = pos;
    }

    public static void onKeyInput(InputEvent.Key event) {
        if (machinePos == null || event.getAction() == InputConstants.REPEAT) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        suppressVanillaMovement(mc.options);

        int key = event.getKey();
        if (event.getAction() == InputConstants.PRESS) {
            if (key == GLFW.GLFW_KEY_R) {
                send(new ArcadeResetPacket(machinePos));
                return;
            }
        }

        Button button = getButton(key, event.getScanCode());
        if (button == null) {
            return;
        }

        boolean pressed = event.getAction() == InputConstants.PRESS;
        if (pressed == HELD_BUTTONS.contains(button)) {
            return;
        }

        if (pressed) {
            HELD_BUTTONS.add(button);

            if (key == GLFW.GLFW_KEY_ENTER) {
                mc.options.keyChat.consumeClick();
                mc.options.keyCommand.consumeClick();
            }
        } else {
            HELD_BUTTONS.remove(button);
        }

        send(new ArcadeInputPacket(machinePos, button, pressed));
    }


    public static void tick(Minecraft mc) {
        if (machinePos == null) {
            return;
        }

        if (mc.player == null || mc.level == null) {
            clear();
            return;
        }

        if (!(mc.level.getBlockEntity(machinePos) instanceof ArcadeBlockEntity arcade)) {
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
        if (machinePos == null) {
            return;
        }
        BlockPos pos = machinePos;
        releaseButtons(pos);
        send(new ArcadeStopControlPacket(pos));
        clear();
    }

    public static boolean exitAt(BlockPos pos) {
        if (!pos.equals(machinePos)) {
            return false;
        }
        releaseButtons(pos);
        clear();
        return true;
    }

    public static void clear() {
        machinePos = null;
        HELD_BUTTONS.clear();
    }

    private static void releaseButtons(BlockPos pos) {
        for (Button button : HELD_BUTTONS) {
            send(new ArcadeInputPacket(pos, button, false));
        }
    }

    private static void send(CustomPacketPayload packet) {
        ClientPacketDistributor.sendToServer(packet);
    }

    private static void suppressVanillaMovement(Options options) {
        options.keyUp.setDown(false);
        options.keyDown.setDown(false);
        options.keyLeft.setDown(false);
        options.keyRight.setDown(false);
        options.keyJump.setDown(false);
        options.keySprint.setDown(false);
        options.keyChat.setDown(false);
        options.keyCommand.setDown(false);
    }

    @Nullable
    private static Button getButton(int key, int scanCode) {
        if (PoKeyBoardInput.ARCADE_UP.getKey().getValue() == key) {
            return Button.UP;
        }
        if (PoKeyBoardInput.ARCADE_DOWN.getKey().getValue() == key) {
            return Button.DOWN;
        }
        if (PoKeyBoardInput.ARCADE_LEFT.getKey().getValue() == key) {
            return Button.LEFT;
        }
        if (PoKeyBoardInput.ARCADE_RIGHT.getKey().getValue() == key) {
            return Button.RIGHT;
        }
        if (PoKeyBoardInput.ARCADE_BUTTON1.getKey().getValue() == key) {
            return Button.BUTTON1;
        }
        if (PoKeyBoardInput.ARCADE_BUTTON2.getKey().getValue() == key) {
            return Button.BUTTON2;
        }
        return null;
    }
}
