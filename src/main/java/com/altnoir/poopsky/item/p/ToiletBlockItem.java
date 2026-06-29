package com.altnoir.poopsky.item.p;

import com.altnoir.poopsky.block.p.LavaToiletBlock;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.init.PComponents;
import com.altnoir.poopsky.block.ToiletType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ToiletBlockItem extends BlockItem {
    public ToiletBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack withType(Block block, ToiletType toiletType) {
        var stack = new ItemStack(block);
        stack.set(PComponents.TOILET_TYPE.get(), toiletType);
        return stack;
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) return null;

        ToiletType type = context.getItemInHand().get(PComponents.TOILET_TYPE.get());
        if (type == null) return state;

        Block block = getBlock();
        if (block instanceof ToiletBlock toilet) {
            return toilet.applyVariant(state, type);
        } else if (block instanceof LavaToiletBlock lava) {
            return lava.applyVariant(state, type);
        }

        return state;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        ToiletType type = stack.get(PComponents.TOILET_TYPE.get());
        if (type == null) return;

        tooltipComponents.add(Component.translatable("tooltip.poopsky.toilet_type")
                .append(": ")
                .append(type.displayName())
                .withStyle(ChatFormatting.GRAY));
    }
}