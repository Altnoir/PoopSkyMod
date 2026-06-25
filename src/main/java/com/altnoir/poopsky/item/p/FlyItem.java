package com.altnoir.poopsky.item.p;

import com.altnoir.poopsky.init.PComponents;
import com.altnoir.poopsky.init.PFlyTypes;
import com.altnoir.poopsky.init.PItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 苍蝇物品。
 * 只有一种实体对应所有品种，品种由DataComponent区分。
 */
public class FlyItem extends Item {
    public FlyItem(Properties properties) {
        super(properties);
    }

    /**
     * 用指定品种创建苍蝇。
     */
    public static ItemStack withType(PFlyTypes.FlyType type) {
        var stack = new ItemStack(PItems.FLY.get());
        stack.set(PComponents.FLY_TYPE.get(), type.getSerializedName());
        return stack;
    }

    public static PFlyTypes.FlyType getFlyType(ItemStack stack) {
        String typeId = stack.get(PComponents.FLY_TYPE.get());
        return typeId != null ? PFlyTypes.byId(typeId) : PFlyTypes.NORMAL;
    }

    public static boolean isFlyItem(ItemStack stack) {
        return stack.has(PComponents.FLY_TYPE.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        PFlyTypes.FlyType type = getFlyType(stack);
        tooltipComponents.add(Component.translatable("tooltip.poopsky.fly_type")
                .append(": ")
                .append(type.getDisplayName())
                .withStyle(ChatFormatting.GRAY));
    }
}
