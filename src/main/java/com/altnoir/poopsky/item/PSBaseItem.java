package com.altnoir.poopsky.item;

import com.altnoir.poopsky.component.PSComponents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
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

    public boolean isShiftDown(){
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LSHIFT)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_RSHIFT);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!isShiftDown())
            tooltipComponents.add(Component.translatable("tooltip.poopsky.item.info_0"));
        else {
            appendTooltip(stack, context, tooltipComponents, tooltipFlag);
        }
    }

    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

    }
}
