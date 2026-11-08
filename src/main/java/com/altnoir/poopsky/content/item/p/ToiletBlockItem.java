package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.HardToiletBlock;
import com.altnoir.poopsky.content.block.p.WoodToiletBlock;
import com.altnoir.poopsky.init.PoComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ToiletBlockItem extends BlockItem {
    public ToiletBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack withType(Block block, ToiletType toiletType) {
        var stack = new ItemStack(block);
        PoComponents.TOILET_TYPE.set(stack, toiletType);
        return stack;
    }

    private static final String TOILET_FORMAT_KEY = "block.poopsky.toilet_format";

    @Override
    public Component getName(ItemStack stack) {
        ToiletType type = PoComponents.TOILET_TYPE.get(stack);
        if (type != null && type.nameKey() != null) {
            return Component.translatable(TOILET_FORMAT_KEY, Component.translatable(type.nameKey()));
        }
        return super.getName(stack);
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) return null;

        ToiletType type = PoComponents.TOILET_TYPE.get(context.getItemInHand());
        if (type == null) return state;

        Block block = getBlock();
        if (block instanceof HardToiletBlock lava) {
            return lava.applyVariant(state, type);
        } else if (block instanceof WoodToiletBlock) {
            return state;
        }

        return state;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean result = super.placeBlock(context, state);
        if (result) {
            ToiletType type = PoComponents.TOILET_TYPE.get(context.getItemInHand());
            if (type != null) {
                Level level = context.getLevel();
                BlockPos pos = context.getClickedPos();
                if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be) {
                    be.setToiletType(type);
                }
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        ToiletType type = PoComponents.TOILET_TYPE.get(stack);
        if (type == null) return;

        tooltipComponents.add(Component.translatable("tooltip.poopsky.toilet_type")
                .append(": ")
                .append(type.displayName())
                .withStyle(ChatFormatting.GRAY));
    }
}