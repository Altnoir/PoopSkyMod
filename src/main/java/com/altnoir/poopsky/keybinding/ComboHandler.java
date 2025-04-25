package com.altnoir.poopsky.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ComboHandler {
    private static final List<Integer> inputBuffer = new ArrayList<>();
    private static final boolean[] keyStates = new boolean[KeyUtil.MOVEMENT_KEYS.length];
    public static final int[] TARGET_SEQUENCE = KeyUtil.parseSequence("WDSSS");

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // 检查激活条件
            boolean shouldListen = KeyUtil.isCtrlHeld() &&
                    KeyUtil.isHoldingPoopBall(client.player);

            if (shouldListen) {
                handleInput(client);
            } else {
                resetCombo();
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
        if (inputBuffer.size() >= TARGET_SEQUENCE.length) return;

        int expectedKey = TARGET_SEQUENCE[inputBuffer.size()];
        if (key == expectedKey) {
            playKeySuccessSound(); // 正确按键音效
        } else {
            playKeyWrongSound();   // 错误按键音效
        }
        inputBuffer.add(key);
    }

    private static void checkSequence() {
        // 检查顺序和按键是否匹配
        for (int i = 0; i < inputBuffer.size(); i++) {
            if (inputBuffer.get(i) != TARGET_SEQUENCE[i]) {
                resetCombo();
                return;
            }
        }
        // 成功触发
        if (inputBuffer.size() == TARGET_SEQUENCE.length) {
            onComboSuccess();
            resetCombo();
        }
    }

    private static void onComboSuccess() {
        // 输出
        MinecraftClient.getInstance().player.sendMessage(
                Text.literal("Poop Combo!"),
                true
        );
        playComboCompleteSound();
    }

    public static void resetCombo() {
        // 重置
        inputBuffer.clear();
    }

    public static List<Integer> getInputBuffer() {
        return new ArrayList<>(inputBuffer);
    }

    private static void playKeySuccessSound() {
        MinecraftClient.getInstance().player.playSound(
                SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
                0.8f,
                1.2f
        );
    }

    private static void playKeyWrongSound() {
        MinecraftClient.getInstance().player.playSound(
                SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(),
                0.8f,
                0.6f
        );
    }

    private static void playComboCompleteSound() {
        MinecraftClient.getInstance().player.playSound(
                SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,
                1.0f,
                Random.create().nextBetween(8, 12)/10f
        );
    }
}


