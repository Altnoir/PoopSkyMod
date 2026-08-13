package com.altnoir.poopsky.content.item;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class PoBaseItem extends Item {
    public PoBaseItem(Properties properties) {
        super(properties);
    }

    public boolean isDisplay(ItemStack stack) {
        return false;
    }

    public void appendShiftTooltip(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
    }
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        appendTooltip(stack, context, tooltipDisplay, consumer, tooltipFlag);
        if (!isShiftDown() && isDisplay(stack))
            consumer.accept(Component.translatable("tooltip.poopsky.item.info_0"));
        else {
            appendShiftTooltip(stack, context, tooltipDisplay, consumer, tooltipFlag);
        }
    }

    private boolean isShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_LSHIFT)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_RSHIFT);
    }
}
