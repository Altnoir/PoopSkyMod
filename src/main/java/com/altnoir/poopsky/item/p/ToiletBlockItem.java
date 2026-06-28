package com.altnoir.poopsky.item.p;

import com.altnoir.poopsky.block.p.MetalToiletBlock;
import com.altnoir.poopsky.block.p.StoneToiletBlock;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.init.PComponents;
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

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) return null;

        ItemStack stack = context.getItemInHand();
        String variant = stack.get(PComponents.TOILET_TYPE.get());
        if (variant == null) return state;

        Block block = getBlock();
        if (block instanceof ToiletBlock toilet) {
            return toilet.applyVariant(state, variant);
        } else if (block instanceof StoneToiletBlock stone) {
            return stone.applyVariant(state, variant);
        } else if (block instanceof MetalToiletBlock metal) {
            return metal.applyVariant(state, variant);
        }

        return state;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        String variant = stack.get(PComponents.TOILET_TYPE.get());
        if (variant == null) return;

        Block block = getBlock();
        Component typeName;
        if (block instanceof ToiletBlock) {
            try {
                typeName = ToiletBlock.WoodType.valueOf(variant.toUpperCase()).getDisplayName();
            } catch (IllegalArgumentException e) {
                typeName = Component.literal(variant);
            }
        } else if (block instanceof StoneToiletBlock) {
            try {
                typeName = StoneToiletBlock.StoneType.valueOf(variant.toUpperCase()).getDisplayName();
            } catch (IllegalArgumentException e) {
                typeName = Component.literal(variant);
            }
        } else if (block instanceof MetalToiletBlock) {
            try {
                typeName = MetalToiletBlock.MetalType.valueOf(variant.toUpperCase()).getDisplayName();
            } catch (IllegalArgumentException e) {
                typeName = Component.literal(variant);
            }
        } else {
            typeName = Component.literal(variant);
        }

        tooltipComponents.add(Component.translatable("tooltip.poopsky.toilet_type")
                .append(": ")
                .append(typeName)
                .withStyle(ChatFormatting.GRAY));
    }
}