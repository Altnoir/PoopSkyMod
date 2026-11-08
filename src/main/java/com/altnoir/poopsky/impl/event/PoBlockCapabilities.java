package com.altnoir.poopsky.impl.event;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/** 为管道和漏斗提供与原版相同的分面输入输出，失效时通知缓存。 */
public final class PoBlockCapabilities {
    private PoBlockCapabilities() {}
    public static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity block = event.getObject();
        Function<Direction, IItemHandler> items = null;
        java.util.function.Supplier<IFluidHandler> fluid = null;
        if (block instanceof SieveBlockEntity be) items = side -> side == null || side == Direction.DOWN ? be.getBottomHandler() : be.getTopSideHandler();
        else if (block instanceof FlyBarrelBlockEntity be) items = side -> side == null || side == Direction.DOWN ? be.getBottomHandler() : be.getTopSideHandler();
        else if (block instanceof BreedingChestBlockEntity be) items = side -> side == null || side == Direction.DOWN ? be.getBottomHandler() : be.getTopSideHandler();
        else if (block instanceof FlushToiletBlockEntity be) items = side -> be.getBottomHandler();
        if (block instanceof ToiletBlockEntity be) fluid = () -> be.fluidTank;
        if (items == null && fluid == null) return;
        Provider provider = new Provider(items, fluid);
        event.addCapability(PoopSky.loc("automation"), provider);
        event.addListener(provider::invalidate);
    }
    private static final class Provider implements ICapabilityProvider {
        private final Map<Direction, LazyOptional<IItemHandler>> sided = new EnumMap<>(Direction.class);
        private final LazyOptional<IItemHandler> unsided;
        private final LazyOptional<IFluidHandler> fluid;
        Provider(Function<Direction, IItemHandler> items, java.util.function.Supplier<IFluidHandler> fluidHandler) {
            unsided = items == null ? LazyOptional.empty() : LazyOptional.of(() -> items.apply(null));
            if (items != null) for (Direction side : Direction.values()) sided.put(side, LazyOptional.of(() -> items.apply(side)));
            fluid = fluidHandler == null ? LazyOptional.empty() : LazyOptional.of(fluidHandler::get);
        }
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
            if (capability == ForgeCapabilities.ITEM_HANDLER) return (side == null ? unsided : sided.getOrDefault(side, LazyOptional.empty())).cast();
            if (capability == ForgeCapabilities.FLUID_HANDLER) return fluid.cast();
            return LazyOptional.empty();
        }
        void invalidate() {
            unsided.invalidate();
            fluid.invalidate();
            sided.values().forEach(LazyOptional::invalidate);
        }
    }
}
