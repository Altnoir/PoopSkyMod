package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.entity.p.FlushToiletCartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class FlushToiletCartItem extends Item {
    private final Supplier<? extends EntityType<FlushToiletCartEntity>> entityType;

    public FlushToiletCartItem(Supplier<? extends EntityType<FlushToiletCartEntity>> entityType, Properties properties) {
        super(properties);
        this.entityType = entityType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        FlushToiletCartEntity cart = this.entityType.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
        if (cart == null) {
            return InteractionResult.PASS;
        }

        float yRot = context.getPlayer() != null ? context.getPlayer().getYRot() : 0.0F;
        cart.setPos(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5);
        cart.setYRot(yRot);
        if (!serverLevel.noCollision(cart)) {
            return InteractionResult.PASS;
        }

        serverLevel.addFreshEntity(cart);
        ItemStack stack = context.getItemInHand();
        if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
