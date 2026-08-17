package com.altnoir.poopsky.fabric.port.fluidhandler;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.Nullable;

public class FluidHandlerStorage extends SnapshotParticipant<FluidStack> implements SingleSlotStorage<FluidVariant> {
    private final IFluidHandler handler;
    @Nullable
    private final Runnable onChange;

    public FluidHandlerStorage(IFluidHandler handler, @Nullable Runnable onChange) {
        this.handler = handler;
        this.onChange = onChange;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        FluidStack toInsert = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
        int filled = handler.fill(toInsert, IFluidHandler.FluidAction.SIMULATE);
        if (filled > 0) {
            updateSnapshots(transaction);
            handler.fill(toInsert, IFluidHandler.FluidAction.EXECUTE);
        }
        return filled;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        FluidStack toDrain = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
        FluidStack drained = handler.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);
        if (!drained.isEmpty()) {
            updateSnapshots(transaction);
            handler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
        }
        return drained.getAmount();
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public boolean isResourceBlank() {
        return handler.getFluidInTank(0).isEmpty();
    }

    @Override
    public FluidVariant getResource() {
        FluidStack stack = handler.getFluidInTank(0);
        if (stack.isEmpty()) {
            return FluidVariant.blank();
        }
        return FluidVariant.of(stack.getFluid(), stack.getComponentsPatch());
    }

    @Override
    public long getAmount() {
        return handler.getFluidInTank(0).getAmount();
    }

    @Override
    public long getCapacity() {
        return handler.getTankCapacity(0);
    }

    @Override
    public StorageView<FluidVariant> getUnderlyingView() {
        return this;
    }

    @Override
    protected FluidStack createSnapshot() {
        return handler.getFluidInTank(0).copy();
    }

    @Override
    protected void readSnapshot(FluidStack snapshot) {
        int currentAmount = handler.getFluidInTank(0).getAmount();
        if (currentAmount > 0) {
            handler.drain(currentAmount, IFluidHandler.FluidAction.EXECUTE);
        }
        if (!snapshot.isEmpty()) {
            handler.fill(snapshot.copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    protected void onFinalCommit() {
        if (onChange != null) {
            onChange.run();
        }
    }
}
