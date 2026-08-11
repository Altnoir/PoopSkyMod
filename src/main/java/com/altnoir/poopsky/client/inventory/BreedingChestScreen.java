package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
        super(menu, playerInventory, title);
        this.imageHeight = 132;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int maxProgress = this.menu.getMaxProgress();
        int progress = this.menu.getProgress();
        if (maxProgress > 0) {
            int progressWidth = Math.min(PROGRESS_WIDTH, (int) ((float) progress / maxProgress * PROGRESS_WIDTH + 0.5F));
            if (progressWidth >= 0) {
                graphics.blit(CONTAINER_BACKGROUND, x + PROGRESS_X, y + PROGRESS_Y,
                        PROGRESS_U, PROGRESS_V, progressWidth, PROGRESS_HEIGHT);
            }
        }
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + PROGRESS_X && mouseX < x + PROGRESS_X + PROGRESS_WIDTH &&
                mouseY >= y + PROGRESS_Y && mouseY < y + PROGRESS_Y + PROGRESS_HEIGHT) {
            int maxProgress = this.menu.getMaxProgress();
            int progress = this.menu.getProgress();
            if (maxProgress > 0) {
                float percent = (float) progress / maxProgress * 100.0F;
                graphics.renderTooltip(this.font,
                        Component.literal(String.format("%.1f%%", percent)).withStyle(ChatFormatting.GRAY), mouseX, mouseY);
            }
        }
    }
}