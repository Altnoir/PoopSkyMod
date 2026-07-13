package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.FlyTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class FlyItem extends Item {
    public FlyItem(Properties properties) {
        super(properties);
    }

    public static ItemStack withType(FlyType.Type type) {
        var stack = new ItemStack(PoItems.FLY.get());
        stack.set(PoComponents.FLY_TYPE.get(), type.id());
        return stack;
    }

    public static ItemStack withType(String typeId) {
        var stack = new ItemStack(PoItems.FLY.get());
        stack.set(PoComponents.FLY_TYPE.get(), typeId);
        return stack;
    }

    public static FlyType.Type getFlyType(ItemStack stack) {
        String typeId = stack.get(PoComponents.FLY_TYPE.get());
        return typeId != null ? FlyType.byId(typeId) : FlyTypes.NORMAL.get();
    }

    public static boolean isFlyItem(ItemStack stack) {
        return stack.has(PoComponents.FLY_TYPE.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        FlyType.Type type = getFlyType(stack);
        tooltipComponents.add(Component.translatable("tooltip.poopsky.fly_type")
                .append(": ")
                .append(type.getDisplayName())
                .withStyle(ChatFormatting.GRAY));
    }
}