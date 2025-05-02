package com.altnoir.poopsky.combo;

import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.keybinding.PoopBallPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class PoopComboHandler {
    public static final List<Integer> inputBuffer = new ArrayList<>();
    private static final boolean[] keyStates = new boolean[KeyUtil.MOVEMENT_KEYS.length];

    public static final List<int[]> COMBO_LIST = new ArrayList<>();
    public static long[] lastTimes = new long[0];
    public static int[] COOL_DOWNS = new int[0];
    public static void addCombo(String sequence, int coolDown) {
        COMBO_LIST.add(KeyUtil.parseSequence(sequence));

        COOL_DOWNS = Arrays.copyOf(COOL_DOWNS, COMBO_LIST.size());
        COOL_DOWNS[COMBO_LIST.size()-1] = coolDown;

        lastTimes = Arrays.copyOf(lastTimes, COMBO_LIST.size());
    }
    static {
        addCombo("WSDAW", 2400);
        addCombo("WDSSS", 1200);
    }
    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            ItemStack stack = client.player.getMainHandStack();
            // 激活条件
            if (KeyUtil.isCtrlHeld() && KeyUtil.isHoldingPoopBall(client.player) && !stack.contains(PSComponents.POOP_BALL_COMPONENT)) {
                handleInput(client);
            } else {
                inputBuffer.clear();
            }
        });
    }
    public static boolean isCoolingDown(int comboIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || comboIndex < 0 || comboIndex >= COMBO_LIST.size()) return false;
        return client.world.getTime() - lastTimes[comboIndex] < COOL_DOWNS[comboIndex];


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
        check();
    }

    private static void addKeyToBuffer(int key) {
        int currentMaxLength = IntStream.range(0, COMBO_LIST.size())
                .filter(comboIndex -> !isCoolingDown(comboIndex))
                .map(comboIndex -> COMBO_LIST.get(comboIndex).length)
                .filter(len -> len > inputBuffer.size())
                .max()
                .orElse(0);

        if (currentMaxLength == 0) return;

        boolean anyMatch = IntStream.range(0, COMBO_LIST.size())
                .filter(comboIndex -> !isCoolingDown(comboIndex)) // 过滤冷却中的组合
                .anyMatch(comboIndex -> {
                    int[] combo = COMBO_LIST.get(comboIndex);
                    return inputBuffer.size() < combo.length &&
                            key == combo[inputBuffer.size()];
                });

        if (anyMatch) {
            playKeySound(inputBuffer.size());
        } else {
            boolean allCooling = IntStream.range(0, COMBO_LIST.size()).allMatch(PoopComboHandler::isCoolingDown);
            if (!allCooling) {
                MinecraftClient.getInstance().player.playSound(
                        SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(),
                        0.5f,
                        0.6f
                );
            }
        }
        inputBuffer.add(key);
    }

    private static void check() {
        for (int comboIndex = 0; comboIndex < COMBO_LIST.size(); comboIndex++) {
            if (isCoolingDown(comboIndex)) continue;

            int[] combo = COMBO_LIST.get(comboIndex);
            boolean match = true;
            for (int i = 0; i < Math.min(inputBuffer.size(), combo.length); i++) {
                if (inputBuffer.get(i) != combo[i]) {
                    match = false;
                    break;
                }
            }
            if (match && inputBuffer.size() == combo.length) {
                inputBuffer.clear();
                onCombo(comboIndex); // 传递组合索引
                return;
            }
        }
        // 全错时清空缓冲
        if (!inputBuffer.isEmpty() &&
                !COMBO_LIST.stream().anyMatch(combo -> inputBuffer.size() <= combo.length &&
                        combo[inputBuffer.size()-1] == inputBuffer.get(inputBuffer.size()-1)
                )) {
            inputBuffer.clear();
        }
    }
    private static void onCombo(int comboIndex) {
        // 输出
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack stack = client.player.getMainHandStack();

        if (client.world != null) {
            lastTimes[comboIndex] = client.world.getTime();
        }
        if (!stack.contains(PSComponents.POOP_BALL_COMPONENT)) {
            if (comboIndex == 0) {
                ClientPlayNetworking.send(new PoopBallPayload(1));
            } else if (comboIndex == 1) {
                ClientPlayNetworking.send(new PoopBallPayload(2));
            }
        }
        client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 0.5f, 1.0f);
    }

    private static void playKeySound(int i) {
        int maxComboLength = COMBO_LIST.stream()
                .mapToInt(arr -> arr.length)
                .max()
                .orElse(5);

        float pitch = 1.05F + 0.05F * (i - maxComboLength);
        MinecraftClient.getInstance().player.playSound(
                SoundEvents.UI_BUTTON_CLICK.value(),
                0.35f,
                pitch
        );
    }
}


