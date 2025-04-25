package com.altnoir.poopsky.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public class ComboHUD {
    private static final int HUD_X = 10;
    private static final int HUD_Y = 50;
    private static final int KEY_SPACING = 12;
    private static final int BACKGROUND_PADDING = 3;

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!shouldRender(client)) return;

        ComboConfig.ComboRecord combo = ComboConfig.getActiveCombo();
        renderComboDisplay(context, combo);
    }
    private static boolean shouldRender(MinecraftClient client) {
        return client.player != null && KeyUtil.isCtrlHeld() && KeyUtil.isHoldingPoopBall(client.player);
    }

    private static void renderComboDisplay(DrawContext context, ComboConfig.ComboRecord combo) {
        int keyCount = combo.sequence().length();
        int boxWidth = keyCount * KEY_SPACING - BACKGROUND_PADDING;
        int boxHeight = KEY_SPACING + BACKGROUND_PADDING;
        // 绘制背景
        context.fill(HUD_X - BACKGROUND_PADDING, HUD_Y - BACKGROUND_PADDING, HUD_X + boxWidth, HUD_Y + boxHeight, 0x80000000);

        // 绘制目标序列
        int[] targetKeys = combo.keyCodes();
        for (int i = 0; i < targetKeys.length; i++) {
            MinecraftClient client = MinecraftClient.getInstance();
            int color = (i < ComboHandler.getInputBuffer().size()) ? 0x808080 : 0xFFFFFF;
            context.drawTextWithShadow(client.textRenderer, KeyUtil.getKeySymbol(targetKeys[i]),
                    HUD_X + i * KEY_SPACING, HUD_Y, color);
        }
    }
}

