package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.tag.PSBlockTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlassBottleItem.class)
public abstract class GlassBottleItemMixin implements GlassBottleItemAccessor{
    @Inject(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;", shift = At.Shift.BEFORE),
            cancellable = true
    )
    private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        BlockHitResult blockHitResult = (BlockHitResult) user.raycast(4.5, 0, false);

        if (blockHitResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = blockHitResult.getBlockPos();
        ItemStack stack = user.getStackInHand(hand);

        if (world.getBlockState(pos).isIn(PSBlockTags.TOILET_BLOCKS)) {
            world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, pos);

            cir.setReturnValue(TypedActionResult.success(
                    this.invokeFill(stack, user, new ItemStack(PSItems.URINE_BOTTLE)),
                    world.isClient()
            ));
        }
    }
}
