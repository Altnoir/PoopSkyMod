package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PortableToiletScreen extends AbstractContainerScreen<PortableToiletMenu> {
    private static final Identifier CONTAINER_BACKGROUND = PoopSky.loc("textures/gui/flush_toilet.png");

    public PortableToiletScreen(PortableToiletMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 133);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, this.leftPos, this.topPos,
                0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
