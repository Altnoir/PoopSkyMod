package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.content.block.entity.GlassJarBlockEntity;
import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassJarBlockItem extends BlockItem {
    public GlassJarBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    private static @Nullable BlockHitResult getPickupHit(Level level, Player player, FluidStack stored) {
        if (stored.getAmount() > GlassJarBlockEntity.CAPACITY - FluidType.BUCKET_VOLUME) {
            return null;
        }
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos pos = hit.getBlockPos();
        if (hit.getType() == HitResult.Type.MISS || !(level.getBlockState(pos).getBlock() instanceof BucketPickup)) {
            return null;
        }
        var source = level.getFluidState(pos);
        if (!source.isSource()) {
            return null;
        }
        var pickedUp = new FluidStack(source.getType(), FluidType.BUCKET_VOLUME);
        return stored.isEmpty() || FluidStack.isSameFluidSameComponents(stored, pickedUp) ? hit : null;
    }

    private static boolean canUseAt(Level level, Player player, ItemStack stack, BlockHitResult hit, BlockPos destination) {
        return level.isInWorldBounds(destination) && level.getWorldBorder().isWithinBounds(destination)
                && level.mayInteract(player, hit.getBlockPos()) && player.mayUseItemAt(destination, hit.getDirection(), stack);
    }

    private static ItemStack storePickup(ItemStack stack, Player player, InteractionHand hand, FluidStack stored,
                                         FluidStack filled, HolderLookup.Provider registries) {
        ItemStack result = withFluid(stack, filled, registries);
        if (!player.hasInfiniteMaterials() || stored.isEmpty()) {
            return ItemUtils.createFilledResult(stack, player, result);
        }
        if (stack.getCount() > 1) {
            player.setItemInHand(hand, result);
            player.getInventory().placeItemBackInInventory(stack.copyWithCount(stack.getCount() - 1));
        }
        return result;
    }

    private static void playTransferSound(Level level, Player player, BlockPos pos, FluidStack fluid, boolean pickup) {
        var action = pickup ? SoundActions.BUCKET_FILL : SoundActions.BUCKET_EMPTY;
        var sound = fluid.getFluidType().getSound(player, level, pos, action);
        if (sound == null) {
            boolean lava = fluid.getFluid().is(FluidTags.LAVA);
            sound = pickup ? (lava ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL)
                    : (lava ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY);
        }
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, pickup ? GameEvent.FLUID_PICKUP : GameEvent.FLUID_PLACE, pos);
    }

    private static ItemStack withFluid(ItemStack stack, FluidStack fluid, HolderLookup.Provider registries) {
        ItemStack result = stack.copyWithCount(1);
        if (fluid.isEmpty()) {
            result.remove(DataComponents.BLOCK_ENTITY_DATA);
        } else {
            CompoundTag tank = new CompoundTag();
            tank.put("Fluid", fluid.save(registries));
            CompoundTag contents = new CompoundTag();
            contents.put("tank", tank);
            setBlockEntityData(result, PoBlockEntityType.GLASS_JAR.get(), contents);
        }
        return result;
    }

    public static FluidStack getStoredFluid(ItemStack stack, HolderLookup.Provider registries) {
        return FluidStack.parseOptional(registries, getStoredFluidTag(stack));
    }

    private static CompoundTag getStoredFluidTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)
                .copyTag().getCompound("tank").getCompound("Fluid");
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        var handler = FluidUtil.getFluidHandler(level, pos, context.getClickedFace())
                .orElseGet(() -> AbstractCompooperBlock.bucketFluidHandler(level, pos));
        if (handler == null) {
            return InteractionResult.PASS;
        }
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        var registries = level.registryAccess();
        FluidStack stored = getStoredFluid(stack, registries);
        var jar = new FluidTank(GlassJarBlockEntity.CAPACITY);
        jar.setFluid(stored.copy());

        FluidStack poured = FluidUtil.tryFluidTransfer(handler, jar, FluidType.BUCKET_VOLUME, true);
        if (!poured.isEmpty()) {
            if (!player.hasInfiniteMaterials()) {
                player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(stack, player, withFluid(stack, jar.getFluid(), registries)));
            }
            playTransferSound(level, player, pos, poured, false);
            return InteractionResult.CONSUME;
        }

        int space = Math.min(jar.getSpace(), FluidType.BUCKET_VOLUME);
        if (space > 0) {
            FluidStack pickedUp = stored.isEmpty()
                    ? FluidUtil.tryFluidTransfer(jar, handler, space, true)
                    : FluidUtil.tryFluidTransfer(jar, handler, stored.copyWithAmount(space), true);
            if (!pickedUp.isEmpty()) {
                player.setItemInHand(context.getHand(), storePickup(stack, player, context.getHand(), stored, jar.getFluid(), registries));
                playTransferSound(level, player, pos, pickedUp, true);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }

        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        if (!getStoredFluidTag(stack).isEmpty()
                || getPickupHit(level, player, FluidStack.EMPTY) != null) {
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            return InteractionResultHolder.pass(stack);
        }

        FluidStack stored = getStoredFluid(stack, level.registryAccess());
        BlockHitResult pickupHit = getPickupHit(level, player, stored);
        if (pickupHit != null) {
            return pickUpFluid(level, player, hand, stack, stored, pickupHit);
        }
        if (stored.getAmount() < FluidType.BUCKET_VOLUME) {
            return InteractionResultHolder.pass(stack);
        }
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        }
        return pourFluid(level, player, stack, stored, hit);
    }

    private InteractionResultHolder<ItemStack> pickUpFluid(Level level, Player player, InteractionHand hand, ItemStack stack,
                                                           FluidStack stored, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        if (!canUseAt(level, player, stack, hit, pos.relative(hit.getDirection()))) {
            return InteractionResultHolder.fail(stack);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        var state = level.getBlockState(pos);
        var pickup = (BucketPickup) state.getBlock();
        FluidStack filled = new FluidStack(level.getFluidState(pos).getType(), stored.getAmount() + FluidType.BUCKET_VOLUME);
        if (pickup.pickupBlock(player, level, pos, state).isEmpty()) {
            return InteractionResultHolder.fail(stack);
        }
        level.playSound(null, pos, pickup.getPickupSound(state).orElse(SoundEvents.BUCKET_FILL), SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(storePickup(stack, player, hand, stored, filled, level.registryAccess()));
    }

    private InteractionResultHolder<ItemStack> pourFluid(Level level, Player player, ItemStack stack, FluidStack stored,
                                                         BlockHitResult hit) {
        var fluid = stored.getFluid();
        BlockPos pos = hit.getBlockPos();
        var state = level.getBlockState(pos);
        var container = state.getBlock() instanceof LiquidBlockContainer liquidContainer
                && state.getFluidState().isEmpty() && liquidContainer.canPlaceLiquid(player, level, pos, state, fluid)
                ? liquidContainer : null;
        BlockPos destination = container != null ? pos : pos.relative(hit.getDirection());
        if (!canUseAt(level, player, stack, hit, destination)) {
            return InteractionResultHolder.fail(stack);
        }
        var fluidType = stored.getFluidType();
        FluidStack dose = stored.copyWithAmount(FluidType.BUCKET_VOLUME);
        var fluidState = fluidType.getStateForPlacement(level, destination, dose);
        var fluidBlock = fluidType.getBlockForFluidState(level, destination, fluidState);
        if (fluidBlock.isAir() || (container == null && !level.getBlockState(destination).canBeReplaced(fluid))) {
            return InteractionResultHolder.fail(stack);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (fluidType.isVaporizedOnPlacement(level, destination, dose)) {
            fluidType.onVaporize(player, level, destination, dose);
        } else {
            boolean placed = container != null
                    ? container.placeLiquid(level, destination, state, fluidState)
                    : level.setBlock(destination, fluidBlock, Block.UPDATE_ALL_IMMEDIATE);
            if (!placed) {
                return InteractionResultHolder.fail(stack);
            }
            playTransferSound(level, player, destination, dose, false);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (player.hasInfiniteMaterials()) {
            return InteractionResultHolder.consume(stack);
        }
        FluidStack remaining = stored.copyWithAmount(stored.getAmount() - FluidType.BUCKET_VOLUME);
        return InteractionResultHolder.consume(ItemUtils.createFilledResult(stack, player, withFluid(stack, remaining, level.registryAccess())));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        }
        FluidStack fluid = getStoredFluid(stack, registries);
        MutableComponent contents = fluid.isEmpty()
                ? Component.translatable("tooltip.poopsky.glass_jar.empty")
                : Component.translatable("tooltip.poopsky.glass_jar.fluid_amount", fluid.getHoverName(), fluid.getAmount());
        tooltip.add(contents.withStyle(ChatFormatting.GRAY));
    }
}
