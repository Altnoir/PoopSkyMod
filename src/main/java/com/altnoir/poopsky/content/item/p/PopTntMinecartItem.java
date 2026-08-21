package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.init.PoEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class PopTntMinecartItem extends Item {
    public PopTntMinecartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        if (!serverLevel.getBlockState(context.getClickedPos()).is(net.minecraft.world.level.block.Blocks.RAIL)) {
            return InteractionResult.PASS;
        }
        var cart = PoEntityType.POP_TNT_MINECART.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
        if (cart == null) return InteractionResult.PASS;
        cart.snapTo(pos.getX() + 0.5, pos.getY() + 0.0625, pos.getZ() + 0.5,
                context.getPlayer() == null ? 0.0F : context.getPlayer().getYRot(), 0.0F);
        if (!serverLevel.noCollision(cart)) return InteractionResult.PASS;
        serverLevel.addFreshEntity(cart);
        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
