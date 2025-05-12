package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.item.PSItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBehavior.class)
public interface DispenserBehaviorMixin {
    @Inject(method = "registerDefaults()V", at = @At("TAIL"))
    private static void poopsky$registerPoopBallBehavior(CallbackInfo ci) {
        DispenserBlock.registerProjectileBehavior(PSItems.POOP_BALL);
        DispenserBlock.registerBehavior(PSItems.POOP, new FallibleItemDispenserBehavior() {
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(true);
                World world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                if (!BoneMealItem.useOnFertilizable(stack, world, blockPos) && !BoneMealItem.useOnGround(stack, world, blockPos, null)) {
                    this.setSuccess(false);
                } else if (!world.isClient) {
                    world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, blockPos, 15);
                }
                return stack;
            }
        });
    }
}