/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.altnoir.poopsky.fabric.port.fluidhandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.*;
import java.util.function.Function;

public final class FluidInteractionRegistry {
    private static final Map<Fluid, List<InteractionInformation>> INTERACTIONS = new HashMap<>();

    private FluidInteractionRegistry() {
    }

    public static synchronized void addInteraction(Fluid source, InteractionInformation interaction) {
        INTERACTIONS.computeIfAbsent(fluidFamily(source), ignored -> new ArrayList<>()).add(interaction);
    }

    public static boolean canInteract(Level level, BlockPos pos) {
        FluidState state = level.getFluidState(pos);
        List<InteractionInformation> interactions = INTERACTIONS.getOrDefault(
                fluidFamily(state.getType()), Collections.emptyList());

        for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            BlockPos relativePos = pos.relative(direction.getOpposite());
            for (InteractionInformation interaction : interactions) {
                if (interaction.predicate().test(level, pos, relativePos, state)) {
                    interaction.interaction().interact(level, pos, relativePos, state);
                    return true;
                }
            }
        }

        return false;
    }

    private static Fluid fluidFamily(Fluid fluid) {
        return fluid instanceof FlowingFluid flowingFluid ? flowingFluid.getSource() : fluid;
    }

    static {
        addInteraction(Fluids.LAVA, new InteractionInformation(
                Fluids.WATER,
                fluidState -> fluidState.isSource()
                        ? Blocks.OBSIDIAN.defaultBlockState()
                        : Blocks.COBBLESTONE.defaultBlockState()));
        addInteraction(Fluids.LAVA, new InteractionInformation(
                (level, currentPos, relativePos, currentState) ->
                        level.getBlockState(currentPos.below()).is(Blocks.SOUL_SOIL)
                                && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                Blocks.BASALT.defaultBlockState()));
    }

    public record InteractionInformation(HasFluidInteraction predicate, FluidInteraction interaction) {
        public InteractionInformation(Fluid type, BlockState state) {
            this(type, fluidState -> state);
        }

        public InteractionInformation(HasFluidInteraction predicate, BlockState state) {
            this(predicate, fluidState -> state);
        }

        public InteractionInformation(Fluid type, Function<FluidState, BlockState> getState) {
            this((level, currentPos, relativePos, currentState) ->
                    fluidFamily(level.getFluidState(relativePos).getType()) == fluidFamily(type), getState);
        }

        public InteractionInformation(
                HasFluidInteraction predicate,
                Function<FluidState, BlockState> getState
        ) {
            this(predicate, (level, currentPos, relativePos, currentState) -> {
                level.setBlockAndUpdate(currentPos, getState.apply(currentState));
                level.levelEvent(1501, currentPos, 0);
            });
        }
    }

    @FunctionalInterface
    public interface HasFluidInteraction {
        boolean test(Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState);
    }

    @FunctionalInterface
    public interface FluidInteraction {
        void interact(Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState);
    }
}
