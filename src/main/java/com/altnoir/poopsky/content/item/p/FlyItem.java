package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class FlyItem extends Item {
    public FlyItem(Properties properties) {
        super(properties);
    }

    public static ItemStack withType(FlyType.Type type) {
        var stack = new ItemStack(PoItems.FLY.get());
        stack.set(PoComponents.FLY_TYPE.get(), type.id());
        return stack;
    }

    public static ItemStack withType(String typeId) {
        var stack = new ItemStack(PoItems.FLY.get());
        stack.set(PoComponents.FLY_TYPE.get(), typeId);
        return stack;
    }

    public static ItemStackTemplate templateWithType(FlyType.Type type) {
        return new ItemStackTemplate(PoItems.FLY.get(), DataComponentPatch.builder()
                .set(PoComponents.FLY_TYPE.get(), type.id())
                .build());
    }

    public static FlyType.Type getFlyType(ItemStack stack) {
        String typeId = stack.get(PoComponents.FLY_TYPE.get());
        return typeId != null ? FlyType.byId(typeId) : FlyTypes.NORMAL.get();
    }

    public static boolean isFlyItem(ItemStack stack) {
        return stack.has(PoComponents.FLY_TYPE.get());
    }

    public static boolean canSpawnDefaultFly(ItemStack stack) {
        String typeId = stack.get(PoComponents.FLY_TYPE.get());
        return typeId == null || typeId.equals(FlyTypes.NORMAL.id());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (!canSpawnDefaultFly(stack)) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockState state = level.getBlockState(clickedPos);
        if (level.getBlockEntity(clickedPos) instanceof Spawner spawner) {
            spawner.setEntityId(PoEntityType.FLY.get(), level.getRandom());
            level.sendBlockUpdated(clickedPos, state, state, 3);
            level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, clickedPos);
            stack.shrink(1);
            return InteractionResult.CONSUME;
        }

        BlockPos spawnPos = state.getCollisionShape(level, clickedPos).isEmpty()
                ? clickedPos
                : clickedPos.relative(direction);
        if (context.getPlayer() != null) {
            spawnDefaultFly(serverLevel, stack, context.getPlayer(), spawnPos, true,
                    !Objects.equals(clickedPos, spawnPos) && direction == Direction.UP
            );
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!canSpawnDefaultFly(stack)) {
            return InteractionResult.PASS;
        }

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = hitResult.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof LiquidBlock)) {
            return InteractionResult.PASS;
        }
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.FAIL;
        }

        if (!spawnDefaultFly(serverLevel, stack, player, pos, false, false)) {
            return InteractionResult.PASS;
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS_SERVER.heldItemTransformedTo(stack);
    }

    private static boolean spawnDefaultFly(ServerLevel level, ItemStack stack, Player player, BlockPos pos,
                                           boolean randomizeYaw, boolean shouldOffsetYMore) {
        EntityType<?> entityType = PoEntityType.FLY.get();
        Entity entity = entityType.spawn(level, stack, player, pos,
                EntitySpawnReason.SPAWN_ITEM_USE,
                randomizeYaw,
                shouldOffsetYMore
        );
        if (entity == null) {
            return false;
        }

        stack.consume(1, player);
        level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, tooltipFlag);
        FlyType.Type type = getFlyType(stack);
        consumer.accept(Component.translatable("tooltip.poopsky.fly_type")
                .append(": ")
                .append(type.getDisplayName())
                .withStyle(ChatFormatting.GRAY));
    }
}
