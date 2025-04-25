package com.altnoir.poopsky.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ComboHandler {
    private static final int BUFFER_SIZE = 10;
    private static final ArrayDeque<Integer> inputBuffer = new ArrayDeque<>(BUFFER_SIZE);
    public static final Set<Integer> pressedKeys = new HashSet<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!shouldProcessInput(client)) {
                if (!inputBuffer.isEmpty()) {
                    inputBuffer.clear();
                    pressedKeys.clear();
                }
                return;
            }
            processKeys(client.getWindow().getHandle());
            checkCombination(client.player);
        });
    }

    private static boolean shouldProcessInput(MinecraftClient client) {
        return client.player != null
                && KeyUtil.isCtrlHeld()
                && KeyUtil.isHoldingPoopBall(client.player);
    }

    private static void processKeys(long window) {
        Arrays.stream(KeyUtil.MOVEMENT_KEYS)
                .filter(key -> KeyUtil.isNewKeyPress(window, key))
                .findFirst()
                .ifPresent(key -> {
                    // 添加新按键前检查是否存在有效前缀
                    String currentInput = inputBuffer.stream()
                            .map(KeyUtil::getKeySymbol)
                            .collect(Collectors.joining());

                    if (hasValidPrefix(currentInput + KeyUtil.getKeySymbol(key))) {
                        inputBuffer.clear();
                    }

                    // 保持缓冲区大小限制
                    if (inputBuffer.size() >= BUFFER_SIZE) {
                        inputBuffer.removeFirst();
                    }
                    inputBuffer.addLast(key);

                    // 添加后再次检查有效性
                    currentInput = inputBuffer.stream()
                            .map(KeyUtil::getKeySymbol)
                            .collect(Collectors.joining());

                    if (hasValidPrefix(currentInput)) {
                        inputBuffer.clear();
                    }
                });
    }
    private static boolean hasValidPrefix(String input) {
        return ComboConfig.COMBO_MAP.keySet().stream()
                .noneMatch(combo -> combo.sequence().startsWith(input));
    }
    private static void checkCombination(PlayerEntity player) {
        String input = inputBuffer.stream()
                .map(KeyUtil::getKeySymbol)
                .collect(Collectors.joining());

        ComboConfig.COMBO_MAP.keySet().stream()
                .filter(combo -> input.endsWith(combo.sequence()))
                .findFirst()
                .ifPresent(matched -> {
                    ComboConfig.COMBO_MAP.get(matched).accept(player);
                    inputBuffer.clear();
                    pressedKeys.clear();
                });
    }

    public static List<Integer> getInputBuffer() {
        return new ArrayList<>(inputBuffer);
    }
}

