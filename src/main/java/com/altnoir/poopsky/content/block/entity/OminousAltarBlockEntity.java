package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.AltarRitualAnimation;
import com.altnoir.poopsky.content.recipe.AltarRecipe;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OminousAltarBlockEntity extends BlockEntity {
    public static final List<BlockPos> PEDESTAL_OFFSETS = List.of(
            new BlockPos(0, 0, -3), new BlockPos(2, 0, -2), new BlockPos(3, 0, 0), new BlockPos(2, 0, 2),
            new BlockPos(0, 0, 3), new BlockPos(-2, 0, 2), new BlockPos(-3, 0, 0), new BlockPos(-2, 0, -2)
    );
    private static final int SCAN_BUDGET = 7;
    private static final int SCAN_INTERVAL = 20;
    private static final int INPUT = 0;
    private static final int OUTPUT = 1;
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public int getSlotLimit(int slot) {
            return slot == INPUT ? 1 : 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot == INPUT) inputVersion++;
            setChanged();
            sync();
        }
    };
    private final IItemHandler top = new RangedWrapper(inventory, INPUT, INPUT + 1) {
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };
    private final IItemHandler bottom = new RangedWrapper(inventory, OUTPUT, OUTPUT + 1) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };
    private final IItemHandler side = new IItemHandler() {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return slot == INPUT ? inventory.insertItem(INPUT, stack, simulate) : stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == OUTPUT ? inventory.extractItem(OUTPUT, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == INPUT;
        }
    };
    private final Map<BlockPos, GlassJarBlockEntity> jars = new LinkedHashMap<>();
    private final Map<BlockPos, GlassJarBlockEntity> supplying = new LinkedHashMap<>();
    private final Map<BlockPos, PedestalBlockEntity> participants = new LinkedHashMap<>();
    private final Map<BlockPos, Long> participantVersions = new LinkedHashMap<>();
    private long inputVersion;
    private int scanIndex;
    private int scanCooldown;
    private @Nullable RecipeHolder<AltarRecipe> active;
    private List<ItemStack> snapshot = List.of();
    private long startInputVersion;
    private long rateRemainder;
    private int consumed;
    private @Nullable CompoundTag pendingRitual;
    private float clientProgress;
    private float clientProgressStep;
    private float lastSyncedProgress;
    private long lastProgressSyncTick;
    private List<BlockPos> clientJars = List.of();
    private List<BlockPos> clientPedestals = List.of();

    public OminousAltarBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.OMINOUS_ALTAR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OminousAltarBlockEntity altar) {
        if (level.isClientSide) altar.tickClient();
        else altar.tickServer();
    }

    public IItemHandler getHandler(@Nullable Direction direction) {
        if (direction == Direction.UP) return top;
        if (direction == Direction.DOWN) return bottom;
        return side;
    }

    public ItemStack getInput() {
        return inventory.getStackInSlot(INPUT);
    }

    public ItemStack getOutput() {
        return inventory.getStackInSlot(OUTPUT);
    }

    public float getClientProgress() {
        return clientProgress;
    }

    public float getClientProgress(float partialTick) {
        return Math.min(1.0F, clientProgress + clientProgressStep * partialTick);
    }

    public List<BlockPos> getClientJars() {
        return clientJars;
    }

    public boolean isClientPedestal(BlockPos pos) {
        return clientPedestals.contains(pos);
    }

    public boolean insertOne(ItemStack stack) {
        return inventory.insertItem(INPUT, stack.copyWithCount(1), false).isEmpty();
    }

    public ItemStack removeInput() {
        return inventory.extractItem(INPUT, 1, false);
    }

    public ItemStack takeItem() {
        return !getOutput().isEmpty() ? inventory.extractItem(OUTPUT, 64, false) : inventory.extractItem(INPUT, 1, false);
    }

    public void dropContents() {
        if (level == null || level.isClientSide) return;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.extractItem(slot, Integer.MAX_VALUE, false);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.8D,
                        worldPosition.getZ() + 0.5D, stack);
            }
        }
    }

    private void tickClient() {
        clientProgress = Math.min(1.0F, clientProgress + clientProgressStep);
        if (clientProgress <= 0.0F || getInput().isEmpty()) return;

        double density = (double) clientProgress * clientProgress;
        double time = level.getGameTime();
        if (level.random.nextDouble() < 0.04D + 0.14D * density) {
            AltarRitualAnimation.spawnSpark(level,
                    Vec3.atLowerCornerWithOffset(worldPosition, 0.5D, AltarRitualAnimation.altarItemY(time), 0.5D));
        }
        for (BlockPos pedestalPos : clientPedestals) {
            if (level.random.nextDouble() < 0.015D + 0.065D * density
                    && level.hasChunkAt(pedestalPos)
                    && level.getBlockEntity(pedestalPos) instanceof PedestalBlockEntity pedestal
                    && !pedestal.getItem().isEmpty()) {
                Vec3 position = AltarRitualAnimation.orbitPosition(pedestalPos.subtract(worldPosition), time, getClientProgress(0.5F));
                AltarRitualAnimation.spawnSpark(level, position.add(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
            }
        }
    }

    private void tickServer() {
        if (pendingRitual != null && !restoreRitual()) return;
        scanJars();
        if (active != null) {
            if (level.getRecipeManager().byKey(active.id()).orElse(null) != active || !inputsUnchanged()
                    || inputVersion != startInputVersion
                    || participants.entrySet().stream().anyMatch(entry ->
                    !level.hasChunkAt(entry.getKey()) || level.getBlockEntity(entry.getKey()) != entry.getValue()
                            || entry.getValue().getContentsVersion() != participantVersions.get(entry.getKey()))
                    || !canOutput(active.value().result()) || supplying.entrySet().stream().anyMatch(entry ->
                    !level.hasChunkAt(entry.getKey()) || level.getBlockEntity(entry.getKey()) != entry.getValue())) {
                cancel();
                return;
            }
            advance();
            return;
        }
        if (getInput().isEmpty() || scanIndex != 0 || scanCooldown == 0) return;
        var input = captureInput();
        if (input == null) return;
        var match = level.getRecipeManager().getAllRecipesFor(PoRecipes.ALTAR.type().get()).stream()
                .filter(holder -> holder.value().matches(input, level))
                .sorted(Comparator.comparing(holder -> holder.id().toString()))
                .findFirst().orElse(null);
        if (match == null || !canOutput(match.value().result())) return;
        long total = matchingJars(match.value().fluid()).stream()
                .mapToLong(jar -> jar.getTank().getFluidAmount()).sum();
        if (total < match.value().fluid().getAmount()) return;
        active = match;
        snapshot = currentStacks();
        participants.clear();
        participantVersions.clear();
        supplying.clear();
        for (GlassJarBlockEntity jar : matchingJars(match.value().fluid())) {
            supplying.put(jar.getBlockPos().immutable(), jar);
        }
        startInputVersion = inputVersion;
        for (int i = 0; i < PEDESTAL_OFFSETS.size(); i++) {
            if (!snapshot.get(i + 1).isEmpty()) {
                BlockPos pos = worldPosition.offset(PEDESTAL_OFFSETS.get(i));
                if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal) {
                    participants.put(pos, pedestal);
                    participantVersions.put(pos, pedestal.getContentsVersion());
                }
            }
        }
        consumed = 0;
        rateRemainder = 0;
        setChanged();
        sync();
    }

    private void scanJars() {
        if (scanCooldown > 0) {
            scanCooldown--;
            return;
        }
        for (int n = 0; n < SCAN_BUDGET && scanIndex < 49; n++, scanIndex++) {
            BlockPos pos = worldPosition.offset(scanIndex / 7 - 3, 0, scanIndex % 7 - 3);
            if (!level.hasChunkAt(pos)) {
                jars.remove(pos);
            } else if (level.getBlockEntity(pos) instanceof GlassJarBlockEntity jar) {
                jars.put(pos.immutable(), jar);
            } else {
                jars.remove(pos);
            }
        }
        if (scanIndex == 49) {
            scanIndex = 0;
            scanCooldown = SCAN_INTERVAL;
        }
    }

    private List<GlassJarBlockEntity> matchingJars(FluidStack required) {
        return jars.entrySet().stream()
                .filter(entry -> level.hasChunkAt(entry.getKey()) && level.getBlockEntity(entry.getKey()) == entry.getValue())
                .map(Map.Entry::getValue)
                .filter(jar -> FluidStack.isSameFluidSameComponents(jar.getTank().getFluid(), required)
                        && !jar.getTank().isEmpty())
                .sorted(Comparator.comparingLong(jar -> jar.getBlockPos().asLong()))
                .toList();
    }

    private void advance() {
        AltarRecipe recipe = active.value();
        List<GlassJarBlockEntity> available = matchingJars(recipe.fluid());
        if (available.isEmpty()) {
            cancel();
            return;
        }
        rateRemainder += (long) available.size() * recipe.fluid().getAmount();
        int budget = (int) Math.min(recipe.fluid().getAmount() - consumed, rateRemainder / recipe.duration());
        rateRemainder %= recipe.duration();
        supplying.clear();
        for (GlassJarBlockEntity jar : available) supplying.put(jar.getBlockPos().immutable(), jar);
        for (int i = 0; i < available.size(); i++) {
            GlassJarBlockEntity jar = available.get(i);
            if (budget <= 0) break;
            int amount = Math.min(budget, Math.max(1, (int) Math.ceil((double) budget / (available.size() - i))));
            FluidStack drained = jar.getTank().drain(recipe.fluid().copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                consumed += drained.getAmount();
                budget -= drained.getAmount();
            }
        }
        supplying.entrySet().removeIf(entry -> entry.getValue().getTank().isEmpty());
        if (consumed >= recipe.fluid().getAmount()) {
            complete(recipe);
        } else {
            setChanged();
            sync();
        }
    }

    private void complete(AltarRecipe recipe) {
        inventory.extractItem(INPUT, 1, false);
        for (BlockPos offset : PEDESTAL_OFFSETS) {
            BlockEntity entity = level.getBlockEntity(worldPosition.offset(offset));
            if (entity instanceof PedestalBlockEntity pedestal && !pedestal.getItem().isEmpty()) {
                pedestal.removeItem();
            }
        }
        if (!recipe.result().isEmpty()) {
            ItemStack output = getOutput();
            if (output.isEmpty()) inventory.setStackInSlot(OUTPUT, recipe.result().copy());
            else inventory.setStackInSlot(OUTPUT, output.copyWithCount(output.getCount() + recipe.result().getCount()));
        }
        cancel();
        if (level instanceof ServerLevel serverLevel) {
            if (!recipe.command().isBlank()) {
                serverLevel.getServer().getCommands().performPrefixedCommand(
                        serverLevel.getServer().createCommandSourceStack()
                                .withLevel(serverLevel)
                                .withPosition(Vec3.atCenterOf(worldPosition))
                                .withSuppressedOutput(),
                        recipe.command());
            }
            double y = worldPosition.getY() + AltarRitualAnimation.altarItemY(level.getGameTime());
            serverLevel.sendParticles(ParticleTypes.FIREWORK, worldPosition.getX() + 0.5D, y, worldPosition.getZ() + 0.5D,
                    12, 0.05D, 0.05D, 0.05D, 0.12D);
            serverLevel.playSound(null, worldPosition, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 0.65F, 1.1F);
        }
    }

    private boolean canOutput(ItemStack result) {
        if (result.isEmpty()) return true;
        ItemStack output = getOutput();
        return (output.isEmpty() || ItemStack.isSameItemSameComponents(output, result))
                && output.getCount() + result.getCount() <= Math.min(result.getMaxStackSize(), inventory.getSlotLimit(OUTPUT));
    }

    private @Nullable AltarRecipe.Input captureInput() {
        List<ItemStack> stacks = currentStacks();
        if (stacks.isEmpty()) return null;
        return new AltarRecipe.Input(stacks.getFirst(), stacks.subList(1, stacks.size()).stream().filter(s -> !s.isEmpty()).toList());
    }

    private List<ItemStack> currentStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(getInput().copy());
        for (BlockPos offset : PEDESTAL_OFFSETS) {
            BlockPos pos = worldPosition.offset(offset);
            if (!level.hasChunkAt(pos)) return List.of();
            if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal)
                stacks.add(pedestal.getItem().copy());
            else stacks.add(ItemStack.EMPTY);
        }
        return stacks;
    }

    private boolean inputsUnchanged() {
        List<ItemStack> current = currentStacks();
        return current.size() == snapshot.size() && ItemStack.listMatches(current, snapshot);
    }

    private void cancel() {
        active = null;
        pendingRitual = null;
        snapshot = List.of();
        supplying.clear();
        participants.clear();
        participantVersions.clear();
        consumed = 0;
        rateRemainder = 0;
        setChanged();
        sync();
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        if (pendingRitual != null) {
            tag.put("ritual", pendingRitual.copy());
        } else if (active != null) {
            CompoundTag ritual = new CompoundTag();
            ritual.putString("recipe", active.id().toString());
            ritual.putInt("consumed", consumed);
            ritual.putLong("remainder", rateRemainder);
            ritual.putBoolean("invalidated", inputVersion != startInputVersion
                    || participants.entrySet().stream().anyMatch(entry ->
                    entry.getValue().getContentsVersion() != participantVersions.get(entry.getKey())));
            ListTag items = new ListTag();
            for (ItemStack stack : snapshot) items.add(stack.saveOptional(registries));
            ritual.put("items", items);
            ListTag pedestals = new ListTag();
            for (var entry : participants.entrySet()) {
                CompoundTag item = new CompoundTag();
                item.putLong("pos", entry.getKey().asLong());
                item.putUUID("id", entry.getValue().getIdentity());
                item.putLong("version", participantVersions.get(entry.getKey()));
                pedestals.add(item);
            }
            ritual.put("pedestals", pedestals);
            ListTag sources = new ListTag();
            for (var entry : supplying.entrySet()) {
                CompoundTag item = new CompoundTag();
                item.putLong("pos", entry.getKey().asLong());
                item.putUUID("id", entry.getValue().getIdentity());
                sources.add(item);
            }
            ritual.put("sources", sources);
            tag.put("ritual", ritual);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        if (tag.contains("ritual", Tag.TAG_COMPOUND)) pendingRitual = tag.getCompound("ritual").copy();
        else pendingRitual = null;
        if (level != null && level.isClientSide) {
            float nextProgress = tag.getFloat("ritual_progress");
            long now = level.getGameTime();
            long elapsed = Math.max(1L, now - lastProgressSyncTick);
            clientProgressStep = nextProgress > lastSyncedProgress
                    ? (nextProgress - lastSyncedProgress) / elapsed : 0.0F;
            lastSyncedProgress = nextProgress;
            lastProgressSyncTick = now;
            if (nextProgress <= 0.0F) {
                clientProgress = 0.0F;
                clientProgressStep = 0.0F;
            }
            long[] positions = tag.getLongArray("ritual_jars");
            List<BlockPos> jarPositions = new ArrayList<>(positions.length);
            for (long pos : positions) jarPositions.add(BlockPos.of(pos));
            clientJars = List.copyOf(jarPositions);
            long[] pedestals = tag.getLongArray("ritual_pedestals");
            List<BlockPos> pedestalPositions = new ArrayList<>(pedestals.length);
            for (long pos : pedestals) pedestalPositions.add(BlockPos.of(pos));
            clientPedestals = List.copyOf(pedestalPositions);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = saveWithoutMetadata(registries);
        if (active != null) {
            double fractionalConsumption = (double) rateRemainder / active.value().duration();
            tag.putFloat("ritual_progress", Math.max(0.001F,
                    (float) ((consumed + fractionalConsumption) / active.value().fluid().getAmount())));
            tag.putLongArray("ritual_jars", supplying.keySet().stream().mapToLong(BlockPos::asLong).toArray());
            tag.putLongArray("ritual_pedestals", participants.keySet().stream().mapToLong(BlockPos::asLong).toArray());
        }
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @SuppressWarnings("unchecked")
    private boolean restoreRitual() {
        CompoundTag saved = pendingRitual;
        ListTag pedestals = saved.getList("pedestals", Tag.TAG_COMPOUND);
        ListTag sources = saved.getList("sources", Tag.TAG_COMPOUND);
        for (BlockPos offset : PEDESTAL_OFFSETS) {
            if (!level.hasChunkAt(worldPosition.offset(offset))) return false;
        }
        for (ListTag list : List.of(pedestals, sources)) {
            for (int i = 0; i < list.size(); i++) {
                BlockPos pos = BlockPos.of(list.getCompound(i).getLong("pos"));
                if (!level.hasChunkAt(pos)) return false;
            }
        }
        ResourceLocation id = ResourceLocation.tryParse(saved.getString("recipe"));
        RecipeHolder<?> holder = id == null ? null : level.getRecipeManager().byKey(id).orElse(null);
        if (saved.getBoolean("invalidated") || holder == null || !(holder.value() instanceof AltarRecipe recipe)
                || saved.getInt("consumed") < 0 || saved.getInt("consumed") >= recipe.fluid().getAmount()) {
            cancel();
            return true;
        }
        List<ItemStack> items = new ArrayList<>();
        ListTag savedItems = saved.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < savedItems.size(); i++) {
            items.add(ItemStack.parseOptional(level.registryAccess(), savedItems.getCompound(i)));
        }
        if (items.size() != 9 || !ItemStack.listMatches(items, currentStacks())
                || !recipe.matches(new AltarRecipe.Input(items.getFirst(),
                items.subList(1, items.size()).stream().filter(stack -> !stack.isEmpty()).toList()), level)) {
            cancel();
            return true;
        }
        participants.clear();
        participantVersions.clear();
        for (int i = 0; i < pedestals.size(); i++) {
            CompoundTag item = pedestals.getCompound(i);
            BlockPos pos = BlockPos.of(item.getLong("pos"));
            if (!item.hasUUID("id") || !(level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal)
                    || !pedestal.getIdentity().equals(item.getUUID("id"))
                    || pedestal.getContentsVersion() != item.getLong("version")) {
                cancel();
                return true;
            }
            participants.put(pos, pedestal);
            participantVersions.put(pos, pedestal.getContentsVersion());
        }
        supplying.clear();
        for (int i = 0; i < sources.size(); i++) {
            CompoundTag item = sources.getCompound(i);
            BlockPos pos = BlockPos.of(item.getLong("pos"));
            if (!item.hasUUID("id") || !(level.getBlockEntity(pos) instanceof GlassJarBlockEntity jar)
                    || !jar.getIdentity().equals(item.getUUID("id"))) {
                cancel();
                return true;
            }
            supplying.put(pos, jar);
            jars.put(pos, jar);
        }
        active = (RecipeHolder<AltarRecipe>) holder;
        snapshot = List.copyOf(items);
        startInputVersion = inputVersion;
        consumed = saved.getInt("consumed");
        rateRemainder = Math.max(0, saved.getLong("remainder"));
        pendingRitual = null;
        sync();
        return true;
    }
}
