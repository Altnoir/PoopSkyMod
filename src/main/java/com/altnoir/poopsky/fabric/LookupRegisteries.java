package com.altnoir.poopsky.fabric;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.fabric.port.fluidhandler.IFluidHandler;
import com.altnoir.poopsky.fabric.port.itemhandler.IItemHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class LookupRegisteries {
    public static final BlockApiLookup<IItemHandler, Direction> SIEVE = itemHandler(PoopSky.loc("sieve_lookup"));
    public static final BlockApiLookup<IFluidHandler, Direction> TOILET = fluidHandler(PoopSky.loc("toilet_lookup"));
    public static final BlockApiLookup<IItemHandler, Direction> FLY_BARREL = itemHandler(PoopSky.loc("fly_barrel_lookup"));
    public static final BlockApiLookup<IItemHandler, Direction> BREEDING_CHEST = itemHandler(PoopSky.loc("breeding_chest_lookup"));

    public static BlockApiLookup<IItemHandler, Direction> itemHandler(ResourceLocation lookupId) {
        return BlockApiLookup.get(lookupId, IItemHandler.class, Direction.class);
    }

    public static BlockApiLookup<IFluidHandler, Direction> fluidHandler(ResourceLocation lookupId) {
        return BlockApiLookup.get(lookupId, IFluidHandler.class, Direction.class);
    }
}
