package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BreedingChestScreen extends AbstractContainerScreen<BreedingChestMenu> {
    private static final Identifier CONTAINER_BACKGROUND =
            PoopSky.loc("textures/gui/breeding_chest.png");
    private static final int PROGRESS_X = 84;
    private static final int PROGRESS_Y = 22;
    private static final int PROGRESS_U = 176;
    private static final int PROGRESS_V = 0;
    private static final int PROGRESS_WIDTH = 26;
    private static final int PROGRESS_HEIGHT = 12;

    public BreedingChestScreen(BreedingChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 132);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, this.leftPos, this.topPos,
                0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        int maxProgress = this.menu.getMaxProgress();
        int progress = this.menu.getProgress();
        if (maxProgress > 0) {
            int progressWidth = Math.min(PROGRESS_WIDTH, (int) ((float) progress / maxProgress * PROGRESS_WIDTH + 0.5F));
            if (progressWidth >= 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND,
                        this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y,
                        PROGRESS_U, PROGRESS_V, progressWidth, PROGRESS_HEIGHT, 256, 256);
            }
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (mouseX >= this.leftPos + PROGRESS_X && mouseX < this.leftPos + PROGRESS_X + PROGRESS_WIDTH &&
                mouseY >= this.topPos + PROGRESS_Y && mouseY < this.topPos + PROGRESS_Y + PROGRESS_HEIGHT) {
            int maxProgress = this.menu.getMaxProgress();
            int progress = this.menu.getProgress();
            if (maxProgress > 0) {
                float percent = (float) progress / maxProgress * 100.0F;
                graphics.setTooltipForNextFrame(this.font,
                        Component.literal(String.format("%.1f%%", percent)).withStyle(ChatFormatting.GRAY), mouseX, mouseY);
            }
        }
    }
}