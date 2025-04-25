package com.altnoir.poopsky.keybinding;

import com.altnoir.poopsky.PoopSky;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ComboHandler {
    private static final List<Integer> inputBuffer = new ArrayList<>();
    private static final boolean[] keyStates = new boolean[KeyUtil.MOVEMENT_KEYS.length];
    public static final int[] COMBO_1 = KeyUtil.parseSequence("WDSSS");
    public static List<Integer> getInputBuffer() {
        return new ArrayList<>(inputBuffer);
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            // 激活条件
            if (KeyUtil.isCtrlHeld() && KeyUtil.isHoldingPoopBall(client.player)) {
                handleInput(client);
            } else {
                inputBuffer.clear();
            }
        });
    }

    private static void handleInput(MinecraftClient client) {
        for (int i = 0; i < KeyUtil.MOVEMENT_KEYS.length; i++) {
            int key = KeyUtil.MOVEMENT_KEYS[i];
            boolean isPressed = InputUtil.isKeyPressed(client.getWindow().getHandle(), key);

            // 检测按键状态变化
            if (isPressed) {
                if (!keyStates[i]) { // 首次按下
                    addKeyToBuffer(key);
                    keyStates[i] = true; // 标记为已处理
                }
            } else {
                keyStates[i] = false; // 松开时重置状态
            }
        }
        checkSequence();
    }

    private static void addKeyToBuffer(int key) {
        if (inputBuffer.size() >= COMBO_1.length) return;

        int expectedKey = COMBO_1[inputBuffer.size()];
        if (key == expectedKey) {
            playKeySuccessSound(inputBuffer.size()); // 正确按键音效
        } else {
            MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.8f, 0.6f);
        }
        inputBuffer.add(key);
    }

    private static void checkSequence() {
        // 检查顺序和按键是否匹配
        for (int i = 0; i < inputBuffer.size(); i++) {
            if (inputBuffer.get(i) != COMBO_1[i]) {
                inputBuffer.clear();
                return;
            }
        }
        // 成功触发
        if (inputBuffer.size() == COMBO_1.length) {
            inputBuffer.clear();
            onComboSuccess();
        }
    }

    private static void onComboSuccess() {
        // 输出
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Poop Combo!"), true);
        MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 0.8f, 0.8f);
    }

    private static void playKeySuccessSound(int i) {
        float pitch = 0.85F + 0.05F * (i - COMBO_1.length);
        MinecraftClient.getInstance().player.playSound(
                SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
                0.8f,
                pitch
        );
    }
}


