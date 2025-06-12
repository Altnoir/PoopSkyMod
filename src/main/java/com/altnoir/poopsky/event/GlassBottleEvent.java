package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.tag.PSBlockTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = PoopSky.MOD_ID)
public class GlassBottleEvent {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var level = event.getLevel();
        var player = event.getEntity();
        var pos = event.getPos();
        var hand = event.getHand();
        var heldItem = player.getItemInHand(hand);

        if (!(heldItem.getItem() instanceof BottleItem)) return;
        if (!level.getBlockState(pos).is(PSBlockTags.TOILET_BLOCKS)) return;

        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);

            var urineBottle = new ItemStack(PSItems.URINE_BOTTLE.get());
            var result = ItemUtils.createFilledResult(heldItem, player, urineBottle);

            player.setItemInHand(hand, result);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
