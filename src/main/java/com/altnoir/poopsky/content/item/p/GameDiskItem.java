package com.altnoir.poopsky.content.item.p;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class GameDiskItem extends Item {
    private final Component name;

    public GameDiskItem(Properties properties, Component name) {
        super(properties.stacksTo(1));
        this.name = name;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(name);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
