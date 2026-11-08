package com.altnoir.poopsky.content.item.p;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.gameevent.GameEvent;
import java.util.function.Supplier;

public class PoBoatItem extends BoatItem {
    private final Supplier<? extends EntityType<? extends Boat>> entityType;
    public PoBoatItem(boolean hasChest, Supplier<? extends EntityType<? extends Boat>> entityType, Properties properties) {
        super(hasChest, Boat.Type.OAK, properties);
        this.entityType = entityType;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() == HitResult.Type.MISS) return InteractionResultHolder.pass(stack);
        Vec3 eye = player.getEyePosition();
        for (var entity : level.getEntities(player, player.getBoundingBox().expandTowards(player.getViewVector(1).scale(5)).inflate(1), e -> !e.isSpectator() && e.isPickable())) {
            if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(eye)) return InteractionResultHolder.pass(stack);
        }
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);
        Boat boat = entityType.get().create(level);
        if (boat == null) return InteractionResultHolder.fail(stack);
        Vec3 location = hit.getLocation();
        boat.setPos(location.x, location.y, location.z);
        boat.setYRot(player.getYRot());
        if (!level.noCollision(boat, boat.getBoundingBox())) return InteractionResultHolder.fail(stack);
        if (level instanceof ServerLevel serverLevel) {
            EntityType.<Boat>createDefaultStackConfig(serverLevel, stack, player).accept(boat);
            level.addFreshEntity(boat);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, location);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
