package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.creative.PoCreativeTabSection;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PoRegistrate extends AbstractRegistrate<PoRegistrate> {
    private PoCreativeTabSection defaultCreativeSection;
    private final Set<String> ignoredCreativeTabEntries = new HashSet<>();

    protected PoRegistrate(String modid) {
        super(modid);
    }

    public static PoRegistrate create(String modId) {
        PoRegistrate registrate = new PoRegistrate(modId);
        Optional<IEventBus> modEventBus = ModList.get()
                .getModContainerById(modId)
                .map(ModContainer::getEventBus);
        modEventBus.ifPresentOrElse(registrate::registerEventListeners,
                () -> PoopSky.LOGGER.error("Failed to register event listeners for mod {}", modId));
        return registrate;
    }

    public PoRegistrate defaultCreativeSection(PoCreativeTabSection section) {
        this.defaultCreativeSection = section;
        return this;
    }

    void ignoreCreativeTab(String name) {
        ignoredCreativeTabEntries.add(name);
    }

    @Override
    public <T extends Block, P> PoBlockBuilder<T, P> block(
            P parent,
            String name,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return (PoBlockBuilder<T, P>) this
                .entry(name, callback -> {
                    PoBlockBuilder<T, P> builder = PoBlockBuilder.create(this, parent, name, callback, factory);
                    if (defaultCreativeSection != null && !ignoredCreativeTabEntries.contains(name)) {
                        builder.defaultCreativeSection(defaultCreativeSection);
                    }
                    return builder;
                });
    }

    @Override
    public <T extends Block> PoBlockBuilder<T, PoRegistrate> block(
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return block(self(), currentName(), factory);
    }

    @Override
    public <T extends Block> PoBlockBuilder<T, PoRegistrate> block(
            String name,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return block(self(), name, factory);
    }

    @Override
    public <T extends Block, P> PoBlockBuilder<T, P> block(
            P parent,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return block(parent, currentName(), factory);
    }

    @Override
    public <T extends Item, P> PoItemBuilder<T, P> item(
            P parent,
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return (PoItemBuilder<T, P>) this
                .<Item, T, P, ItemBuilder<T, P>>entry(name, callback -> {
                    PoItemBuilder<T, P> builder = PoItemBuilder.create(this, parent, name, callback, factory);
                    if (defaultCreativeSection != null && !ignoredCreativeTabEntries.contains(name)) {
                        builder.defaultCreativeSection(defaultCreativeSection);
                    }
                    return builder;
                });
    }

    @Override
    public <T extends Item> PoItemBuilder<T, PoRegistrate> item(
            NonNullFunction<Item.Properties, T> factory
    ) {
        return item(self(), currentName(), factory);
    }

    @Override
    public <T extends Item> PoItemBuilder<T, PoRegistrate> item(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return item(self(), name, factory);
    }

    @Override
    public <T extends Item, P> PoItemBuilder<T, P> item(
            P parent,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return item(parent, currentName(), factory);
    }
}
