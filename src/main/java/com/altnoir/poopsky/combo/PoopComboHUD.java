// ComboHUD.java 修改后
package com.altnoir.poopsky.combo;

import com.altnoir.poopsky.component.PSComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;

import java.util.List;

@Environment(EnvType.CLIENT)
public class PoopComboHUD {
    private static final int HUD_X = 10;
    private static final int HUD_Y = 37;
    private static final int KEY_SPACING = 12;
    private static final int BACKGROUND_PADDING = 3;
    private static final int Y_OFFSET = KEY_SPACING + BACKGROUND_PADDING;

    public static void render(DrawContext context, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        ItemStack stack = client.player.getMainHandStack();

        if (KeyUtil.isCtrlHeld() && KeyUtil.isHoldingPoopBall(client.player) && !stack.contains(PSComponents.POOP_BALL_COMPONENT)) {
            renderComboDisplay(context);
        }
    }

    private static void renderComboDisplay(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int yOffset = 0;

        final int maxComboLength = PoopComboHandler.COMBO_LIST.stream()
                .mapToInt(combo -> combo.length)
                .max()
                .orElse(0);
        final int boxWidth = maxComboLength * KEY_SPACING - BACKGROUND_PADDING;


        // 遍历所有组合键
        for (int comboIndex = 0; comboIndex < PoopComboHandler.COMBO_LIST.size(); comboIndex++) {
            int[] combo = PoopComboHandler.COMBO_LIST.get(comboIndex);
            int currentY = HUD_Y + yOffset;

            // 绘制背景
            context.fill(
                    HUD_X - BACKGROUND_PADDING,
                    currentY - BACKGROUND_PADDING,
                    HUD_X + boxWidth,
                    currentY + KEY_SPACING,
                    0x80000000
            );
            // 冷却CD
            if (PoopComboHandler.isCoolingDown(comboIndex)) {
                int remaining = (int) (PoopComboHandler.COOL_DOWNS[comboIndex] -
                        (client.world.getTime() - PoopComboHandler.lastTimes[comboIndex])) / 20;
                String text = String.format("%02d:%02d", remaining / 60, remaining % 60);

                context.drawTextWithShadow(
                        client.textRenderer,
                        text,
                        HUD_X,
                        HUD_Y + yOffset,
                        0xFFFFFF
                );
                yOffset += Y_OFFSET;
                continue;
            }

            List<Integer> inputBuffer = PoopComboHandler.inputBuffer;
            // 绘制键位序列
            boolean skip = false;
            for (int i = 0; i < inputBuffer.size(); i++) {
                if (i >= combo.length || inputBuffer.get(i) != combo[i]) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                yOffset += Y_OFFSET;
                continue;
            }

            for (int i = 0; i < combo.length; i++) {
                int color = 0xFFFFFF; // 白色

                if (i < inputBuffer.size()) color = 0x808080 ; // 灰色
                context.drawTextWithShadow(
                        client.textRenderer,
                        KeyUtil.getKey(combo[i]),
                        HUD_X + i * KEY_SPACING,
                        currentY,
                        color
                );
            }
            yOffset += Y_OFFSET;
        }
    }
}
