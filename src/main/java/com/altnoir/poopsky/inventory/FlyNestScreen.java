package com.altnoir.poopsky.inventory;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FlyNestScreen extends AbstractContainerScreen<FlyNestMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = 
            PoopSky.loc("textures/gui/fly_nest.png");
    private static final int PROGRESS_X = 47;
    private static final int PROGRESS_Y = 20;
    private static final int PROGRESS_U = 176;
    private static final int PROGRESS_V = 0;
    private static final int PROGRESS_WIDTH = 26;
    private static final int PROGRESS_HEIGHT = 16;

    public FlyNestScreen(FlyNestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 132;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int maxProgress = this.menu.getMaxProgress();
        if (maxProgress > 0) {
            int progressWidth = Math.min(PROGRESS_WIDTH, this.menu.getProgress() * PROGRESS_WIDTH / maxProgress);
            if (progressWidth > 0) {
                graphics.blit(CONTAINER_BACKGROUND, x + PROGRESS_X, y + PROGRESS_Y,
                        PROGRESS_U, PROGRESS_V, progressWidth, PROGRESS_HEIGHT);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
