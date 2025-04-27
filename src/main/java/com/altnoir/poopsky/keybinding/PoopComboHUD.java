// ComboHUD.java 修改后
package com.altnoir.poopsky.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PoopComboHUD {
    private static final int HUD_X = 10;
    private static final int HUD_Y = 37;
    private static final int KEY_SPACING = 12;
    private static final int BACKGROUND_PADDING = 3;

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!shouldRender(client)) return;
        // 获取目标组合键
        renderComboDisplay(context);
    }

    private static boolean shouldRender(MinecraftClient client) {
        return client.player != null
                && KeyUtil.isCtrlHeld()
                && KeyUtil.isHoldingPoopBall(client.player);
    }
    private static void renderComboDisplay(DrawContext context) {
        int keyCount = PoopComboHandler.COMBO_1.length;
        int boxWidth = keyCount * KEY_SPACING - BACKGROUND_PADDING;

        // 绘制半透明背景
        context.fill(
                HUD_X - BACKGROUND_PADDING,
                HUD_Y - BACKGROUND_PADDING,
                HUD_X + boxWidth,
                HUD_Y + KEY_SPACING,
                0x80000000
        );

        // 绘制键位序列
        List<Integer> inputBuffer = PoopComboHandler.getInputBuffer();
        for (int i = 0; i < PoopComboHandler.COMBO_1.length; i++) {
            int color;
            if (i < inputBuffer.size()) {
                // 已输入部分：正确显示灰色，错误显示红色
                color = (inputBuffer.get(i) == PoopComboHandler.COMBO_1[i]) ? 0x808080 : 0xFF0000;
            } else {
                // 未输入部分保持白色
                color = 0xFFFFFF;
            }
            context.drawTextWithShadow(
                    MinecraftClient.getInstance().textRenderer,
                    KeyUtil.getKey(PoopComboHandler.COMBO_1[i]),
                    HUD_X + i * KEY_SPACING,
                    HUD_Y,
                    color
            );
        }
    }
}
