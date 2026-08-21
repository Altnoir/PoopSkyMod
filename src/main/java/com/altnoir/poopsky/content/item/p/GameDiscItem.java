package com.altnoir.poopsky.content.item.p;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class GameDiscItem extends Item {
    private final Component name;

    public GameDiscItem(Properties properties, Component name) {
        super(properties.stacksTo(1));
        this.name = name;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(name);
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
