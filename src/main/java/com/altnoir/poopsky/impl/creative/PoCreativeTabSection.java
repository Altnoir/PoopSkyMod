package com.altnoir.poopsky.impl.creative;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class PoCreativeTabSection {
    private final Component title;
    private final @Nullable ResourceLocation bannerSprite;
    private final List<Supplier<ItemStack>> entries = new ArrayList<>();

    public PoCreativeTabSection(Component title) {
        this(title, null);
    }

    public PoCreativeTabSection(Component title, @Nullable ResourceLocation bannerSprite) {
        this.title = title;
        this.bannerSprite = bannerSprite;
    }

    public Component title() {
        return title;
    }

    public Optional<ResourceLocation> bannerSprite() {
        return Optional.ofNullable(bannerSprite);
    }

    public void add(ItemLike item) {
        add(() -> item.asItem().getDefaultInstance());
    }

    public void add(Supplier<ItemStack> stack) {
        entries.add(stack);
    }

    public void clear() {
        entries.clear();
    }

    public List<ItemStack> itemStacks() {
        return entries.stream()
                .map(Supplier::get)
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
    }
}
