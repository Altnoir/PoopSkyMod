package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.impl.creative.PoCreativeTabSection;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PoBlockBuilder<T extends Block, P> extends BlockBuilder<T, P> {
    private PoCreativeTabSection defaultCreativeSection;

    protected PoBlockBuilder(
            PoRegistrate owner,
            P parent,
            String name,
            BuilderCallback callback,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        super(owner, parent, name, callback, factory, BlockBehaviour.Properties::of);
    }

    static <T extends Block, P> PoBlockBuilder<T, P> create(
            PoRegistrate owner,
            P parent,
            String name,
            BuilderCallback callback,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        PoBlockBuilder<T, P> builder = new PoBlockBuilder<>(owner, parent, name, callback, factory);
        builder.defaultBlockstate().defaultLoot().defaultLang();
        return builder;
    }

    void defaultCreativeSection(PoCreativeTabSection section) {
        this.defaultCreativeSection = section;
    }

    @Override
    public BlockEntry<T> register() {
        BlockEntry<T> entry = super.register();
        if (defaultCreativeSection != null) {
            defaultCreativeSection.add(() -> entry.get().asItem().getDefaultInstance());
        }
        return entry;
    }

    @Override
    public PoRegistrate getOwner() {
        return (PoRegistrate) super.getOwner();
    }
}
