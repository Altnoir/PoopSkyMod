package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.ToiletComponent;
import com.altnoir.poopsky.content.block.entity.FlushToiletBlockEntity;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.item.PoBaseItem;
import com.altnoir.poopsky.data.sound.PoSoundEvents;
import com.altnoir.poopsky.init.PoComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class ToiletLinkerItem extends PoBaseItem {
    public ToiletLinkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return ToiletPlugItem.poisonOnHit(target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.sidedSuccess(resetComponent(stack, player), level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        ItemStack stack = context.getItemInHand();
        if (player.isShiftKeyDown()) {
            resetComponent(stack, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        BlockPos pos = context.getClickedPos();
        var be = level.getBlockEntity(pos);
        if (!(be instanceof ToiletBlockEntity || be instanceof FlushToiletBlockEntity)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        executeBindingLogic((ServerLevel) level, pos, player, stack);
        return InteractionResult.SUCCESS;
    }

    private ItemStack resetComponent(ItemStack stack, Player player) {
        stack.set(PoComponents.TOILET_COMPONENT.get(), ToiletComponent.EMPTY);
        player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.4").withStyle(ChatFormatting.RED), true);
        return stack;
    }

    private void executeBindingLogic(ServerLevel level, BlockPos pos, Player player, ItemStack stack) {
        ToiletComponent comp = stack.getOrDefault(PoComponents.TOILET_COMPONENT.get(), ToiletComponent.EMPTY);
        String dimKey = level.dimension().location().toString();
        if (comp.level1().isEmpty()) {
            // Bind first endpoint
            stack.set(PoComponents.TOILET_COMPONENT.get(), new ToiletComponent(
                    dimKey, comp.level2(),
                    pos.getX(), pos.getY(), pos.getZ(),
                    comp.x2(), comp.y2(), comp.z2()
            ));
            var pitch = level.random.nextFloat() + 0.1F;
            player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.1"), true);
            level.playSound(null, pos, PoSoundEvents.ITEM_TOILET_LINKER_BOOP.get(), SoundSource.PLAYERS, 1.0F, pitch);
        } else if (comp.level2().isEmpty()) {
            // Temporarily store second endpoint data
            ToiletComponent fullComp = new ToiletComponent(
                    comp.level1(), dimKey,
                    comp.x1(), comp.y1(), comp.z1(),
                    pos.getX(), pos.getY(), pos.getZ()
            );
            linkToilets(stack, level, player, fullComp);
        }
    }

    private void linkToilets(ItemStack stack, ServerLevel currentLevel, Player player, ToiletComponent comp) {
        var server = currentLevel.getServer();
        ServerLevel level1 = getLevelFromKey(server, comp.level1()).orElse(currentLevel);
        ServerLevel level2 = getLevelFromKey(server, comp.level2()).orElse(currentLevel);
        BlockPos pos1 = new BlockPos(comp.x1(), comp.y1(), comp.z1());
        BlockPos pos2 = new BlockPos(comp.x2(), comp.y2(), comp.z2());
        if (isLinkableToilet(level1.getBlockEntity(pos1)) && isLinkableToilet(level2.getBlockEntity(pos2))) {
            setToiletLink(level1.getBlockEntity(pos1), pos2, level2);
            setToiletLink(level2.getBlockEntity(pos2), pos1, level1);
            // Mark both block entities as changed
            if (level1.getBlockEntity(pos1) instanceof BlockEntity be1) be1.setChanged();
            if (level2.getBlockEntity(pos2) instanceof BlockEntity be2) be2.setChanged();
            level1.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos1), 1, pos1);
            level2.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos2), 1, pos2);
            notifyBlockUpdate(level1, pos1);
            notifyBlockUpdate(level2, pos2);
            player.displayClientMessage(Component.translatable("message.poopsky.toilet_linker.3").withStyle(ChatFormatting.GREEN), true);
            stack.set(PoComponents.TOILET_COMPONENT.get(), ToiletComponent.EMPTY);
            var pitch = level2.random.nextFloat() + 0.3F;
            level2.playSound(null, pos2, PoSoundEvents.ITEM_TOILET_LINKER_SUCCESS.get(), SoundSource.BLOCKS, 1.0F, pitch);
        }
    }

    private static boolean isLinkableToilet(Object be) {
        return be instanceof ToiletBlockEntity || be instanceof FlushToiletBlockEntity;
    }

    private static void setToiletLink(Object be, BlockPos targetPos, ServerLevel targetLevel) {
        if (be instanceof ToiletBlockEntity tbe) {
            tbe.setLinkedPos(targetPos, targetLevel);
        } else if (be instanceof FlushToiletBlockEntity fbe) {
            fbe.setLinkedPos(targetPos, targetLevel);
        }
    }

    private Optional<ServerLevel> getLevelFromKey(MinecraftServer server, String dimStr) {
        if (dimStr.isEmpty()) return Optional.empty();
        ResourceLocation loc = ResourceLocation.tryParse(dimStr);
        if (loc == null) return Optional.empty();
        ResourceKey<Level> registryKey = ResourceKey.create(Registries.DIMENSION, loc);
        return Optional.ofNullable(server.getLevel(registryKey));
    }

    private void notifyBlockUpdate(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
    }

    @Override
    public boolean isDisplay(ItemStack stack) {
        var comp = stack.get(PoComponents.TOILET_COMPONENT.get());
        return comp != null && (!comp.level1().isEmpty() || !comp.level2().isEmpty());
    }

    @Override
    public void appendShiftTooltip(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var comp = stack.get(PoComponents.TOILET_COMPONENT.get());
        if (comp == null) return;
        if (!comp.level1().isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.poopsky.toilet_linker.info_1", comp.level1(), comp.x1(), comp.y1(), comp.z1()).withStyle(ChatFormatting.GRAY));
        }
        if (!comp.level2().isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.poopsky.toilet_linker.info_2", comp.level2(), comp.x2(), comp.y2(), comp.z2()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.poopsky.item.info_1"));
    }
}
