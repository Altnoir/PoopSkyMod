package com.altnoir.poopsky.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.block.AbstractToiletBlock;
import com.altnoir.poopsky.block.p.ToiletLavaBlock;
import com.altnoir.poopsky.fluid.PSFluids;
import com.altnoir.poopsky.item.PSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
        if (!level.isClientSide && level.getBlockState(pos).getBlock() instanceof AbstractToiletBlock abstractToiletBlock) {
            if (abstractToiletBlock instanceof ToiletLavaBlock && level.getBlockState(pos).getValue(ToiletLavaBlock.LAVA)) return;

            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);

            var result = ItemUtils.createFilledResult(heldItem, player, new ItemStack(PSItems.URINE_BOTTLE.get()));

            player.setItemInHand(hand, result);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        Level level = event.getLevel();

        if (!stack.isEmpty() && stack.getItem() instanceof BottleItem) {

            BlockHitResult blockhitresult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos blockpos = blockhitresult.getBlockPos();

            if (blockhitresult.getType() == HitResult.Type.BLOCK && level.mayInteract(player, blockpos) && level.getFluidState(blockpos).is(PSFluids.POOP.get())) {
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);

                ItemStack itemStack = ItemUtils.createFilledResult(stack, player, new ItemStack(PSItems.URINE_BOTTLE.get()));
                player.setItemInHand(event.getHand(), itemStack);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
