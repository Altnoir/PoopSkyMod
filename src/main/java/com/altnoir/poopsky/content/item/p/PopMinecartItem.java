package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.entity.p.PopTntMinecartEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class PopMinecartItem extends Item {
    private final Supplier<? extends EntityType<PopTntMinecartEntity>> entityType;

    public PopMinecartItem(Supplier<? extends EntityType<PopTntMinecartEntity>> entityType, Properties properties) {
        super(properties);
        this.entityType = entityType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        if (!state.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        PopTntMinecartEntity cart = this.entityType.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
        if (cart == null) {
            return InteractionResult.PASS;
        }

        EntityType.<PopTntMinecartEntity>createDefaultStackConfig(serverLevel, stack, context.getPlayer()).accept(cart);
        double offset = state.getBlock() instanceof BaseRailBlock rail
                && rail.getRailDirection(state, serverLevel, context.getClickedPos(), null).isSlope() ? 0.5 : 0.0;
        cart.snapTo(context.getClickedPos().getX() + 0.5,
                context.getClickedPos().getY() + 0.0625 + offset,
                context.getClickedPos().getZ() + 0.5,
                context.getPlayer() == null ? 0.0F : context.getPlayer().getYRot(), 0.0F);
        serverLevel.addFreshEntity(cart);
        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
