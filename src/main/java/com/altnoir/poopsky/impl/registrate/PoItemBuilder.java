package com.altnoir.poopsky.impl.registrate;

import com.altnoir.poopsky.impl.creative.PoCreativeTabSection;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.item.Item;

public class PoItemBuilder<T extends Item, P> extends ItemBuilder<T, P> {
    private PoCreativeTabSection defaultCreativeSection;

    protected PoItemBuilder(
            PoRegistrate owner,
            P parent,
            String name,
            BuilderCallback callback,
            NonNullFunction<Item.Properties, T> factory
    ) {
        super(owner, parent, name, callback, factory);
    }

    static <T extends Item, P> PoItemBuilder<T, P> create(
            PoRegistrate owner,
            P parent,
            String name,
            BuilderCallback callback,
            NonNullFunction<Item.Properties, T> factory
    ) {
        PoItemBuilder<T, P> builder = new PoItemBuilder<>(owner, parent, name, callback, factory);
        builder.defaultModel().defaultLang();
        return builder;
    }

    void defaultCreativeSection(PoCreativeTabSection section) {
        this.defaultCreativeSection = section;
    }

    /** Removes this item from the currently configured default creative tab. */
    public PoItemBuilder<T, P> ignore() {
        getOwner().ignoreCreativeTab(getName());
        return this;
    }

    public PoItemBuilder<T, P> addTabSection(PoCreativeTabSection section) {
        section.add(() -> getEntry().getDefaultInstance());
        return this;
    }

    @Override
    public ItemEntry<T> register() {
        ItemEntry<T> entry = super.register();
        if (defaultCreativeSection != null) {
            defaultCreativeSection.add(() -> entry.get().getDefaultInstance());
        }
        return entry;
    }

    @Override
    public PoRegistrate getOwner() {
        return (PoRegistrate) super.getOwner();
    }
}
