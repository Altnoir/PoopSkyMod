package com.altnoir.poopsky.mixin;

import com.altnoir.poopsky.tag.PSBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectMossConversion(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        ItemStack stack = context.getStack();
        PotionContentsComponent potion = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

        if (context.getSide() != Direction.DOWN && blockState.isIn(PSBlockTags.CONVERTABLE_TO_MOSS) && potion.matches(Potions.WATER)) {
            convertToMossBlock(context, world, blockPos, stack);
            cir.setReturnValue(ActionResult.success(world.isClient()));
        }
    }

    @Unique
    private void convertToMossBlock(ItemUsageContext context, World world, BlockPos pos, ItemStack stack) {
        PlayerEntity player = context.getPlayer();

        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (player != null) {
            player.setStackInHand(context.getHand(),
                    ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }

        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;
            for (int i = 0; i < 5; i++) {
                serverWorld.spawnParticles(ParticleTypes.SPLASH,
                        pos.getX() + world.random.nextDouble(),
                        pos.getY() + 1.0,
                        pos.getZ() + world.random.nextDouble(),
                        1,
                        0.0, 0.0, 0.0, 1.0
                );
            }
        }

        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.setBlockState(pos, Blocks.MOSS_BLOCK.getDefaultState());
        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
    }
}
