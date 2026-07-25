package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.creative.PoCreativeTabSection;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class PoRegistrate extends AbstractRegistrate<PoRegistrate> {
    private final Set<String> ignoredCreativeTabEntries = new HashSet<>();
    private ResourceKey<CreativeModeTab> defaultCreativeTab;
    private PoCreativeTabSection defaultCreativeSection;

    protected PoRegistrate(String modid) {
        super(modid);
    }

    public static PoRegistrate create(String modId) {
        var ret = new PoRegistrate(modId);
        Optional<IEventBus> modEventBus = ModList.get().getModContainerById(modId).map(ModContainer::getEventBus);
        modEventBus.ifPresentOrElse(ret::registerEventListeners, () -> {
            PoopSky.LOGGER.error("Failed to register eventListeners for mod {}", modId);
        });
        return ret;
    }

    @Override
    public PoRegistrate defaultCreativeTab(ResourceKey<CreativeModeTab> creativeModeTab) {
        defaultCreativeTab = creativeModeTab;
        defaultCreativeSection = null;
        return super.defaultCreativeTab(creativeModeTab);
    }

    public PoRegistrate defaultCreativeSection(PoCreativeTabSection section) {
        defaultCreativeTab = section.tab();
        defaultCreativeSection = section;
        return super.defaultCreativeTab(section.tab());
    }

    @Override
    public <T extends Item, P> ItemBuilder<T, P> item(
            P parent,
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return this
                .<Item, T, P, ItemBuilder<T, P>>entry(name, callback -> {
                    ItemBuilder<T, P> builder = ItemBuilder.create(this, parent, name, callback, factory);
                    if (defaultCreativeTab != null && !ignoredCreativeTabEntries.contains(name)) {
                        builder.tab(defaultCreativeTab);
                        if (defaultCreativeSection != null) {
                            defaultCreativeSection.add(PoopSky.loc(name));
                        }
                    }
                    return builder;
                });
    }

    @Override
    public <T extends Item> ItemBuilder<T, PoRegistrate> item(
            NonNullFunction<Item.Properties, T> factory
    ) {
        return item(self(), currentName(), factory);
    }

    @Override
    public <T extends Item> ItemBuilder<T, PoRegistrate> item(
            String name,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return item(self(), name, factory);
    }

    @Override
    public <T extends Item, P> ItemBuilder<T, P> item(
            P parent,
            NonNullFunction<Item.Properties, T> factory
    ) {
        return item(parent, currentName(), factory);
    }

    @Override
    public <T extends Block, P> BlockBuilder<T, P> block(
            P parent,
            String name,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return this
                .<Block, T, P, BlockBuilder<T, P>>entry(
                        name,
                        callback -> BlockBuilder.create(this, parent, name, callback, factory)
                );
    }

    @Override
    public <T extends Block> BlockBuilder<T, PoRegistrate> block(
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return block(self(), currentName(), factory);
    }

    @Override
    public <T extends Block> BlockBuilder<T, PoRegistrate> block(
            String name,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return block(self(), name, factory);
    }

    @Override
    public <T extends Block, P> BlockBuilder<T, P> block(
            P parent,
            NonNullFunction<BlockBehaviour.Properties, T> factory
    ) {
        return block(parent, currentName(), factory);
    }

    void ignoreCreativeTab(String name) {
        ignoredCreativeTabEntries.add(name);
    }

    Optional<ResourceKey<CreativeModeTab>> defaultCreativeTabKey() {
        return Optional.ofNullable(defaultCreativeTab);
    }
}
