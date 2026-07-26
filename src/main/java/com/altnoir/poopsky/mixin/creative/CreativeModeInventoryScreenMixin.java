package com.altnoir.poopsky.mixin.creative;

import com.altnoir.poopsky.client.creative.PoSectionedCreativeTabRenderer;
import com.altnoir.poopsky.impl.creative.PoSectionedCreativeModeTab;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Shadow
    private float scrollOffs;

    private CreativeModeInventoryScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void poopsky$renderSectionHeadings(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (selectedTab instanceof PoSectionedCreativeModeTab sectionedTab) {
            PoSectionedCreativeTabRenderer.render(
                    graphics,
                    sectionedTab,
                    scrollOffs,
                    this.leftPos,
                    this.topPos
            );
        } else {
            PoSectionedCreativeTabRenderer.clearHeadingSlots();
        }
    }
}