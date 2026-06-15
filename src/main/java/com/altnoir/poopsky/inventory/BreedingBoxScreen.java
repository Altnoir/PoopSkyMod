package com.altnoir.poopsky.inventory;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BreedingBoxScreen extends AbstractContainerScreen<BreedingBoxMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = 
            PoopSky.loc("textures/gui/breeding_box.png");

    public BreedingBoxScreen(BreedingBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 168;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, 168);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
