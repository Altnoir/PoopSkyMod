package com.altnoir.poopsky.fabric.port.fluidhandler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Fabric-side port of NeoForge's {@code FluidStack}. A stack consists of a fluid,
 * an amount in droplets, and an optional data-component patch.
 */
public final class FluidStack implements DataComponentHolder {
    public static final FluidStack EMPTY = new FluidStack(null);

    public static final Codec<Holder<Fluid>> FLUID_NON_EMPTY_CODEC = BuiltInRegistries.FLUID.holderByNameCodec()
            .validate(holder -> holder.is(Fluids.EMPTY.builtInRegistryHolder())
                    ? DataResult.error(() -> "Fluid must not be minecraft:empty")
                    : DataResult.success(holder));

    public static final Codec<FluidStack> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
                    FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter(FluidStack::getFluidHolder),
                    ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidStack::getAmount),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(stack -> stack.components.asPatch()))
            .apply(instance, FluidStack::new)));

    public static final Codec<FluidStack> OPTIONAL_CODEC = ExtraCodecs.optionalEmptyMap(CODEC)
            .xmap(optional -> optional.orElse(EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));

    private int amount;
    @Nullable
    private final Fluid fluid;
    private final PatchedDataComponentMap components;

    public FluidStack(Holder<Fluid> fluid, int amount, DataComponentPatch patch) {
        this(fluid.value(), amount, PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, patch));
    }

    public FluidStack(Holder<Fluid> fluid, int amount) {
        this(fluid.value(), amount);
    }

    public FluidStack(Fluid fluid, int amount) {
        this(fluid, amount, new PatchedDataComponentMap(DataComponentMap.EMPTY));
    }

    private FluidStack(Fluid fluid, int amount, PatchedDataComponentMap components) {
        this.fluid = fluid;
        this.amount = amount;
        this.components = components;
    }

    private FluidStack(@Nullable Void unused) {
        this.fluid = null;
        this.amount = 0;
        this.components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
    }

    public static Optional<FluidStack> parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).result();
    }

    public static FluidStack parseOptional(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        return tag.isEmpty() ? EMPTY : parse(lookupProvider, tag).orElse(EMPTY);
    }

    public boolean isEmpty() {
        return this == EMPTY || this.fluid == null || this.fluid == Fluids.EMPTY || this.amount <= 0;
    }

    public FluidStack split(int amount) {
        int splitAmount = Math.min(amount, this.amount);
        FluidStack result = copyWithAmount(splitAmount);
        shrink(splitAmount);
        return result;
    }

    public FluidStack copyAndClear() {
        if (isEmpty()) {
            return EMPTY;
        }
        FluidStack result = copy();
        setAmount(0);
        return result;
    }

    public Fluid getFluid() {
        return isEmpty() ? Fluids.EMPTY : this.fluid;
    }

    public Holder<Fluid> getFluidHolder() {
        return getFluid().builtInRegistryHolder();
    }

    public boolean is(TagKey<Fluid> tag) {
        return getFluidHolder().is(tag);
    }

    public boolean is(Fluid fluid) {
        return getFluid() == fluid;
    }

    public boolean is(Holder<Fluid> holder) {
        return is(holder.value());
    }

    public boolean is(HolderSet<Fluid> holderSet) {
        return holderSet.contains(getFluidHolder());
    }

    public boolean is(Predicate<Holder<Fluid>> holderPredicate) {
        return holderPredicate.test(getFluidHolder());
    }

    public Stream<TagKey<Fluid>> getTags() {
        return getFluidHolder().tags();
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot encode empty FluidStack");
        }
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public Tag saveOptional(HolderLookup.Provider lookupProvider) {
        return isEmpty() ? new CompoundTag() : save(lookupProvider);
    }

    public FluidStack copy() {
        return isEmpty() ? EMPTY : new FluidStack(this.fluid, this.amount, this.components.copy());
    }

    public FluidStack copyWithAmount(int amount) {
        if (isEmpty()) {
            return EMPTY;
        }
        FluidStack copy = copy();
        copy.setAmount(amount);
        return copy;
    }

    public static boolean matches(FluidStack first, FluidStack second) {
        return first == second || first.getAmount() == second.getAmount() && isSameFluidSameComponents(first, second);
    }

    public static boolean isSameFluid(FluidStack first, FluidStack second) {
        return first.is(second.getFluid());
    }

    public static boolean isSameFluidSameComponents(FluidStack first, FluidStack second) {
        if (!isSameFluid(first, second)) {
            return false;
        }
        return first.isEmpty() && second.isEmpty() || Objects.equals(first.components, second.components);
    }

    public int getAmount() {
        return isEmpty() ? 0 : this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void limitSize(int amount) {
        if (!isEmpty() && getAmount() > amount) {
            setAmount(amount);
        }
    }

    public void grow(int amount) {
        setAmount(getAmount() + amount);
    }

    public void shrink(int amount) {
        grow(-amount);
    }

    @Override
    public PatchedDataComponentMap getComponents() {
        return components;
    }

    public DataComponentPatch getComponentsPatch() {
        return isEmpty() ? DataComponentPatch.EMPTY : components.asPatch();
    }

    @Nullable
    public <T> T set(DataComponentType<? super T> type, @Nullable T component) {
        return components.set(type, component);
    }

    @Nullable
    public <T> T remove(DataComponentType<? extends T> type) {
        return components.remove(type);
    }

    public void applyComponents(DataComponentPatch patch) {
        components.applyPatch(patch);
    }

    public void applyComponents(DataComponentMap components) {
        this.components.setAll(components);
    }

    @Override
    public String toString() {
        return getAmount() + " " + getFluid();
    }
}
