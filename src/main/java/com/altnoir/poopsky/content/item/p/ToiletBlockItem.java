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
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class ToiletBlockItem extends BlockItem {
    public ToiletBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack withType(Block block, ToiletType toiletType) {
        var stack = new ItemStack(block);
        stack.set(PoComponents.TOILET_TYPE.get(), toiletType);
        return stack;
    }

    private static final String TOILET_FORMAT_KEY = "block.poopsky.toilet_format";

    @Override
    public Component getName(ItemStack stack) {
        ToiletType type = stack.get(PoComponents.TOILET_TYPE.get());
        if (type != null && type.nameKey() != null) {
            return Component.translatable(TOILET_FORMAT_KEY, Component.translatable(type.nameKey()));
        }
        return super.getName(stack);
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) return null;

        ToiletType type = context.getItemInHand().get(PoComponents.TOILET_TYPE.get());
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
            ToiletType type = context.getItemInHand().get(PoComponents.TOILET_TYPE.get());
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, tooltipFlag);
        ToiletType type = stack.get(PoComponents.TOILET_TYPE.get());
        if (type == null) return;

        consumer.accept(Component.translatable("tooltip.poopsky.toilet_type")
                .append(": ")
                .append(type.displayName())
                .withStyle(ChatFormatting.GRAY));
    }
}