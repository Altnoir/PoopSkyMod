package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoFluids;
import com.altnoir.poopsky.init.ToiletTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class ToiletBlockEntity extends BlockEntity {
    private BlockPos linkedPos;
    private String linkedDim;
    private ToiletType toiletType;

    public final FluidStacksResourceHandler fluidTank = new FluidStacksResourceHandler(1, 8888000) {
        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public ToiletBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.TOILET_BLOCK_ENTITY.get(), pos, state);
        this.toiletType = inferDefaultType(state);
    }

    private static ToiletType inferDefaultType(BlockState state) {
        if (state.getBlock() instanceof AbstractToiletBlock toiletBlock) {
            return toiletBlock.getDefaultToiletType();
        }
        return ToiletTypes.COBBLESTONE;
    }

    public ToiletType getToiletType() {
        return toiletType;
    }

    public void setToiletType(ToiletType toiletType) {
        this.toiletType = toiletType;
        this.setChanged();
        if (level instanceof ServerLevel serverLevel) {
            syncToiletTypeToBlockState();
            requestModelDataUpdate();
            serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private void syncToiletTypeToBlockState() {
        if (level == null) return;
        BlockState state = getBlockState();
        if (state.getBlock() instanceof AbstractToiletBlock toiletBlock) {
            BlockState updatedState = toiletBlock.applyToiletType(state, toiletType);
            if (updatedState != state) {
                level.setBlock(getBlockPos(), updatedState, 3);
            }
        }
    }

    public String getLinkedDim() {
        return linkedDim;
    }

    public BlockPos getLinkedPos() {
        return linkedPos;
    }

    public void clearLinkedBlock() {
        if (level == null || level.isClientSide()) return;
        if (linkedPos == null || linkedDim == null || linkedDim.isBlank()) return;

        var targetDimension = Identifier.tryParse(linkedDim);
        if (targetDimension == null) return;

        var server = ((ServerLevel) level).getServer();
        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;

        var chunkPos = ChunkPos.containing(this.linkedPos);

        targetWorld.getChunkSource().getChunk(chunkPos.x(), chunkPos.z(), ChunkStatus.FULL, true);

        if (targetWorld.getBlockEntity(linkedPos) instanceof ToiletBlockEntity be) {
            be.setLinkedPos(BlockPos.ZERO, "");
        }
    }

    public void setLinkedPos(BlockPos pos, String dim) {
        this.linkedPos = pos;
        this.linkedDim = dim;
        this.setChanged();
    }

    public void setLinkedPos(BlockPos pos, ServerLevel serverLevel) {
        this.linkedPos = pos;
        this.linkedDim = serverLevel.dimension().identifier().toString();
        this.setChanged();
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("fluid").ifPresent(this.fluidTank::deserialize);
        if (input.getLong("LinkedPos").isPresent()) {
            this.linkedPos = BlockPos.of(input.getLongOr("LinkedPos", BlockPos.ZERO.asLong()));
            this.linkedDim = input.getStringOr("LinkedDim", "");
        }
        if (input.getString("ToiletType").isPresent()) {
            String id = input.getStringOr("ToiletType", "");
            ToiletType type = ToiletType.byId(id);
            if (type != null) {
                this.toiletType = type;
            }
        }
        if (level != null && level.isClientSide()) {
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.fluidTank.serialize(output.child("fluid"));
        if (linkedPos != null && linkedDim != null) {
            output.putLong("LinkedPos", linkedPos.asLong());
            output.putString("LinkedDim", linkedDim);
        }
        if (toiletType != null) {
            output.putString("ToiletType", toiletType.id());
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    public FluidStack getFluid() {
        FluidResource resource = fluidTank.getResource(0);
        long amount = fluidTank.getAmountAsLong(0);
        return resource.isEmpty() || amount <= 0 ? FluidStack.EMPTY : resource.toStack((int) amount);
    }

    public int getFluidAmount() {
        return (int) fluidTank.getAmountAsLong(0);
    }

    public int fill(FluidStack stack, boolean simulate) {
        if (stack.isEmpty()) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            int filled = fluidTank.insert(0, FluidResource.of(stack), stack.getAmount(), tx);
            if (!simulate) tx.commit();
            return filled;
        }
    }

    public int drain(int amount, boolean simulate) {
        FluidResource resource = fluidTank.getResource(0);
        if (resource.isEmpty() || amount <= 0) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            int drained = fluidTank.extract(0, resource, amount, tx);
            if (!simulate) tx.commit();
            return drained;
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ToiletBlockEntity blockEntity) {
        var fluid = state.getBlock() instanceof AbstractToiletBlock toilet && toilet.isLavaFilled(state)
                ? Fluids.LAVA
                : PoFluids.URINE.get();
        if (blockEntity.getFluid().getFluid() != fluid) {
            blockEntity.drain(blockEntity.getFluidAmount(), false);
        }
        blockEntity.fill(new FluidStack(fluid, Integer.MAX_VALUE), false);
    }
}
