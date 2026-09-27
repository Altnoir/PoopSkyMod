package com.altnoir.poopsky.init;

import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.entity.p.PoopTntEntity;
import com.altnoir.poopsky.content.item.p.JinKeLaItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public final class PoDispenserBehaviors {
    private PoDispenserBehaviors() {
    }

    public static void register() {
        DispenserBlock.registerProjectileBehavior(PoItems.POOP_BALL);
        DispenserBlock.registerProjectileBehavior(PoItems.SEA_POOP_BALL);
        DispenserBlock.registerProjectileBehavior(PoItems.WITHER_POOP_BALL);
        DispenserBlock.registerProjectileBehavior(PoItems.GACHAPON);
        DispenserBlock.registerBehavior(PoBlocks.SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(PoBlocks.CHILI_SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(PoBlocks.GOLDEN_SHIT.asItem(), ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(PoItems.POOP.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                this.setSuccess(true);
                Level level = blockSource.level();
                BlockPos pos = targetPos(blockSource);

                if (!BoneMealItem.applyBonemeal(item, level, pos, null) && !BoneMealItem.growWaterPlant(item, level, pos, null)) {
                    this.setSuccess(false);
                } else if (!level.isClientSide) {
                    level.levelEvent(1505, pos, 15);
                }
                return item;
            }
        });
        DispenserBlock.registerBehavior(PoItems.JINKELA.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                boolean success = false;
                if (blockSource.level() instanceof ServerLevel serverLevel) {
                    BlockPos pos = targetPos(blockSource);
                    success = JinKeLaItem.tryApplyToBlock(serverLevel, pos, serverLevel.getBlockState(pos));
                }
                this.setSuccess(success);
                if (success) {
                    item.shrink(1);
                }
                return item;
            }
        });
        DispenserBlock.registerBehavior(PoBlocks.POP.asItem(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                Level level = blockSource.level();
                BlockPos pos = targetPos(blockSource);

                PoopTntEntity tnt = new PoopTntEntity(level, pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() + 0.5, null);
                level.addFreshEntity(tnt);
                level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                item.shrink(1);
                return item;
            }
        });
        DispenserBlock.registerBehavior(PoItems.URINE_BUCKET.get(), new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior fallback = new DefaultDispenseItemBehavior();

            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                DispensibleContainerItem container = (DispensibleContainerItem) item.getItem();
                BlockPos pos = targetPos(blockSource);
                Level level = blockSource.level();
                if (container.emptyContents(null, level, pos, null, item)) {
                    container.checkExtraContent(null, level, item, pos);
                    return this.consumeWithRemainder(blockSource, item, new ItemStack(Items.BUCKET));
                }
                return fallback.dispense(blockSource, item);
            }
        });

        DispenseItemBehavior glassBottleBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.GLASS_BOTTLE);
        DispenseItemBehavior urineBottleBehavior = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource blockSource, ItemStack item) {
                BlockPos pos = targetPos(blockSource);
                Level level = blockSource.level();
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 0.6F);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
                return this.consumeWithRemainder(blockSource, item, new ItemStack(PoItems.URINE_BOTTLE.get()));
            }
        };
        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE, (blockSource, item) -> {
            var fluidState = blockSource.level().getFluidState(targetPos(blockSource));
            if (fluidState.isSource() && fluidState.is(PoFluids.URINE.get())) {
                return urineBottleBehavior.dispense(blockSource, item);
            }
            return glassBottleBehavior.dispense(blockSource, item);
        });

        AbstractToiletBlock.dispenserToiletExplosion(Items.FLINT_AND_STEEL, (toilet, level, pos, stack) ->
                stack.hurtAndBreak(1, level, null, player -> {
                }));
        AbstractToiletBlock.dispenserToiletExplosion(Items.FIRE_CHARGE, (toilet, level, pos, stack) -> stack.shrink(1));
    }

    private static BlockPos targetPos(BlockSource blockSource) {
        Direction facing = blockSource.state().getValue(DispenserBlock.FACING);
        return blockSource.pos().relative(facing);
    }
}
