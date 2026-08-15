package com.altnoir.poopsky.client.screen;

import com.altnoir.poopsky.content.item.p.GameDiscItem;
import com.altnoir.poopsky.game.controls.Button;
import com.altnoir.poopsky.impl.network.ArcadeInputPacket;
import com.altnoir.poopsky.impl.network.ArcadeResetPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GamingConsoleScreen extends Screen {
    private static final int W = 87;
    private static final int S = 83;
    private static final int A = 65;
    private static final int D = 68;
    private static final int E = 69;
    private static final int SPACE = 32;
    private static final int ENTER = 257;

    private final BlockPos arcadeMachinePos;

    public GamingConsoleScreen(Component title, BlockPos arcadeMachinePos, GameDiscItem cartridge) {
        super(title);
        this.arcadeMachinePos = arcadeMachinePos;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        boolean flag = false;
        if (key == E) {
            this.minecraft.setScreen(null);
            return true;
        }
        if (key == 82) {
            PacketDistributor.sendToServer(new ArcadeResetPacket(arcadeMachinePos));
            flag = true;
        }
        flag |= sendInputIfGameKey(key, true);

        return super.keyPressed(key, scanCode, modifiers) || flag;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return sendInputIfGameKey(keyCode, false) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    private boolean sendInputIfGameKey(int key, boolean pressed) {
        Button button = switch (key) {
            case W -> Button.UP;
            case S -> Button.DOWN;
            case A -> Button.LEFT;
            case D -> Button.RIGHT;
            case SPACE -> Button.BUTTON1;
            case ENTER -> Button.BUTTON2;
            default -> null;
        };
        if (button == null) {
            return false;
        }
        PacketDistributor.sendToServer(new ArcadeInputPacket(arcadeMachinePos, button, pressed));
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}