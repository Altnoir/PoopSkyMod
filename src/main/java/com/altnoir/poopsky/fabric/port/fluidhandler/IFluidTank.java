package com.altnoir.poopsky.fabric.port.fluidhandler;

import com.altnoir.poopsky.fabric.port.fluidhandler.IFluidHandler.FluidAction;

public interface IFluidTank {
    FluidStack getFluid();

    int getFluidAmount();

    int getCapacity();

    boolean isFluidValid(FluidStack stack);

    int fill(FluidStack resource, FluidAction action);

    FluidStack drain(int maxDrain, FluidAction action);

    FluidStack drain(FluidStack resource, FluidAction action);
}
