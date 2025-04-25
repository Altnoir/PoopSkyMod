package com.altnoir.poopsky.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ComboConfig {
    public static final Map<ComboRecord, Consumer<PlayerEntity>> COMBO_MAP = new LinkedHashMap<>();
    private static ComboRecord activeCombo;

    public static void init() {
        register(new ComboRecord("WDSSS", "message.poopsky.combo.1"), player -> {
            player.sendMessage(Text.translatable("message.poopsky.combo.1"), true);
        });
        activeCombo = COMBO_MAP.keySet().iterator().next();
    }

    public static void register(ComboRecord combo, Consumer<PlayerEntity> action) {
        COMBO_MAP.put(combo, action);
    }

    public static Optional<Consumer<PlayerEntity>> getAction(ComboRecord combo) {
        return Optional.ofNullable(COMBO_MAP.get(combo));
    }

    public static ComboRecord getActiveCombo() {
        return activeCombo;
    }

    public record ComboRecord(String sequence, String displayName) {
        public int[] keyCodes() {
            return KeyUtil.parseSequence(sequence);
        }
    }
}
