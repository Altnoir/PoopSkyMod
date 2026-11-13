package com.altnoir.poopsky.client.inventory;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FlyBarrelScreen extends AbstractContainerScreen<FlyBarrelMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = PoopSky.loc("textures/gui/fly_barrel.png");
    private static final int TANK_X = 8;
    private static final int TANK_Y = 8;
    private static final int TANK_WIDTH = 160;
    private static final int TANK_HEIGHT = 37;
    private static final int PROGRESS_X = 48;
    private static final int PROGRESS_Y = 19;
    private static final int PROGRESS_U = 176;
    private static final int PROGRESS_V = 0;
    private static final int PROGRESS_WIDTH = 26;
    private static final int PROGRESS_HEIGHT = 16;

    public FlyBarrelScreen(FlyBarrelMenu menu, Inventory playerInventory, Component title) {
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
        this.renderFluid(graphics, x, y);
        graphics.blit(CONTAINER_BACKGROUND, x + 25, y + 19, 25, 133, 126, 18);

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

    private void renderFluid(GuiGraphics graphics, int x, int y) {
        int amount = Mth.clamp(this.menu.getFluidAmount(), 0, FlyBarrelBlockEntity.FLUID_CAPACITY);
        if (amount == 0) {
            return;
        }
        int height = Mth.ceil((float) amount * TANK_HEIGHT / FlyBarrelBlockEntity.FLUID_CAPACITY);
        FluidStack fluid = new FluidStack(this.menu.getFluid(), amount);
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(extensions.getStillTexture(fluid));
        int tint = extensions.getTintColor(fluid);
        float red = FastColor.ARGB32.red(tint) / 255.0F;
        float green = FastColor.ARGB32.green(tint) / 255.0F;
        float blue = FastColor.ARGB32.blue(tint) / 255.0F;
        float alpha = FastColor.ARGB32.alpha(tint) / 255.0F;

        int left = x + TANK_X;
        int bottom = y + TANK_Y + TANK_HEIGHT;
        graphics.enableScissor(left, bottom - height, left + TANK_WIDTH, bottom);
        RenderSystem.enableBlend();
        for (int dx = 0; dx < TANK_WIDTH; dx += 16) {
            for (int dy = 0; dy < height; dy += 16) {
                graphics.blit(left + dx, bottom - dy - 16, 0, 16, 16, sprite, red, green, blue, alpha);
            }
        }
        graphics.flush();
        RenderSystem.disableBlend();
        graphics.disableScissor();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
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

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }
}
