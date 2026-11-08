package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.PoLeashable;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PoopSky.MOD_ID)
public final class PoLeashEvents {
    private PoLeashEvents() {}
    @SubscribeEvent
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        if (target instanceof PoLeashable leash) {
            if (leash.getLeashHolder() == event.getEntity()) {
                if (!event.getLevel().isClientSide) leash.dropLeash(true, !event.getEntity().getAbilities().instabuild);
            } else if (event.getItemStack().is(Items.LEAD) && !leash.isLeashed()) {
                if (!event.getLevel().isClientSide) {
                    leash.setLeashedTo(event.getEntity());
                    event.getItemStack().shrink(1);
                }
            } else return;
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        } else if (target instanceof LeashFenceKnotEntity knot) {
            for (ToiletPlugEntity plug : event.getLevel().getEntitiesOfClass(ToiletPlugEntity.class, knot.getBoundingBox().inflate(10))) {
                if (plug.getLeashHolder() == event.getEntity()) {
                    if (!event.getLevel().isClientSide) plug.setLeashedTo(knot);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }
    @SubscribeEvent
    public static void fence(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().getBlockState(event.getPos()).is(BlockTags.FENCES)) return;
        LeashFenceKnotEntity knot = null;
        boolean attached = false;
        for (ToiletPlugEntity plug : event.getLevel().getEntitiesOfClass(ToiletPlugEntity.class, new AABB(event.getPos()).inflate(10))) {
            if (plug.getLeashHolder() != event.getEntity()) continue;
            if (!event.getLevel().isClientSide) {
                if (knot == null) { knot = LeashFenceKnotEntity.getOrCreateKnot(event.getLevel(),event.getPos()); knot.playPlacementSound(); }
                plug.setLeashedTo(knot);
            }
            attached = true;
        }
        if (attached) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
