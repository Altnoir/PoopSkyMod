package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PoBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IgneousExtruderBlockEntity extends BlockEntity {
    private static final int TICKS_PER_OPERATION = 20;

    private int progress;

    public IgneousExtruderBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.IGNEOUS_EXTRUDER.get(), pos, state);
    }

    public static void tick(Level level, IgneousExtruderBlockEntity blockEntity) {
        Block result = blockEntity.findResult(level);
        if (result == null) {
            blockEntity.progress = 0;
            return;
        }

        if (++blockEntity.progress < TICKS_PER_OPERATION) {
            return;
        }
        blockEntity.progress = 0;
        blockEntity.output(level, new ItemStack(result));
    }

    private void output(Level level, ItemStack stack) {
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.above(), Direction.DOWN);
        if (handler != null) {
            for (int slot = 0; slot < handler.getSlots() && !stack.isEmpty(); slot++) {
                stack = handler.insertItem(slot, stack, false);
            }
        }
        if (!stack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.0D, worldPosition.getZ() + 0.5D, stack);
        }
    }

    private @Nullable Block findResult(Level level) {
        for (Direction firstDirection : Direction.Plane.HORIZONTAL) {
            FluidState first = level.getFluidState(worldPosition.relative(firstDirection));
            if (first.isEmpty()) continue;
            for (Direction secondDirection : Direction.Plane.HORIZONTAL) {
                if (firstDirection == secondDirection) continue;
                Block result = resolve(level, worldPosition, first, worldPosition.relative(secondDirection));
                if (result != null) return result;
            }
        }
        return null;
    }

    private static @Nullable Block resolve(Level level, BlockPos pos, FluidState source, BlockPos neighbourPos) {
        for (FluidInteractionRegistry.InteractionInformation interaction : interactions(source.getFluidType())) {
            if (!interaction.predicate().test(level, pos, neighbourPos, source)) continue;
            BlockState result = getResultState(interaction, source);
            if (result != null) {
                result = EventHooks.fireFluidPlaceBlockEvent(level, pos, pos, result);
            }
            if (result != null) return result.getBlock();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<FluidInteractionRegistry.InteractionInformation> interactions(FluidType fluidType) {
        try {
            Field field = FluidInteractionRegistry.class.getDeclaredField("INTERACTIONS");
            field.setAccessible(true);
            return ((Map<FluidType, List<FluidInteractionRegistry.InteractionInformation>>) field.get(null))
                    .getOrDefault(fluidType, List.of());
        } catch (ReflectiveOperationException exception) {
            PoopSky.LOGGER.error("Unable to read registered fluid interactions for igneous extruder", exception);
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private static @Nullable BlockState getResultState(FluidInteractionRegistry.InteractionInformation interaction, FluidState source) {
        for (Field field : interaction.interaction().getClass().getDeclaredFields()) {
            if (!Function.class.isAssignableFrom(field.getType())) continue;
            try {
                field.setAccessible(true);
                Object result = ((Function<FluidState, ?>) field.get(interaction.interaction())).apply(source);
                if (result instanceof BlockState state) return state;
            } catch (ReflectiveOperationException exception) {
                PoopSky.LOGGER.error("Unable to resolve a registered fluid interaction for igneous extruder", exception);
            }
        }
        return null;
    }
}
