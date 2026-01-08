package com.altnoir.poopsky.item.p;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PSBaseItem extends Item {
    public PSBaseItem(Properties properties) {
        super(properties);
    }

    public boolean isDisplay(ItemStack stack) {
        return false;
    }

    public void appendShiftTooltip(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
    }
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendTooltip(stack, context, tooltipComponents, tooltipFlag);
        if (!isShiftDown() && isDisplay(stack))
            tooltipComponents.add(Component.translatable("tooltip.poopsky.item.info_0"));
        else {
            appendShiftTooltip(stack, context, tooltipComponents, tooltipFlag);
        }
    }

    private boolean isShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_RSHIFT);
    }
}
