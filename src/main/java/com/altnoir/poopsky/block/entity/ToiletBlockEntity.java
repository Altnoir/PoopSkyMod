package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.block.ToiletType;
import com.altnoir.poopsky.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.block.p.LavaToiletBlock;
import com.altnoir.poopsky.block.p.ToiletBlock;
import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.init.PFluids;
import com.altnoir.poopsky.init.PToiletTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class ToiletBlockEntity extends BlockEntity {
    private BlockPos linkedPos;
    private String linkedDim;
    private ToiletType toiletType;

    public static final ModelProperty<ToiletType> TOILET_TYPE_PROPERTY = new ModelProperty<>();

    public final FluidTank fluidTank = new FluidTank(8888000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public ToiletBlockEntity(BlockPos pos, BlockState state) {
        super(PBlockEntityType.TOILET_BLOCK_ENTITY.get(), pos, state);
        this.toiletType = inferDefaultType(state);
    }

    private static ToiletType inferDefaultType(BlockState state) {
        if (state.getBlock() instanceof LavaToiletBlock) {
            return PToiletTypes.COBBLESTONE;
        } else if (state.getBlock() instanceof ToiletBlock) {
            return PToiletTypes.OAK;
        }
        return PToiletTypes.COBBLESTONE;
    }

    public ToiletType getToiletType() {
        return toiletType;
    }

    public void setToiletType(ToiletType toiletType) {
        this.toiletType = toiletType;
        this.setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            requestModelDataUpdate();
            syncToiletModeToBlockState();
        }
    }

    private void syncToiletModeToBlockState() {
        if (level == null) return;
        BlockState state = getBlockState();
        boolean shouldBeRedstone = toiletType == PToiletTypes.REDSTONE;
        if (state.getBlock() instanceof LavaToiletBlock) {
            LavaToiletBlock.ToiletMode currentMode = state.getValue(LavaToiletBlock.TOILET_MODE);
            LavaToiletBlock.ToiletMode targetMode = shouldBeRedstone ? LavaToiletBlock.ToiletMode.REDSTONE : LavaToiletBlock.ToiletMode.DEFAULT;
            if (currentMode != targetMode) {
                level.setBlock(getBlockPos(), state.setValue(LavaToiletBlock.TOILET_MODE, targetMode), 3);
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

        var targetDimension = ResourceLocation.tryParse(linkedDim);
        if (targetDimension == null) return;

        var server = ((ServerLevel) level).getServer();
        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;

        var chunkPos = new ChunkPos(this.getLinkedPos());

        targetWorld.getChunkSource().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

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
        this.linkedDim = serverLevel.dimension().location().toString();
        this.setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.fluidTank.readFromNBT(registries, tag);
        if (tag.contains("LinkedPos")) {
            this.linkedPos = BlockPos.of(tag.getLong("LinkedPos"));
            this.linkedDim = tag.getString("LinkedDim");
        }
        if (tag.contains("ToiletType")) {
            String id = tag.getString("ToiletType");
            ToiletType type = ToiletType.byId(id);
            if (type != null) {
                this.toiletType = type;
            }
        }
        if (level != null && level.isClientSide) {
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.fluidTank.writeToNBT(registries, tag);
        if (linkedPos != null && linkedDim != null) {
            tag.putLong("LinkedPos", linkedPos.asLong());
            tag.putString("LinkedDim", linkedDim);
        }
        if (toiletType != null) {
            tag.putString("ToiletType", toiletType.id());
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (tag.contains("LinkedPos")) {
            this.linkedPos = BlockPos.of(tag.getLong("LinkedPos"));
            this.linkedDim = tag.getString("LinkedDim");
        }
        if (tag.contains("ToiletType")) {
            String id = tag.getString("ToiletType");
            ToiletType type = ToiletType.byId(id);
            if (type != null) {
                this.toiletType = type;
            }
        }
        if (level != null && level.isClientSide) {
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public ModelData getModelData() {
        if (toiletType != null) {
            return ModelData.builder().with(TOILET_TYPE_PROPERTY, toiletType).build();
        }
        return ModelData.EMPTY;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        saveCustomAndMetadata(registries);
        return tag;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ToiletBlockEntity blockEntity) {
        var fluid = PFluids.URINE.get();
        if (state.hasProperty(BaseToiletLavaBlock.LAVA) && state.getValue(BaseToiletLavaBlock.LAVA)) {
            fluid = Fluids.LAVA;
        }
        var fluidTank = blockEntity.fluidTank.getFluid().getFluid();
        if (fluidTank != fluid) {
            blockEntity.fluidTank.drain(blockEntity.fluidTank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        blockEntity.fluidTank.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
    }
}