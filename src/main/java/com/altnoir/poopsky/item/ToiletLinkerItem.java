package com.altnoir.poopsky.item;

import com.altnoir.poopsky.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.component.PSComponents;
import com.altnoir.poopsky.component.ToiletComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ToiletLinkerItem extends PSBaseItem {
    public ToiletLinkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemstack = player.getItemInHand(usedHand);
        if (level.isClientSide && !player.isShiftKeyDown())
            return super.use(level, player, usedHand);

        itemstack.set(PSComponents.TOILET_COMPONENT, ToiletComponent.EMPTY);
        player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.3").withStyle(style -> style.withColor(0xAA0000)), true);
        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        var stack = context.getItemInHand();

        if (!(level.getBlockEntity(pos) instanceof ToiletBlockEntity)) {
            return InteractionResult.PASS;
        }

        var comp = stack.getOrDefault(PSComponents.TOILET_COMPONENT.get(), ToiletComponent.EMPTY);
        var dimKey = level.dimension().location().toString();

        if (player != null && !player.isShiftKeyDown()) {
            if (comp.level1().isEmpty()) {
                stack.set(PSComponents.TOILET_COMPONENT.get(), new ToiletComponent(
                        dimKey, comp.level2(),
                        pos.getX(), pos.getY(), pos.getZ(),
                        comp.x2(), comp.y2(), comp.z2()
                ));
                player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.1"), true);
            } else if (comp.level2().isEmpty()) {
                stack.set(PSComponents.TOILET_COMPONENT.get(), new ToiletComponent(
                        comp.level1(), dimKey,
                        comp.x1(), comp.y1(), comp.z1(),
                        pos.getX(), pos.getY(), pos.getZ()
                ));
                player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.2"), true);
                linkToilets(stack, level, player);
            }
        } else {
            stack.set(PSComponents.TOILET_COMPONENT.get(), ToiletComponent.EMPTY);
            player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.4"), true);
        }

        return InteractionResult.SUCCESS;
    }

    private void linkToilets(ItemStack stack, Level level, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        var comp = stack.get(PSComponents.TOILET_COMPONENT.get());
        var server = serverLevel.getServer();

        if (comp == null) return;

        var level1 = server.getLevel(Level.OVERWORLD);
        if (!comp.level1().isEmpty()) level1 = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(comp.level1())));
        var level2 = server.getLevel(Level.OVERWORLD);
        if (!comp.level2().isEmpty()) level2 = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(comp.level2())));

        if (level1 == null || level2 == null) return;

        var pos1 = new BlockPos(comp.x1(), comp.y1(), comp.z1());
        var pos2 = new BlockPos(comp.x2(), comp.y2(), comp.z2());

        if (level1.getBlockEntity(pos1) instanceof ToiletBlockEntity toilet1 &&
                level2.getBlockEntity(pos2) instanceof ToiletBlockEntity toilet2) {

            toilet1.setLinkedPos(pos2, level2);
            toilet2.setLinkedPos(pos1, level1);
            toilet1.setChanged();
            toilet2.setChanged();

            level1.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos2), 1, pos2);
            level2.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos1), 1, pos1);

            var s1 = level1.getBlockState(pos1);
            var s2 = level2.getBlockState(pos2);
            level1.sendBlockUpdated(pos1, s1, s1, Block.UPDATE_ALL);
            level2.sendBlockUpdated(pos2, s2, s2, Block.UPDATE_ALL);

            player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.3").withStyle(style -> style.withColor(0x00AA00)), true);
            stack.set(PSComponents.TOILET_COMPONENT.get(), ToiletComponent.EMPTY);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var comp = stack.get(PSComponents.TOILET_COMPONENT);
        if (comp == null) return;
        var dim1 = comp.level1();
        var dim2 = comp.level2();

        if (dim1 == null || dim2 == null) return;

        if (dim1.isEmpty()) return;
        int x1 = comp.x1(), y1 = comp.y1(), z1 = comp.z1();
        tooltipComponents.add(Component.translatable("tooltip.poopsky.toilet_linker.info_1", dim1, x1, y1, z1).withStyle(style -> style.withColor(ChatFormatting.GRAY)));

        if (dim2.isEmpty()) return;
        int x2 = comp.x2(), y2 = comp.y2(), z2 = comp.z2();
        tooltipComponents.add(Component.translatable("tooltip.poopsky.toilet_linker.info_2", dim2, x2, y2, z2).withStyle(style -> style.withColor(ChatFormatting.GRAY)));
    }
}
